#!/bin/sh

OPTION="${1}"
case ${OPTION} in
    -f) FILE="${2}"
        echo "FILE Name is $FILE"
        ;;
    -d) DIR="${2}"
        echo "Dir name is $DIR"
        ;;
    [0-9]) NUM="${1}"
        echo "number is $NUM"
        ;;
    *)
        echo "`basename ${0}`: usage: [-f file] | [-d directory]"
        exit 1
        ;;
esac
