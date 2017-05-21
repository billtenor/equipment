package billtenor.graduation.datacustomization;

import billtenor.graduation.datacustomization.dataType.KafkaSpoutConfig;
import billtenor.graduation.datacustomization.tableType.localFile.LocalJSON;
import billtenor.graduation.datacustomization.topology.LocalTopologyCreator;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.topology.TopologyBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

/**
 * Created by lyj on 17-3-13.
 */
public class DataConsumer implements java.io.Serializable{
    private String zkConnString;
    private String zkKafkaPrefix;
    private String topicName;
    private String kafkaBrokerHosts;

    public DataConsumer(String zkConnString, String zkKafkaPrefix, String topicName, String kafkaBrokerHosts){
        this.zkConnString=zkConnString;
        this.zkKafkaPrefix=zkKafkaPrefix;
        this.topicName=topicName;
        this.kafkaBrokerHosts=kafkaBrokerHosts;
    }

    private StormTopology buildConsumerTopology(JSONObject topologyConfig) {

        String dataWarehouseModelID = (String)topologyConfig.get("dataWarehouseModelID");
        String dataSourceModelID = (String)topologyConfig.get("dataSourceModelID");
        String dataModelID = (String)topologyConfig.get("dataModelID");
        JSONArray factsTableIDsJOSN = (JSONArray)topologyConfig.get("factsTableIDs");
        String[] factsTableIDs=new String[factsTableIDsJOSN.size()];
        Long spaceAggregateWaitTime=(Long)topologyConfig.get("spaceAggregateWaitTime");
        Long noBlockTimeVPT=(Long)topologyConfig.get("noBlockTimeVPT");
        Map<String,Integer> parallel=new HashMap<>();
        parallel.put("spout",((Long)topologyConfig.get("parallel_spout")).intValue());
        parallel.put("transform",((Long)topologyConfig.get("parallel_transform")).intValue());
        parallel.put("aggregate",((Long)topologyConfig.get("parallel_aggregate")).intValue());
        parallel.put("save",((Long)topologyConfig.get("parallel_save")).intValue());
        Long dataSaveWindowCount=(Long)topologyConfig.get("dataSaveWindowCount");
        Long dataSaveRetryCount=(Long)topologyConfig.get("dataSaveRetryCount");

        for(int i=0;i<factsTableIDsJOSN.size();i++){
            factsTableIDs[i]=(String)factsTableIDsJOSN.get(i);
        }
        LocalTopologyCreator topologyCreator = new LocalTopologyCreator(
                dataWarehouseModelID,
                dataSourceModelID,
                dataModelID,
                factsTableIDs,
                new KafkaSpoutConfig(zkConnString,zkKafkaPrefix,topicName),
                spaceAggregateWaitTime,noBlockTimeVPT,
                kafkaBrokerHosts,
                parallel,
                dataSaveWindowCount.intValue(),dataSaveRetryCount.intValue()
        );


        TopologyBuilder builder = topologyCreator.buildConsumerTopology();

        //test from kafka
        /**
         * The output field of the RandomSentenceSpout ("word") is provided as the boltMessageField
         * so that this gets written out as the message in the kafka topic.
        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,this.kafkaBrokerHosts);
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.CLIENT_ID_CONFIG, "storm-kafka-producer");
        KafkaBolt bolt = new KafkaBolt().withProducerProperties(prop)
                .withTopicSelector(new DefaultTopicSelector("consumer"))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper("key", "data"));
        builder.setBolt("forwardToKafka", bolt, 1).shuffleGrouping("timeBlockAggregate_1");
         */
        return builder.createTopology();
    }

    public static void main(String[] args) throws Exception {
        if(args.length==1){
            LocalJSON stringInput = new LocalJSON(args[0],true);
            JSONParser parser=new JSONParser();
            try{
                JSONObject jsonInput = (JSONObject)parser.parse(stringInput.data);

                String zkConnString = (String)jsonInput.get("zkConnString");
                String zkKafkaPrefix = (String)jsonInput.get("zkKafkaPrefix");
                String topicName = (String)jsonInput.get("topicName");
                String kafkaBrokerHosts = (String)jsonInput.get("kafkaBrokerHosts");
                String topologyName = (String)jsonInput.get("topologyName");
                Long NumWorkers = (Long)jsonInput.get("NumWorkers");
                JSONObject topologyConfig = (JSONObject)jsonInput.get("topologyConfig");
                Boolean debug;
                if(jsonInput.containsKey("debug")){
                    debug=true;
                }
                else{
                    debug=false;
                }
                DataConsumer self = new DataConsumer(zkConnString, zkKafkaPrefix, topicName, kafkaBrokerHosts);
                Config conf = new Config();
                if(!debug) {
                    conf.setMaxSpoutPending(200000);
                    conf.setNumWorkers(NumWorkers.intValue());
                    // submit the consumer topology.
                    StormSubmitter.submitTopologyWithProgressBar(
                            topologyName, conf,
                            self.buildConsumerTopology(topologyConfig)
                    );
                }else {
                    conf.setDebug(false);
                    conf.setMaxTaskParallelism(5);
                    System.out.println("Using Kafka zookeeper url: " + zkConnString + " Kafka zookeeper prefix: " + zkKafkaPrefix + " Kafka topic:" + topicName);

                    LocalCluster cluster = new LocalCluster();
                    cluster.submitTopology(
                            topicName, conf,
                            self.buildConsumerTopology(topologyConfig)
                    );
                    //Thread.sleep(15000);
                    //cluster.shutdown();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
