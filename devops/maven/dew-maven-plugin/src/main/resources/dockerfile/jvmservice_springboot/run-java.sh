#!/bin/bash

#
# Copyright 2022. the original author or authors
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# This script is modified from https://github.com/fabric8io-images/run-java-sh

# ===================================================================================
# JAVA_OPTIONS: Checked for already set options
# JAVA_MAX_MEM_RATIO: Ratio use to calculate a default maximum Memory, in percent.
#                     E.g. the "50" value implies that 50% of the Memory
#                     given to the container is used as the maximum heap memory with
#                     '-Xmx'.
#                     It defaults to "25" when the maximum amount of memory available
#                     to the container is below 300M, otherwise defaults to "50".
#                     It is a heuristic and should be better backed up with real
#                     experiments and measurements.
#                     For a good overviews what tuning options are available -->
#                             https://youtu.be/Vt4G-pHXfs4
#                             https://www.youtube.com/watch?v=w1rZOY5gbvk
#                             https://vimeo.com/album/4133413/video/181900266
# Also note that heap is only a small portion of the memory used by a JVM. There are lot
# of other memory areas (metadata, thread, code cache, ...) which addes to the overall
# size. When your container gets killed because of an OOM, then you should tune
# the absolute values.
# JAVA_INIT_MEM_RATIO: Ratio use to calculate a default intial heap memory, in percent.
#                      By default this value is not set.
#

# Fail on a single failed command in a pipeline (if supported)
(set -o | grep -q pipefail) && set -o pipefail

# Fail on error and undefined vars
set -eu

# Generic formula evaluation based on awk
calc() {
  local formula="$1"
  shift
  echo "$@" | awk '
    function ceil(x) {
      return x % 1 ? int(x) + 1 : x
    }
    function log2(x) {
      return log(x)/log(2)
    }
    function max2(x, y) {
      return x > y ? x : y
    }
    function round(x) {
      return int(x + 0.5)
    }
    {print '"int(${formula})"'}
  '
}

# Based on the cgroup limits, figure out the max number of core we should utilize
core_limit() {
  local cpu_period_file="/sys/fs/cgroup/cpu/cpu.cfs_period_us"
  local cpu_quota_file="/sys/fs/cgroup/cpu/cpu.cfs_quota_us"
  if [ -r "${cpu_period_file}" ]; then
    local cpu_period="$(cat ${cpu_period_file})"

    if [ -r "${cpu_quota_file}" ]; then
      local cpu_quota="$(cat ${cpu_quota_file})"
      # cfs_quota_us == -1 --> no restrictions
      if [ ${cpu_quota:-0} -ne -1 ]; then
        echo $(calc 'ceil($1/$2)' "${cpu_quota}" "${cpu_period}")
      fi
    fi
  fi
}

max_memory() {
  # High number which is the max limit until which memory is supposed to be
  # unbounded.
  local mem_file="/sys/fs/cgroup/memory/memory.limit_in_bytes"
  if [ -r "${mem_file}" ]; then
    local max_mem_cgroup="$(cat ${mem_file})"
    local max_mem_meminfo_kb="$(cat /proc/meminfo | awk '/MemTotal/ {print $2}')"
    local max_mem_meminfo="$(expr $max_mem_meminfo_kb \* 1024)"
    if [ ${max_mem_cgroup:-0} != -1 ] && [ ${max_mem_cgroup:-0} -lt ${max_mem_meminfo:-0} ]
    then
      echo "${max_mem_cgroup}"
    fi
  fi
}

init_limit_env_vars() {
  # Read in container limits and export the as environment variables
  local core_limit="$(core_limit)"
  if [ -n "${core_limit}" ]; then
    export CONTAINER_CORE_LIMIT="${core_limit}"
  fi

  local mem_limit="$(max_memory)"
  if [ -n "${mem_limit}" ]; then
    export CONTAINER_MAX_MEMORY="${mem_limit}"
  fi
}

debug_options() {
  if [ -n "${JAVA_ENABLE_DEBUG:-}" ] || [ -n "${JAVA_DEBUG_ENABLE:-}" ] ||  [ -n "${JAVA_DEBUG:-}" ]; then
    local debug_port="${JAVA_DEBUG_PORT:-5005}"
    local suspend_mode="n"
    if [ -n "${JAVA_DEBUG_SUSPEND:-}" ]; then
      if ! echo "${JAVA_DEBUG_SUSPEND}" | grep -q -e '^\(false\|n\|no\|0\)$'; then
        suspend_mode="y"
      fi
    fi
    echo "-agentlib:jdwp=transport=dt_socket,server=y,suspend=${suspend_mode},address=${debug_port}"
  fi
}


# ==========================================================================

memory_options() {
  echo "$(calc_init_memory) $(calc_max_memory)"
  return
}

