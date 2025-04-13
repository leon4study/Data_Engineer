#!/bin/sh
if [ !false ]; then
    echo "not false is true"
fi

if [ $1 -gt $2 -o $1 -eq $2 ]; then
    echo "$1 is greter than or equal to $2"
fi

if [ $1 -gt $2 -a $1 -lt $2 ]; then
    echo "never happend"
fi
