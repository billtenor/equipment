#!/bin/bash
case "$1" in
    DataSimulator)
    /usr/local/storm/bin/storm jar ../target/storm-kafka-1.0.0.jar billtenor.graduation.datacustomization.DataSimulator simulator.json
    #192.168.100.12:2181,192.168.100.13:2181,192.168.100.14:2181/kafka/q-m1f1lxxv 192.168.100.47:9092,192.168.100.44:9092
    ;;
    DataConsumer)
    /usr/local/storm/bin/storm jar ../target/storm-kafka-1.0.0.jar billtenor.graduation.datacustomization.DataConsumer consumer.json
    #192.168.100.12:2181,192.168.100.13:2181,192.168.100.14:2181 /kafka/q-m1f1lxxv test
    ;;
    *)
    echo "Usage: {DataSimulator|DataConsumer}"
esac
