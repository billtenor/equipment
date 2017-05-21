package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.bolt.TimeBlockAggregateBolt;
import billtenor.graduation.datacustomization.bolt.TimeNoBlockAggregateBolt;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * Created by lyj on 17-4-14.
 */
public class TimeNoBlockAggregateBoltComponent extends StormComponent{
    TimeNoBlockAggregateBolt timeNoBlockAggregateBolt;
    public TimeNoBlockAggregateBoltComponent(
            int parallel,TimeNoBlockAggregateBolt timeNoBlockAggregateBolt
    ){
        super(parallel);
        this.timeNoBlockAggregateBolt=timeNoBlockAggregateBolt;
    }

    @Override
    public IBaseKeyTransform getKeyTransform() {
        return this.timeNoBlockAggregateBolt.getKeyTransform();
    }

    @Override
    public void setKeyTransform(IBaseKeyTransform keyTransform) {
        this.timeNoBlockAggregateBolt.setKeyTransform(keyTransform);
    }

    @Override
    protected String getComponentBaseName() {
        return "timeNoBlockAggregate";
    }

    @Override
    public TopologyBuilder addToTopology(TopologyBuilder in) {
        in.setBolt(componentName, timeNoBlockAggregateBolt,parallel).fieldsGrouping(previousName, new Fields("key"));
        return in;
    }
}
