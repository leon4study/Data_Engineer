#!/bin/sh
echo "a is first param, b is second param"
echo "a + b = $(expr $1 + $2)"
echo "a - b = $(expr $1 - $2)"
echo "a * b = $(expr $1 \* $2)"
echo "a / b = $(expr $1 / $2)"
echo "a % b = $(expr $1 % $2)"

if [ $1 = $2 ]; then
    echo "a == b"
elif [ $1 != $2 ]; then
    echo "a != b"
fi
