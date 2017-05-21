package billtenor.graduation.datacustomization.bolt;

import billtenor.graduation.datacustomization.dataType.BaseAggregateWindow;
import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import billtenor.graduation.datacustomization.dataType.IMyComponent;
import billtenor.graduation.datacustomization.fieldTransform.BlockAggregateBoltActor;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
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
public abstract class BaseAggregateBolt implements java.io.Serializable,IMyComponent{
    protected final String defaultTimeStampID;
    protected final Map<String,String[]> factIDTargetFactIDs;
    protected final Map<String,String> factsIDFunction;
    protected final Map<String,String> measureIDMeasureType;
    protected final Long aggregateTimeOut;
    protected final Long aggregateTimeRange;

    protected IBaseKeyTransform keyTransform;
    private Long timeNow;

    public BaseAggregateBolt(
            Long aggregateTimeOut,
            Long aggregateTimeRange,
            String defaultTimeStampID,
            Map<String,String[]> factIDTargetFactIDs,
            Map<String,String> factsIDFunction,
            Map<String,String> measureIDMeasureType
    ) {
        this.aggregateTimeOut = aggregateTimeOut;
        this.aggregateTimeRange=aggregateTimeRange;
        this.defaultTimeStampID = defaultTimeStampID;
        this.factIDTargetFactIDs = factIDTargetFactIDs;
        this.factsIDFunction = factsIDFunction;
        this.measureIDMeasureType = measureIDMeasureType;
        this.timeNow=0l;
    }

    abstract protected boolean judgeIfIn(BaseAggregateWindow window, TupleDataTransfer tupleDataTransfer);
    abstract protected int createAggregateWindow(List<BaseAggregateWindow> windows, Tuple tuple, TupleDataTransfer transfer);
    abstract protected void firstAddTuple(BaseAggregateWindow window, Tuple tuple, TupleDataTransfer transfer);
    abstract protected void notFirstAddTuple(BaseAggregateWindow window, Tuple tuple, TupleDataTransfer transfer);

    public void setKeyTransform(IBaseKeyTransform keyTransform){
        this.keyTransform=keyTransform;
    }
    protected boolean isNewWindow(BaseAggregateWindow window){
        return window.createTime==null;
    }
    protected int cleanWindows(List<BaseAggregateWindow> windows, int index){
        if(index==0){
            windows.get(0).clear();
            return 0;
        }
        else {
            windows.remove(index);
            return index - 1;
        }
    }
    protected void addTuple(BaseAggregateWindow window, Tuple tuple, TupleDataTransfer transfer){
        if(window.isEmpty()){
            firstAddTuple(window,tuple,transfer);
        }
        else {
            notFirstAddTuple(window,tuple,transfer);
        }
    }
    protected Long getTimeNow(){
        return this.timeNow;
    }
    protected void setTimeNow(TupleDataTransfer tupleDataTransfer){
        Long time = tupleDataTransfer.getDefaultTimeStamp();
        if(time>this.timeNow)
            this.timeNow=time;
    }
}
