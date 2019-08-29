#!/bin/sh

ps -ef | grep java | grep address=5005 | awk '{print $1}' | xargs kill -9
