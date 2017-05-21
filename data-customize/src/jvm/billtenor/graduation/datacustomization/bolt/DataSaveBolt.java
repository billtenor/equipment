package billtenor.graduation.datacustomization.bolt;

import billtenor.graduation.datacustomization.dataType.IMyComponent;
import billtenor.graduation.datacustomization.fieldTransform.BaseDataSaveActor;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import billtenor.graduation.datacustomization.fieldTransform.ToSaveBolt;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.windowing.TupleWindow;

import java.util.List;
import java.util.Map;

/**
 * Created by lyj on 17-3-31.
 */
public class DataSaveBolt extends BaseWindowedBolt implements IMyComponent{
    private OutputCollector collector;
    private BaseDataSaveActor dataSaveActor;
    private int retryCount;

    public DataSaveBolt (int retryCount,BaseDataSaveActor dataSaveActor){
        this.retryCount=retryCount;
        this.dataSaveActor=dataSaveActor;
    }
    public IBaseKeyTransform getKeyTransform() {
        return new ToSaveBolt();
    }

    @Override
    public void setKeyTransform(IBaseKeyTransform keyTransform) {

    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector=outputCollector;
        this.dataSaveActor.init();
    }

    @Override
    public void execute(TupleWindow tupleWindow) {
        List<Tuple> newTuples = tupleWindow.get();
        Tuple[] tuples =  newTuples.toArray(new Tuple[newTuples.size()]);
        int count=0;
        while(count<retryCount){
            if(dataSaveActor.dataTransfer(tuples)){
                for(int i=0;i<tuples.length;i++){
                    collector.ack(tuples[i]);
                }
                break;
            }
            count++;
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        if(count>=retryCount){
            for(int i=0;i<tuples.length;i++){
                collector.fail(tuples[i]);
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public void cleanup() {
        super.cleanup();
        this.dataSaveActor.close();
    }
}
