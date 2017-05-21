#!/bin/bash
ID=$1
PID=$2
cd `dirname $0`
#DIR=`pwd`
LOG="log/${ID}"
if [ $PID -eq 0 ];then
    cmd=$3
    daemon_cmd="${cmd} > ${LOG} 2>&1 &"
    eval $daemon_cmd
    echo $!
    exit 1
else
    line=$3
    now=`wc -l ${LOG}|awk '{print $1}'`
    for i in $( seq $line $now )
    do
        sed -n ${i}p ${LOG}
    done
    kill -0 $PID > /dev/null 2>&1
    status=$?
    if [ $status -eq 0 ];then
        exit 1
    else
        exit 0
    fi
fi
