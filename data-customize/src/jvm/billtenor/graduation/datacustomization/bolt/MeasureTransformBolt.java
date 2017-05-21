package billtenor.graduation.datacustomization.bolt;

import billtenor.graduation.datacustomization.dataType.IMyComponent;
import billtenor.graduation.datacustomization.fieldTransform.ToMeasureTransformBolt;
import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;
import billtenor.graduation.datacustomization.fieldTransform.MeasureTransformBoltActor;
import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * Created by lyj on 17-3-14.
 */
public class MeasureTransformBolt implements IRichBolt,IMyComponent {
    private IBaseKeyTransform keyTransform;
    private final Map<String,String> measureTargetIDMeasureEquation;
    private final Map<String,String> measureIDMeasureType;

    private OutputCollector collector;
    private TupleDataTransfer tupleDataTransfer;
    private MeasureTransformBoltActor measureTransformBoltActor;

    public MeasureTransformBolt(
            Map<String,String> measureTargetIDMeasureEquation,Map<String,String> measureIDMeasureType
    ){
        this.measureTargetIDMeasureEquation=measureTargetIDMeasureEquation;
        this.measureIDMeasureType=measureIDMeasureType;
    }

    public void setKeyTransform(IBaseKeyTransform keyTransform){
        this.keyTransform=keyTransform;
    }
    public IBaseKeyTransform getKeyTransform(){
        return new ToMeasureTransformBolt();
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector=collector;
        this.tupleDataTransfer =new TupleDataTransfer();
        this.measureTransformBoltActor=new MeasureTransformBoltActor(
                this.measureTargetIDMeasureEquation,
                this.measureIDMeasureType
        );
    }

    @Override
    public void execute(Tuple tuple) {
        tupleDataTransfer.refreshData(tuple);
        //System.out.println("[MYLOG]:inputTuple:"+tupleDataTransfer.toString());
        TupleDataTransfer outputData = measureTransformBoltActor.dataTransform(tupleDataTransfer);
        if(outputData!=null) {
            collector.emit(tuple, new Values(keyTransform.getKey(outputData), outputData.toString()));
        }
        collector.ack(tuple);
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
