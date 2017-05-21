package billtenor.graduation.datacustomization;

import billtenor.graduation.datacustomization.dataType.KafkaSpoutConfig;
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

import java.util.Properties;

/**
 * Created by lyj on 17-3-13.
 */
public class DataConsumerOld implements java.io.Serializable{
    private String zkConnString;
    private String zkKafkaPrefix;
    private String topicName;

    DataConsumerOld(String zkConnString, String zkKafkaPrefix, String topicName){
        this.zkConnString=zkConnString;
        this.zkKafkaPrefix=zkKafkaPrefix;
        this.topicName=topicName;
    }

    private StormTopology buildConsumerTopology() {
        TopologyBuilder builder = new TopologyBuilder();
        /*
        //spout create and config
        BrokerHosts hosts = new ZkHosts(zkConnString,zkKafkaPrefix+"/brokers");
        SpoutConfig spoutConfig = new SpoutConfig(
                hosts,topicName,
                zkKafkaPrefix+"/topic/"+topicName,UUID.randomUUID().toString()
        );
        spoutConfig.scheme = new SchemeAsMultiScheme(new DataSourceScheme(
                new ToMeasureTransformBolt(),measureTable.getMeasureIDMeasureType()
        ));
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
        builder.setSpout("kafka-spout", kafkaSpout,2);
        //measureTransformBolt create and config
        List<String> measureTargetID = new ArrayList<String>(){{
            add("Voltage");
            add("Current");
            add("State");
            add("Power");
            add("Load");
            add("Accelaration_g");
            add("Accelaration_m/s^2");
        }};
        MeasureTransformBolt measureTransformBolt = new MeasureTransformBolt(
                new ToSpaceAggregateBolt(spaceTable.getLowGrainKeyHighGrainKey(1,2)),
                measureTable.getMeasureTargetIDMeasureEquation(measureTargetID),
                measureTable.getMeasureIDMeasureType()
        );
        builder.setBolt("measureTransform",measureTransformBolt,2).shuffleGrouping("kafka-spout");

        //spaceAggregateBolt create and config
        LocalStatusCheck statusCheck = new LocalStatusCheck(1,2,spaceTable);
        statusCheck.setStatus("95135742",false);
        statusCheck.setStatus("21968543",false);
        statusCheck.setStatus("66511289",false);
        statusCheck.setStatus("21681231",false);
        statusCheck.setStatus("32195123",false);
        statusCheck.setStatus("21684213",false);
        statusCheck.setStatus("21968420",false);
        statusCheck.setStatus("12354621",false);
        statusCheck.setStatus("21632456",false);
        statusCheck.setStatus("10324652",false);
        SpaceBlockAggregateBolt spaceAggregateBolt = new SpaceBlockAggregateBolt(
                1500,1,2,"16",
                new ToSaveBolt(),
                new HashMap<String, String[]>(){{
                    put("1",new String[]{"10","11","12","13"});
                    put("2",new String[]{"16"});
                }},
                factsTable.getFactsIDFunction(),
                measureTable.getMeasureIDMeasureType(),
                statusCheck
        );
        builder.setBolt("spaceAggregate",spaceAggregateBolt,3).fieldsGrouping("measureTransform", new Fields("key"));

        //DataSaveBolt create and config
        DataSaveBolt dataSaveBolt = new DataSaveBolt(
                3,
                new MySqlDataSaveActor("192.168.100.254","3306","warehouseUser","zhu88jie","warehouseTest",
                        new HashMap<String,String[]>(){{
                            put("table3",new String[]{"16","10","11","12","13"});
                        }}
                )
        );
        builder.setBolt("dataSave",dataSaveBolt.withTumblingWindow(new BaseWindowedBolt.Count(5)),2).shuffleGrouping("spaceAggregate");
        */

        //test from kafka
        /**
         * The output field of the RandomSentenceSpout ("word") is provided as the boltMessageField
         * so that this gets written out as the message in the kafka topic.
         */
        Properties prop = new Properties();
        prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.100.47:9092,192.168.100.44:9092");
        prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        prop.put(ProducerConfig.CLIENT_ID_CONFIG, "storm-kafka-producer");
        KafkaBolt bolt = new KafkaBolt().withProducerProperties(prop)
                .withTopicSelector(new DefaultTopicSelector("consumer"))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper("key", "data"));
        builder.setBolt("forwardToKafka", bolt, 1).shuffleGrouping("spaceAggregate_1");
        return builder.createTopology();
    }
}
