#!/bin/sh

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

java_options() {
  echo "${JAVA_OPTIONS:-} $(debug_options)" | awk '$1=$1'
}

run() {
  echo exec java $(java_options) -jar ./serv.jar
  exec java $(java_options) -jar ./serv.jar
}

run