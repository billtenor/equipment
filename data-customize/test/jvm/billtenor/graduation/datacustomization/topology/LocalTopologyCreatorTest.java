package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.dataType.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;
import org.junit.Test;
/**
 * Created by yanjun on 17-4-8.
 */
public class LocalTopologyCreatorTest {
    //@Test
    public void localTopologyCreatorTest(){
        String zkConnString="192.168.100.12:2181,192.168.100.13:2181,192.168.100.14:2181";
        String zkKafkaPrefix="/kafka/q-m1f1lxxv";
        String topicName="test";
        String kafkaBrokerHosts="192.168.100.47:9092,192.168.100.44:9092";
        LocalTopologyCreator topologyCreator = new LocalTopologyCreator(
                "testWarehouseModel","testDataSourceModel","testDataModel",
                new String[]{"testFactTable004"},
                new KafkaSpoutConfig(zkConnString,zkKafkaPrefix,topicName),
                750l,3000l,kafkaBrokerHosts,2,5,3
        );
        TopologyBuilder builder = topologyCreator.buildConsumerTopology();
        return;
    }
}
