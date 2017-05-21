package billtenor.graduation.datacustomization.bolt;

import billtenor.graduation.datacustomization.dataType.BaseAggregateWindow;
import billtenor.graduation.datacustomization.dataType.TimeBlockAggregateWindow;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import billtenor.graduation.datacustomization.fieldTransform.ToTimeAggregateBolt;
import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lyj on 17-4-13.
 */
public class TimeBlockAggregateBolt extends BaseBlockAggregateBolt{
    final private Long timeGrain;
    private Long beginTime=null;
    private Long beginTimeInit=null;
    private List<Tuple> beginTimeInitTuple;

    public TimeBlockAggregateBolt(
            Long aggregateTimeOut,
            Long aggregateTimeRange,
            Long timeGrain,
            String defaultTimeStampID,
            Map<String,String[]> factIDTargetFactIDs,
            Map<String,String> factsIDFunction,
            Map<String,String> measureIDMeasureType
    ){
        super(aggregateTimeOut,aggregateTimeRange,defaultTimeStampID,factIDTargetFactIDs,factsIDFunction,measureIDMeasureType);
        this.timeGrain=timeGrain;
    }

    @Override
    protected int createAggregateWindow(List<BaseAggregateWindow> datas, Tuple tuple, TupleDataTransfer transfer) {
        BaseAggregateWindow data;
        if(datas.size()!=0 && isNewWindow(datas.get(0))){
            data = datas.get(0);
            firstAddTuple(data,tuple,transfer);
            return 0;
        }
        else{
            data = new TimeBlockAggregateWindow();
            firstAddTuple(data,tuple,transfer);
            datas.add(data);
            return datas.size() - 1;
        }
    }

    @Override
    protected void firstAddTuple(BaseAggregateWindow data, Tuple tuple, TupleDataTransfer transfer) {
        String tupleMark = transfer.getTupleMark();
        Long tupleTime = transfer.getDefaultTimeStamp();
        data.createTime=getTimeNow();
        long count = (tupleTime - beginTime) / timeGrain;
        Long startTime = beginTime + timeGrain * count;
        data.setState("startTime", startTime);
        data.addProcessed(tupleMark);
        data.tuples.add(tuple);
    }

    @Override
    protected void notFirstAddTuple(BaseAggregateWindow data, Tuple tuple, TupleDataTransfer transfer) {
        String tupleMark = transfer.getTupleMark();
        data.addProcessed(tupleMark);
        data.tuples.add(tuple);
    }

    @Override
    protected boolean judgeIfIn(BaseAggregateWindow spaceAggregateData, TupleDataTransfer tupleDataTransfer) {
        Long time = tupleDataTransfer.getDefaultTimeStamp();
        Long startTime = (Long)spaceAggregateData.getState("startTime");
        return time>=startTime && time<startTime+this.timeGrain;
    }

    private boolean judgeEmit(BaseAggregateWindow data) {
        if(data.createTime + this.aggregateTimeOut + this.timeGrain<getTimeNow())
            return true;
        else
            return false;
    }
    @Override
    protected void emitFail(String tupleKey, List<BaseAggregateWindow> datas, int index) {
        for (int i = 0; i < datas.size(); i++) {
            BaseAggregateWindow data = datas.get(i);
            if (!isNewWindow(data) && judgeEmit(data)) {
                //data.print();
                emitTuples(data);

                System.out.println("[MYLOG]-------------------------");
                System.out.println("[MYLOG]emit!!!");
                System.out.println("[MYLOG]tupleKey:"+tupleKey);
                System.out.println("[MYLOG]createTime:"+datas.get(i).createTime);
                System.out.println("[MYLOG]tupleKey has size:"+datas.size());
                System.out.println("[MYLOG]tuple size:"+datas.get(i).tuples.size());
                System.out.println("[MYLOG]-------------------------");

                cleanWindows(datas,i);
            }
        }
    }

    @Override
    public IBaseKeyTransform getKeyTransform() {
        return new ToTimeAggregateBolt();
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        super.prepare(stormConf, context, collector);
        this.beginTimeInitTuple=new ArrayList<>();
    }

    public Long avg(Long avg,int count,Long num){
        Double avgDouble = avg.doubleValue();
        Double resultDouble = (avgDouble*count+num.doubleValue())/(count+1);
        return resultDouble.longValue();
    }
    @Override
    public void execute(Tuple tuple) {
        if(beginTime==null) {
            this.tupleDataTransfer.refreshData(tuple);
            TupleDataTransfer transfer=this.tupleDataTransfer;
            setTimeNow(transfer);
            System.out.println("[MYLOG]tupleMark:"+transfer.getTupleMark());

            Long time = transfer.getDefaultTimeStamp();
            beginTimeInitTuple.add(tuple);
            if(beginTimeInit==null){
                beginTimeInit=time;
                System.out.println("[MYLOG]beginTimeInit:"+beginTimeInit.toString());
            }
            else if(Math.abs(beginTimeInit-time)<this.aggregateTimeRange){
                beginTimeInit=avg(beginTimeInit,beginTimeInitTuple.size()-1,time);
                System.out.println("[MYLOG]beginTimeInit:"+beginTimeInit.toString());
            }
            else {
                beginTime=beginTimeInit-aggregateTimeRange;
                for(int i=0;i<beginTimeInitTuple.size();i++){
                    super.execute(beginTimeInitTuple.get(i));
                }
                beginTimeInitTuple.clear();
                System.out.println("[MYLOG]beginTime:"+beginTime.toString());
            }
        }
        else{
            super.execute(tuple);
        }
    }
}
