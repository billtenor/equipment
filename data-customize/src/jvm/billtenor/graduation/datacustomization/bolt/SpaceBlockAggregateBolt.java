package billtenor.graduation.datacustomization.bolt;

import billtenor.graduation.datacustomization.dataType.BaseAggregateWindow;
import billtenor.graduation.datacustomization.fieldTransform.ToSpaceAggregateBolt;
import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import billtenor.graduation.datacustomization.dataType.SpaceAggregateWindow;
import billtenor.graduation.datacustomization.statusCheck.StatusCheck;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;

import java.util.*;

/**
 * Created by lyj on 17-3-21.
 */
public class SpaceBlockAggregateBolt extends BaseBlockAggregateBolt{
    private final int sourceLevel;
    private final int targetLevel;
    private final Map<String,String> lowGrainIDHighGrainID;

    private StatusCheck statusCheck;
    private Map<String,Integer> buffDataSize;

    public SpaceBlockAggregateBolt(
            Long aggregateTimeOut,Long aggregateTimeRange, int sourceLevel, int targetLevel, String defaultTimeStampID,
            Map<String,String[]> factIDTargetFactIDs,
            Map<String,String> factsIDFunction,
            Map<String,String> measureIDMeasureType,
            Map<String,String> lowGrainIDHighGrainID,
            StatusCheck statusCheck
    ) {
        super(aggregateTimeOut,aggregateTimeRange,defaultTimeStampID,factIDTargetFactIDs,factsIDFunction,measureIDMeasureType);
        this.sourceLevel=sourceLevel;
        this.targetLevel=targetLevel;
        this.lowGrainIDHighGrainID=lowGrainIDHighGrainID;
        this.statusCheck=statusCheck;
    }
    public IBaseKeyTransform getKeyTransform(){
        return new ToSpaceAggregateBolt(lowGrainIDHighGrainID);
    }
    private int getBuffDataSize(String key,boolean refresh){
        if(refresh){
            this.statusCheck.refresh();
            buffDataSize.remove(key);
        }
        if(buffDataSize.containsKey(key)){
            return buffDataSize.get(key);
        }
        else{
            int size = this.statusCheck.getActiveSetByKey(key).size();
            buffDataSize.put(key,size);
            return size;
        }
    }

    private boolean judgeEmit(BaseAggregateWindow data){
        return data.countProcessed() == (int) data.getState("size");
    }
    private boolean judgeTimeOut(BaseAggregateWindow data){
        if(data.createTime+this.aggregateTimeOut<this.getTimeNow())
            return true;
        else
            return false;
    }
    @Override
    protected void emitFail(String tupleKey, List<BaseAggregateWindow> datas, int index) {
        if(judgeEmit(datas.get(index))){
            emitTuples(datas.get(index));

            System.out.println("[MYLOG]-------------------------");
            System.out.println("[MYLOG]emit!!!");
            System.out.println("[MYLOG]tupleKey:"+tupleKey);
            System.out.println("[MYLOG]createTime:"+datas.get(index).createTime);
            System.out.println("[MYLOG]tupleKey has size:"+datas.size());
            System.out.println("[MYLOG]tuple size:"+datas.get(index).tuples.size());
            System.out.println("[MYLOG]-------------------------");

            cleanWindows(datas,index);
        }
        //check if AggregateData is TimeOut
        for (int i = 0; i < datas.size(); i++) {
            BaseAggregateWindow data = datas.get(i);
            if (!isNewWindow(data) && judgeTimeOut(data)) {

                System.out.println("[MYLOG]-------------------------");
                System.out.println("[MYLOG]TimeOut!");
                data.print();
                System.out.println("[MYLOG]-------------------------");

                int size=getBuffDataSize(tupleKey,true);
                data.setState("size",size);
                if (judgeEmit(data)) {
                    emitTuples(data);
                } else {
                    failTuples(data);
                }
                i = cleanWindows(datas, i);
            }
        }
    }

    @Override
    protected boolean judgeIfIn(BaseAggregateWindow spaceAggregateData, TupleDataTransfer tupleDataTransfer){
        Long time = tupleDataTransfer.getDefaultTimeStamp();
        if(time<(Long)spaceAggregateData.getState("startTime")+this.aggregateTimeRange &&
                time > (Long)spaceAggregateData.getState("endTime")-this.aggregateTimeRange){
            return true;
        }
        else
            return false;
    }

    @Override
    protected int createAggregateWindow(List<BaseAggregateWindow> spaceAggregateDatas, Tuple tuple, TupleDataTransfer transfer){
        BaseAggregateWindow data;
        if(spaceAggregateDatas.size()!=0 && isNewWindow(spaceAggregateDatas.get(0))){
            data = spaceAggregateDatas.get(0);
            firstAddTuple(data,tuple,transfer);
            return 0;
        }
        else{
            data = new SpaceAggregateWindow();
            firstAddTuple(data,tuple,transfer);
            spaceAggregateDatas.add(data);
            return spaceAggregateDatas.size() - 1;
        }
    }

    @Override
    protected void firstAddTuple(BaseAggregateWindow data, Tuple tuple, TupleDataTransfer transfer){
        String tupleKey = transfer.getKey(tuple);
        String tupleMark = transfer.getTupleMark();
        Long tupleTime = transfer.getDefaultTimeStamp();
        data.createTime=getTimeNow();
        data.setState("startTime", tupleTime);
        data.setState("endTime", tupleTime);
        data.setState("size",getBuffDataSize(tupleKey,false));
        data.addProcessed(tupleMark);
        data.tuples.add(tuple);

        System.out.println("[MYLOG]-------------------------");
        System.out.println("[MYLOG]Function:firstAddTuple()");
        System.out.println("[MYLOG]tupleKey:"+tupleKey);
        System.out.println("[MYLOG]createTime:"+data.createTime.toString());
        System.out.println("[MYLOG]startTime:"+data.getState("startTime").toString());
        System.out.println("[MYLOG]endTime:"+data.getState("endTime").toString());
        System.out.println("[MYLOG]size:"+data.getState("size").toString());
        System.out.println("[MYLOG]-------------------------");
    }

    @Override
    protected void notFirstAddTuple(BaseAggregateWindow data, Tuple tuple, TupleDataTransfer transfer){
        String tupleMark=transfer.getTupleMark();
        Long time=transfer.getDefaultTimeStamp();
        data.tuples.add(tuple);
        data.addProcessed(tupleMark);
        if(time>(Long)data.getState("endTime")){
            data.setState("endTime",time);
        }
        else if(time<(Long)data.getState("startTime")){
            data.setState("startTime",time);
        }
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf,context,collector);
        this.buffDataSize=new HashMap<>();
        this.statusCheck.refresh();
    }
}
