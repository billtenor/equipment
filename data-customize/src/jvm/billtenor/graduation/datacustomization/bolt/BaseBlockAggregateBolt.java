package billtenor.graduation.datacustomization.bolt;

import billtenor.graduation.datacustomization.dataType.BaseAggregateWindow;
import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import billtenor.graduation.datacustomization.fieldTransform.BlockAggregateBoltActor;
import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyj on 17-3-21.
 */
public abstract class BaseBlockAggregateBolt extends BaseAggregateBolt implements IRichBolt{

    private OutputCollector collector;
    private BlockAggregateBoltActor blockAggregateBoltActor;

    protected TupleDataTransfer tupleDataTransfer =new TupleDataTransfer();
    protected Map<String,ArrayList<BaseAggregateWindow>> buffData;

    public BaseBlockAggregateBolt(
            Long aggregateTimeOut,
            Long aggregateTimeRange,
            String defaultTimeStampID,
            Map<String,String[]> factIDTargetFactIDs,
            Map<String,String> factsIDFunction,
            Map<String,String> measureIDMeasureType
    ) {
        super(aggregateTimeOut,aggregateTimeRange,defaultTimeStampID,
                factIDTargetFactIDs,factsIDFunction,measureIDMeasureType);
    }

    abstract protected void emitFail(String tupleKey, List<BaseAggregateWindow> datas, int index);

    protected void emitTuples(BaseAggregateWindow data){
        String spaceID = TupleDataTransfer.getKey(data.tuples.get(0));
        TupleDataTransfer outputData = blockAggregateBoltActor.aggressive(data.getJSONDataList(),spaceID,defaultTimeStampID);
        String outputKey=keyTransform.getKey(outputData);
        collector.emit(data.tuples, new Values(outputKey,outputData.toString()));
        for(int i=0;i<data.tuples.size();i++){
            collector.ack(data.tuples.get(i));
        }
    }
    protected void failTuples(BaseAggregateWindow data) {
        for(int i=0;i<data.tuples.size();i++)
            collector.fail(data.tuples.get(i));
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.buffData=new HashMap<>();
        this.blockAggregateBoltActor =new BlockAggregateBoltActor(
                this.factsIDFunction,
                this.measureIDMeasureType,
                this.factIDTargetFactIDs
        );
    }
    @Override
    public void execute(Tuple tuple) {
        this.tupleDataTransfer.refreshData(tuple);
        TupleDataTransfer transfer=this.tupleDataTransfer;

        setTimeNow(transfer);

        String tupleKey = transfer.getKey(tuple);
        if(!buffData.containsKey(tupleKey)){
            System.out.println("[MYLOG]Create tupleKey:"+tupleKey);
            buffData.put(tupleKey,new ArrayList<BaseAggregateWindow>());
        }
        List<BaseAggregateWindow> datas=buffData.get(tupleKey);

        //judge if the tuple has been processed
        for(int i=0;i<datas.size();i++){
            String tupleMark = transfer.getTupleMark();
            if(datas.get(i).hasProcessed(tupleMark)) {
                System.out.println("[MYLOG]This tuple has been processed:"+tupleMark);
                collector.ack(tuple);
                return;
            }
        }

        //judge if to add the tuple, result in index
        int index=-1;
        boolean addIn=false;
        for(int i=0;i<datas.size();i++){
            BaseAggregateWindow data = datas.get(i);
            if(!isNewWindow(data)&&judgeIfIn(data,transfer)){
                addTuple(data,tuple,transfer);
                addIn=true;
                index=i;
                break;
            }
        }
        if(!addIn){
            index = createAggregateWindow(datas,tuple,transfer);
        }

        //finally emit, wait, or fail the datas;
        emitFail(tupleKey,datas,index);
    }
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(GlobalConfig.outputFields));
    }

    @Override
    public void cleanup(){}


    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
