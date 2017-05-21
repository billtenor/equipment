package billtenor.graduation.datacustomization.dataType;

import java.util.UUID;

/**
 * Created by yanjun on 17-4-5.
 */
public class KafkaSpoutConfig {
    final public String zkConnString;
    final public String zkKafkaPrefix;
    final public String topicName;
    final public String kafkaSpoutID;
    public KafkaSpoutConfig(KafkaSpoutConfig kafkaSpoutConfig,String topicName){
        this.zkConnString=kafkaSpoutConfig.zkConnString;
        this.zkKafkaPrefix=kafkaSpoutConfig.zkKafkaPrefix;
        this.kafkaSpoutID = UUID.randomUUID().toString();
        this.topicName=topicName;
    }
    public KafkaSpoutConfig(String zkConnString,String zkKafkaPrefix,String topicName,String kafkaSpoutID){
        this.zkConnString=zkConnString;
        this.zkKafkaPrefix=zkKafkaPrefix;
        this.topicName=topicName;
        this.kafkaSpoutID=kafkaSpoutID;
    }
    public KafkaSpoutConfig(String zkConnString,String zkKafkaPrefix,String topicName){
        this.zkConnString=zkConnString;
        this.zkKafkaPrefix=zkKafkaPrefix;
        this.topicName=topicName;
        this.kafkaSpoutID = UUID.randomUUID().toString();
    }
}
