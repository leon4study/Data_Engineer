#!/bin/sh
myfunc(){
    echo $1
    echo $2
    echo `expr $1 + $2`
}

myfunc 2 4

echo "$1 $2 are shell param"
