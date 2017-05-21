package billtenor.graduation.datacustomization;

import billtenor.graduation.datacustomization.spout.DataSimulatorSpout;
import billtenor.graduation.datacustomization.tableType.localFile.LocalJSON;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.topology.TopologyBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Properties;

/**
 * Created by lyj on 17-2-9.
 */
public class DataSimulator {
    private String brokerUrl;

    DataSimulator(String brokerUrl){
        this.brokerUrl=brokerUrl;
    }

    StormTopology buildProducerTopology(Properties prop,String topic,JSONObject topologyConfig) {

        String dataSourceModelID = (String)topologyConfig.get("dataSourceModelID");
        Long parallel=(Long)topologyConfig.get("parallel");

        TopologyBuilder builder = new TopologyBuilder();
        DataSimulatorSpout spout=new DataSimulatorSpout(dataSourceModelID);
        builder.setSpout("spout", spout, parallel);
        /**
         * The output field of the RandomSentenceSpout ("word") is provided as the boltMessageField
         * so that this gets written out as the message in the kafka topic.
         */
        KafkaBolt bolt = new KafkaBolt().withProducerProperties(prop)
                .withTopicSelector(new DefaultTopicSelector(topic))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper("key", "jsonSpout"));
        builder.setBolt("forwardToKafka", bolt, parallel).shuffleGrouping("spout");
        return builder.createTopology();
    }
    Properties getProducerConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "storm-kafka-producer");
        return props;
    }

    public static void main(String[] args) throws Exception {
        if(args.length==1){
            LocalJSON stringInput = new LocalJSON(args[0],true);
            JSONParser parser=new JSONParser();
            try{
                JSONObject jsonInput = (JSONObject)parser.parse(stringInput.data);

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
                DataSimulator self = new DataSimulator(kafkaBrokerHosts);

                Config conf = new Config();
                // submit the producer topology.
                if(!debug) {
                    //conf.put("nimbus.thrift.max_buffer_size",500000000);
                    conf.setMaxSpoutPending(1000);
                    conf.setNumWorkers(NumWorkers.intValue());

                    StormSubmitter.submitTopologyWithProgressBar(
                            topologyName, conf,
                            self.buildProducerTopology(self.getProducerConfig(),topicName,topologyConfig)
                    );
                }
                else{
                    conf.setDebug(true);
                    conf.setMaxTaskParallelism(3);

                    LocalCluster cluster = new LocalCluster();
                    cluster.submitTopology(
                            "performanceTest", conf,
                            self.buildProducerTopology(self.getProducerConfig(),topicName,topologyConfig)
                    );
                    Thread.sleep(15000);
                    cluster.shutdown();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
