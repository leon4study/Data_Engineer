#!/bin/sh
echo '$* without quotes:'
./arg-callee.sh $*


echo '$@ without quotes:'
./arg-callee.sh $@


echo '$* with quotes:'
./arg-callee.sh "$*"


echo '$@ with quotes:'
./arg-callee.sh "$@"
