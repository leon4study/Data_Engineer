#!/bin/sh

echo "PID: $$"
echo "File Name: $0"
echo "First Param: $1"
echo "Second Param: $2"
echo "Quoted Values @: $@"
echo "quoted Values *: $*"
echo "Total Num of Params: $#"

sleep 100
