#!/bin/sh

export JAVA_ENABLE_DEBUG=true
export JAVA_OPTIONS=${JAVA_DEBUG_OPTIONS}

ps -ef | grep java | grep address=5005 | awk '{print $1}' | xargs kill -9

./run-java.sh
