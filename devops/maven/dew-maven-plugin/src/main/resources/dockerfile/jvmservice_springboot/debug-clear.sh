#!/bin/sh

pidof java | awk '{print $1}' | xargs kill -9