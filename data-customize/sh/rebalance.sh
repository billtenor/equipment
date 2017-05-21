#!/bin/bash
case "$1" in
    DataSimulator)
    /usr/local/storm/bin/storm rebalance performanceTestSimulator -n 10
    #-e spout=20 -e forwardToKafka=20
    ;;  
    DataConsumer)
    /usr/local/storm/bin/storm rebalance performanceTestConsumer -n 10
    #-e kafkaSpout_1=20 -e measureTransform_1=5 -e spaceBlockAggregate_1=40 -e dataSave_1=2
    ;;  
    *)  
    echo "Usage: {DataSimulator|DataConsumer}"
esac

