package billtenor.graduation.datacustomization.bolt;

import billtenor.graduation.datacustomization.dataType.BaseAggregateWindow;
import billtenor.graduation.datacustomization.dataType.TimeNoBlockAggregateWindow;
import billtenor.graduation.datacustomization.fieldTransform.*;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lyj on 17-4-14.
 */
public class TimeNoBlockAggregateBolt extends BaseNoBlockAggregateBolt {
    private final Long timeGrain;
    private Long beginTime=null;
    private Long beginTimeInit=null;
    private List<Tuple> beginTimeInitTuple;

    public TimeNoBlockAggregateBolt(
            Long aggregateTimeOut,
            Long aggregateTimeRange,
            Long timeGrain,
            String defaultTimeStampID,
            Map<String,String[]> factIDTargetFactIDs,
            Map<String,String> factsIDFunction,
            Map<String,String> measureIDMeasureType,
            Properties kafkaProps,
            String topic
    ){
        super(aggregateTimeOut,aggregateTimeRange,defaultTimeStampID,
                factIDTargetFactIDs,factsIDFunction,measureIDMeasureType,
                kafkaProps,topic);
        this.timeGrain=timeGrain;
    }

    @Override
    protected int createAggregateWindow(List<BaseAggregateWindow> windows, Tuple tuple, TupleDataTransfer transfer) {
        BaseAggregateWindow window;
        if(windows.size()!=0 && isNewWindow(windows.get(0))){
            window = windows.get(0);
            firstAddTuple(window,tuple,transfer);
            return 0;
        }
        else{
            window = new TimeNoBlockAggregateWindow();
            firstAddTuple(window,tuple,transfer);
            windows.add(window);
            return windows.size() - 1;
        }
    }

    @Override
    protected void firstAddTuple(BaseAggregateWindow window, Tuple tuple, TupleDataTransfer transfer){
        String spaceID = TupleDataTransfer.getKey(tuple);
        TimeNoBlockAggregateWindow timeWindow = (TimeNoBlockAggregateWindow)window;
        timeWindow.data = noBlockAggregateBoltActor.init(transfer,spaceID,defaultTimeStampID,timeWindow.para);
    }

    @Override
    protected void notFirstAddTuple(BaseAggregateWindow window, Tuple tuple, TupleDataTransfer transfer){
        TimeNoBlockAggregateWindow timeWindow = (TimeNoBlockAggregateWindow)window;
        noBlockAggregateBoltActor.aggressive(timeWindow.data,transfer,defaultTimeStampID,timeWindow.para);
    }

    @Override
    protected boolean judgeIfIn(BaseAggregateWindow window, TupleDataTransfer tupleDataTransfer){
        Long time = tupleDataTransfer.getDefaultTimeStamp();
        Long startTime = (Long)window.getState("startTime");
        return time>=startTime && time<startTime+this.timeGrain;
    }

    private boolean judgeEmit(BaseAggregateWindow data) {
        if(data.createTime + this.aggregateTimeOut + this.timeGrain<getTimeNow())
            return true;
        else
            return false;
    }

    @Override
    protected void emitFail(String tupleKey, List<BaseAggregateWindow> windows) {
        for (int i = 0; i < windows.size(); i++) {
            BaseAggregateWindow window = windows.get(i);
            if (!isNewWindow(window) && judgeEmit(window)) {
                //data.print();
                emitTuples(windows,i);

                System.out.println("[MYLOG]-------------------------");
                System.out.println("[MYLOG]emit!!!");
                System.out.println("[MYLOG]tupleKey:"+tupleKey);
                System.out.println("[MYLOG]createTime:"+windows.get(i).createTime);
                System.out.println("[MYLOG]tupleKey has size:"+windows.size());
                System.out.println("[MYLOG]tuple size:"+windows.get(i).tuples.size());
                System.out.println("[MYLOG]-------------------------");
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
