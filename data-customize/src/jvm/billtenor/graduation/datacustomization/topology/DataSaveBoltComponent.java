package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.bolt.DataSaveBolt;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;

/**
 * Created by lyj on 17-4-6.
 */
public class DataSaveBoltComponent extends StormComponent{
    final private int dataSaveWindowCount;
    private DataSaveBolt dataSaveBolt;
    public DataSaveBoltComponent(
            int parallel,int dataSaveWindowCount,DataSaveBolt dataSaveBolt
    ){
        super(parallel);
        this.dataSaveWindowCount=dataSaveWindowCount;
        this.dataSaveBolt=dataSaveBolt;
    }
    @Override
    public IBaseKeyTransform getKeyTransform() {
        return dataSaveBolt.getKeyTransform();
    }

    @Override
    public void setKeyTransform(IBaseKeyTransform keyTransform) {
    }
    @Override
    protected String getComponentBaseName() {
        return "dataSave";
    }
    @Override
    public TopologyBuilder addToTopology(TopologyBuilder in) {
        in.setBolt(componentName,dataSaveBolt.withTumblingWindow(new BaseWindowedBolt.Count(dataSaveWindowCount)),
                parallel).shuffleGrouping(previousName);
        return in;
    }
}
