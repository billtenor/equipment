package billtenor.graduation.datacustomization.bolt;

import billtenor.graduation.datacustomization.dataType.BaseAggregateWindow;
import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import billtenor.graduation.datacustomization.dataType.TimeNoBlockAggregateWindow;
import billtenor.graduation.datacustomization.fieldTransform.NoBlockAggregateBoltActor;
import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;
import org.apache.kafka.clients.producer.*;
import org.apache.storm.state.KeyValueState;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IStatefulBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.*;

/**
 * Created by lyj on 17-3-21.
 */
public abstract class BaseNoBlockAggregateBolt extends BaseAggregateBolt implements IStatefulBolt<KeyValueState<String, List<BaseAggregateWindow>>>{

    class MyCallBack implements Callback {
        private List<BaseAggregateWindow> windows;
        private BaseAggregateWindow window;

        public MyCallBack(List<BaseAggregateWindow> windows, BaseAggregateWindow window) {
            this.windows=windows;
            this.window=window;
        }
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            if (metadata != null) {
                windows.remove(window);
            } else {
                exception.printStackTrace();
            }
        }
    }

    private final Properties kafkaProps;
    private final String topic;
    private OutputCollector collector;
    private KafkaProducer<String, String> producer;

    protected NoBlockAggregateBoltActor noBlockAggregateBoltActor;
    protected TupleDataTransfer tupleDataTransfer =new TupleDataTransfer();
    protected KeyValueState<String,List<BaseAggregateWindow>> buffData;

    public BaseNoBlockAggregateBolt(
            Long aggregateTimeOut,
            Long aggregateTimeRange,
            String defaultTimeStampID,
            Map<String,String[]> factIDTargetFactIDs,
            Map<String,String> factsIDFunction,
            Map<String,String> measureIDMeasureType,
            Properties kafkaProps,
            String topic
    ) {
        super(aggregateTimeOut,aggregateTimeRange,defaultTimeStampID,
                factIDTargetFactIDs,factsIDFunction,measureIDMeasureType);
        this.kafkaProps=kafkaProps;
        this.topic=topic;
    }

    abstract protected void emitFail(String tupleKey, List<BaseAggregateWindow> windows);

    protected void emitTuples(List<BaseAggregateWindow> windows,int index){
        TimeNoBlockAggregateWindow window = (TimeNoBlockAggregateWindow) windows.get(index);
        this.producer.send(
                new ProducerRecord<>(this.topic,window.data.spaceID,window.data.toString()),
                new MyCallBack(windows,windows.get(index))
        );
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.noBlockAggregateBoltActor =new NoBlockAggregateBoltActor(
                this.factsIDFunction,
                this.measureIDMeasureType,
                this.factIDTargetFactIDs
        );
        String clientID = (String)this.kafkaProps.get(ProducerConfig.CLIENT_ID_CONFIG);
        this.kafkaProps.put(ProducerConfig.CLIENT_ID_CONFIG,clientID+"_"+Integer.toString(context.getThisTaskIndex()));
        this.producer = new KafkaProducer<>(this.kafkaProps);
    }

    @Override
    public void initState(KeyValueState<String, List<BaseAggregateWindow>> stringListKeyValueState) {
        buffData=stringListKeyValueState;
    }
    @Override
    public void execute(Tuple tuple) {
        this.tupleDataTransfer.refreshData(tuple);
        TupleDataTransfer transfer=this.tupleDataTransfer;

        setTimeNow(transfer);

        String tupleKey = transfer.getKey(tuple);
        if(buffData.get(tupleKey)==null){
            System.out.println("[MYLOG]Create tupleKey:"+tupleKey);
            buffData.put(tupleKey,new ArrayList<BaseAggregateWindow>());
        }
        List<BaseAggregateWindow> windows=buffData.get(tupleKey);

        //judge if to add the tuple, result in index
        int index=-1;
        boolean addIn=false;
        for(int i=0;i<windows.size();i++){
            BaseAggregateWindow data = windows.get(i);
            if(!isNewWindow(data)&&judgeIfIn(data,transfer)){
                addTuple(data,tuple,transfer);
                addIn=true;
                index=i;
                break;
            }
        }
        if(!addIn){
            createAggregateWindow(windows,tuple,transfer);
        }

        //finally emit, wait, or fail the datas;
        emitFail(tupleKey,windows);

        collector.ack(tuple);
    }
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(GlobalConfig.outputFields));
    }

    @Override
    public void cleanup(){}

    @Override
    public void preCommit(long l) {
    }

    @Override
    public void prePrepare(long l) {
    }

    @Override
    public void preRollback() {
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
