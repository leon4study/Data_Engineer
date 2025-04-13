#!/bin/sh

./success.sh

if [ $? -ne 0 ]; then
        echo "failed from sub command"
        exit 1
fi

sleep 100 &
echo "background sleep pid: $!"

echo "next step"
