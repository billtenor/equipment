package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.bolt.MeasureTransformBolt;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by lyj on 17-4-6.
 */
public class MeasureTransformBoltComponent extends StormComponent {
    private MeasureTransformBolt measureTransformBolt;
    public MeasureTransformBoltComponent(
            int parallel,MeasureTransformBolt measureTransformBolt
    ){
        super(parallel);
        this.measureTransformBolt=measureTransformBolt;
    }
    @Override
    public void setKeyTransform(IBaseKeyTransform keyTransform) {
        this.measureTransformBolt.setKeyTransform(keyTransform);
    }
    @Override
    public IBaseKeyTransform getKeyTransform() {
        return this.measureTransformBolt.getKeyTransform();
    }
    @Override
    protected String getComponentBaseName() {
        return "measureTransform";
    }
    @Override
    public TopologyBuilder addToTopology(TopologyBuilder in) {
        in.setBolt(componentName,measureTransformBolt,parallel).shuffleGrouping(previousName);
        return in;
    }
}
