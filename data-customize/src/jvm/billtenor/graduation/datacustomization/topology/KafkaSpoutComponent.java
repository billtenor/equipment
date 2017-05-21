package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.dataType.KafkaSpoutConfig;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import billtenor.graduation.datacustomization.spout.DataSourceScheme;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by lyj on 17-4-6.
 */
public class KafkaSpoutComponent extends StormComponent{
    final private KafkaSpoutConfig kafkaSpoutConfig;
    private DataSourceScheme dataSourceScheme;

    public KafkaSpoutComponent(
            int parallel,
            KafkaSpoutConfig kafkaSpoutConfig,
            DataSourceScheme dataSourceScheme
    ){
        super(parallel);
        this.kafkaSpoutConfig=kafkaSpoutConfig;
        this.dataSourceScheme=dataSourceScheme;
    }

    @Override
    public void setKeyTransform(IBaseKeyTransform keyTransform) {
        this.dataSourceScheme.setKeyTransform(keyTransform);
    }
    @Override
    public IBaseKeyTransform getKeyTransform() {
        return this.dataSourceScheme.getKeyTransform();
    }
    @Override
    protected String getComponentBaseName() {
        return "kafkaSpout";
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
        spoutConfig.scheme = new SchemeAsMultiScheme(dataSourceScheme);
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.LatestTime();
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
        in.setSpout(componentName,kafkaSpout,parallel);
        return in;
    }
}
