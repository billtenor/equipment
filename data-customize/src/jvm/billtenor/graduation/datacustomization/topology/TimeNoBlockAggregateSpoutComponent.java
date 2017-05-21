package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.dataType.KafkaSpoutConfig;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import billtenor.graduation.datacustomization.spout.TimeNoBlockAggregateScheme;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by lyj on 17-4-6.
 */
public class TimeNoBlockAggregateSpoutComponent extends StormComponent{
    final private KafkaSpoutConfig kafkaSpoutConfig;
    private TimeNoBlockAggregateScheme timeNoBlockAggregateScheme;

    public TimeNoBlockAggregateSpoutComponent(
            int parallel,
            KafkaSpoutConfig kafkaSpoutConfig
    ){
        super(parallel);
        this.kafkaSpoutConfig=kafkaSpoutConfig;
        this.timeNoBlockAggregateScheme=new TimeNoBlockAggregateScheme();
    }

    @Override
    public void setKeyTransform(IBaseKeyTransform keyTransform) {
        this.timeNoBlockAggregateScheme.setKeyTransform(keyTransform);
    }
    @Override
    public IBaseKeyTransform getKeyTransform() {
        return this.timeNoBlockAggregateScheme.getKeyTransform();
    }
    @Override
    protected String getComponentBaseName() {
        return "timeNoBlockAggregateSpout";
    }
    @Override
    public TopologyBuilder addToTopology(TopologyBuilder in) {
        //spout create and config
        BrokerHosts hosts = new ZkHosts(
                kafkaSpoutConfig.zkConnString,kafkaSpoutConfig.zkKafkaPrefix+"/brokers"
        );
        SpoutConfig spoutConfig = new SpoutConfig(
                hosts,kafkaSpoutConfig.topicName,
                kafkaSpoutConfig.zkKafkaPrefix+"/topic/"+kafkaSpoutConfig.topicName,
                kafkaSpoutConfig.kafkaSpoutID
        );
        spoutConfig.scheme = new SchemeAsMultiScheme(timeNoBlockAggregateScheme);
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
        in.setSpout(componentName,kafkaSpout,parallel);
        return in;
    }
}
