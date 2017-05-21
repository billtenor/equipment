package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.bolt.SpaceBlockAggregateBolt;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * Created by lyj on 17-4-6.
 */
public class SpaceBlockAggregateBoltComponent extends StormComponent{
    SpaceBlockAggregateBolt spaceBlockAggregateBolt;
    public SpaceBlockAggregateBoltComponent(
            int parallel,SpaceBlockAggregateBolt spaceBlockAggregateBolt
    ){
        super(parallel);
        this.spaceBlockAggregateBolt = spaceBlockAggregateBolt;
    }
    @Override
    public IBaseKeyTransform getKeyTransform() {
        return this.spaceBlockAggregateBolt.getKeyTransform();
    }
    @Override
    public void setKeyTransform(IBaseKeyTransform keyTransform) {
        this.spaceBlockAggregateBolt.setKeyTransform(keyTransform);
    }
    @Override
    protected String getComponentBaseName() {
        return "spaceBlockAggregate";
    }
    @Override
    public TopologyBuilder addToTopology(TopologyBuilder in) {
        in.setBolt(componentName, spaceBlockAggregateBolt,parallel).fieldsGrouping(previousName, new Fields("key"));
        return in;
    }
}
