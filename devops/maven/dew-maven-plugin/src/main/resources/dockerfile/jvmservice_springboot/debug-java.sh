#!/bin/sh

export JAVA_ENABLE_DEBUG=true
export JAVA_OPTIONS="${JAVA_DEBUG_OPTIONS}"

pidof java | awk '{print $1}' | xargs kill -9

./run-java.sh
