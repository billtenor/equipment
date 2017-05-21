package billtenor.graduation.datacustomization.spout;

import billtenor.graduation.datacustomization.fieldTransform.DataSimulatorSpoutActor;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import java.util.*;

/**
 * Created by lyj on 17-2-9.
 */
public class DataSimulatorSpout extends BaseRichSpout{
    private DataSimulatorSpoutActor dataSimulatorSpoutActor;
    private SpoutOutputCollector _collector;
    private Map<String,String> spaceIDAreaID;
    private Random _rand;

    public DataSimulatorSpout(String dataSourceModelID){
        this.dataSimulatorSpoutActor=new DataSimulatorSpoutActor(dataSourceModelID);
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this._collector = collector;
        this._rand = new Random();
        this.spaceIDAreaID = new HashMap<>();
        int spoutsSize = context.getComponentTasks(context.getThisComponentId()).size();
        int spoutNum = context.getThisTaskIndex();
        System.out.println("[MYLOG]:spoutsSize="+Integer.toString(spoutsSize)+",thisSpoutNum="+Integer.toString(spoutNum));
        for(int i=0;i<dataSimulatorSpoutActor.spaceID.size();i++){
            if(i%spoutsSize==spoutNum){
                spaceIDAreaID.put(
                        dataSimulatorSpoutActor.spaceID.get(i),
                        dataSimulatorSpoutActor.areaID.get(i)
                );
            }
        }
        for(String spaceID:spaceIDAreaID.keySet()){
            System.out.println("[MYLOG]:spaceDimTableKeys:"+spaceID);
        }
    }

    @Override
    public void nextTuple() {
        Utils.sleep(dataSimulatorSpoutActor.dataSourceModel.dataGapTime);
        int randTimeError=(int)(dataSimulatorSpoutActor.dataSourceModel.dataGapTime / 50);
        Utils.sleep(_rand.nextInt(randTimeError));
        for(String spaceID:spaceIDAreaID.keySet()){
            _collector.emit(new Values(
                    spaceIDAreaID.get(spaceID),
                    dataSimulatorSpoutActor.createJsonData(spaceID,this._rand)
            ));
        }
    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("key","jsonSpout"));
    }
}
