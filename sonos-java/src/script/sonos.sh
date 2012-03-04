#!/bin/sh

SCRIPT_NAME=$0
CURRENT_PATH=`dirname "${SCRIPT_NAME}"`
LIB_PATH="${CURRENT_PATH}/lib/"
MAIN_JAR=`ls -1 "${LIB_PATH}" 2>/dev/null | grep "sonos-java"`
java -jar "${LIB_PATH}/${MAIN_JAR}" $*
