#!/bin/bash
server=$1
if [ $# -lt 1 ];then
    echo error!
else
    scp $server:/etc/hosts ./
    scp $server:/opt/hadoop/etc/hadoop/* /usr/local/hadoop/etc/hadoop/
fi