# Check for memory options and set max heap size if needed
calc_max_memory() {
  # Check whether -Xmx is already given in JAVA_OPTIONS
  if echo "${JAVA_OPTIONS:-}" | grep -q -- "-Xmx"; then
    return
  fi

  if [ -z "${CONTAINER_MAX_MEMORY:-}" ]; then
    return
  fi

  # Check for the 'real memory size' and calculate Xmx from the ratio
  if [ -n "${JAVA_MAX_MEM_RATIO:-}" ]; then
    if [ "${JAVA_MAX_MEM_RATIO}" -eq 0 ]; then
      # Explicitely switched off
      return
    fi
    calc_mem_opt "${CONTAINER_MAX_MEMORY}" "${JAVA_MAX_MEM_RATIO}" "mx"
  # When JAVA_MAX_MEM_RATIO not set and JVM >= 10 no max_memory
  elif [ "${JAVA_MAJOR_VERSION:-0}" -ge "10" ]; then
    return
  elif [ "${CONTAINER_MAX_MEMORY}" -le 314572800 ]; then
    # Restore the one-fourth default heap size instead of the one-half below 300MB threshold
    # See https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/parallel.html#default_heap_size
    calc_mem_opt "${CONTAINER_MAX_MEMORY}" "25" "mx"
  else
    calc_mem_opt "${CONTAINER_MAX_MEMORY}" "50" "mx"
  fi
}

# Check for memory options and set initial heap size if requested
calc_init_memory() {
  # Check whether -Xms is already given in JAVA_OPTIONS.
  if echo "${JAVA_OPTIONS:-}" | grep -q -- "-Xms"; then
    return
  fi

   # Check for the 'real memory size' and calculate Xms from the ratio
  if [ -n "${JAVA_INIT_MEM_RATIO:-}" ]; then
    if [ "${JAVA_INIT_MEM_RATIO}" -eq 0 ]; then
      # Explicitely switched off
      return
    fi
  else
    JAVA_INIT_MEM_RATIO=100
  fi

  local xmx=`echo  $(calc_max_memory) | tr -cd "[0-9]"`
  if [[ ${xmx} ]]; then
    calc_mem_opt $(($xmx*1048576)) "${JAVA_INIT_MEM_RATIO}" "ms"
  fi
}

calc_mem_opt() {
  local max_mem="$1"
  local fraction="$2"
  local mem_opt="$3"

  local val=$(calc 'round($1*$2/100/1048576)' "${max_mem}" "${fraction}")
  echo "-X${mem_opt}${val}m"
}

cpu_options() {
  # JVMs >= 10 know about CPU limits
  if [ "${JAVA_MAJOR_VERSION:-0}" -ge "10" ]; then
    return
  fi

  if [ -n "${CONTAINER_CORE_LIMIT:-}" ]; then
    echo "-XX:ParallelGCThreads=${CONTAINER_CORE_LIMIT} " \
         "-XX:ConcGCThreads=${CONTAINER_CORE_LIMIT} " \
         "-Djava.util.concurrent.ForkJoinPool.common.parallelism=${CONTAINER_CORE_LIMIT} "
  fi
}

#-XX:MinHeapFreeRatio=20  These parameters tell the heap to shrink aggressively and to grow conservatively.
#-XX:MaxHeapFreeRatio=40  Thereby optimizing the amount of memory available to the operating system.
heap_ratio() {
  echo "-XX:MinHeapFreeRatio=40 -XX:MaxHeapFreeRatio=70"
}

# These parameters are necessary when running parallel GC if you want to use the Min and Max Heap Free ratios.
# Skip setting gc_options if any other GC is set in JAVA_OPTIONS.
# -XX:GCTimeRatio=4
# -XX:AdaptiveSizePolicyWeight=90
gc_options() {
  if echo "${JAVA_OPTIONS:-}" | grep -q -- "-XX:.*Use.*GC"; then
    return
  fi

  local opts=""
  # for JVMs < 10 set GC settings
  if [ -z "${JAVA_MAJOR_VERSION:-}" ] || [ "${JAVA_MAJOR_VERSION:-0}" -lt "10" ]; then
    opts="${opts} -XX:+UseParallelGC -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 $(heap_ratio)"
  fi
  if [ -z "${JAVA_MAJOR_VERSION:-}" ] || [ "${JAVA_MAJOR_VERSION:-}" != "7" ]; then
    opts="${opts} -XX:+ExitOnOutOfMemoryError"
  fi
  echo $opts
}

java_default_options() {
  # Echo options, trimming trailing and multiple spaces
  echo "$(memory_options) $(cpu_options) $(gc_options)" | awk '$1=$1'

}

# Combine all java options
java_options() {
  # Normalize spaces with awk (i.e. trim and elimate double spaces)
  # See e.g. https://www.physicsforums.com/threads/awk-1-1-1-file-txt.658865/ for an explanation
  # of this awk idiom
  echo "${JAVA_OPTIONS:-} $(debug_options) $(java_default_options)" | awk '$1=$1'
}

run() {
  echo exec java $(java_options) org.springframework.boot.loader.JarLauncher
  exec java $(java_options) org.springframework.boot.loader.JarLauncher
}

init_limit_env_vars

run
