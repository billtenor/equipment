package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.bolt.TimeBlockAggregateBolt;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * Created by lyj on 17-4-14.
 */
public class TimeBlockAggregateBoltComponent extends StormComponent{
    TimeBlockAggregateBolt timeBlockAggregateBolt;
    public TimeBlockAggregateBoltComponent(
            int parallel,TimeBlockAggregateBolt timeBlockAggregateBolt
    ){
        super(parallel);
        this.timeBlockAggregateBolt=timeBlockAggregateBolt;
    }

    @Override
    public IBaseKeyTransform getKeyTransform() {
        return this.timeBlockAggregateBolt.getKeyTransform();
    }

    @Override
    public void setKeyTransform(IBaseKeyTransform keyTransform) {
        this.timeBlockAggregateBolt.setKeyTransform(keyTransform);
    }

    @Override
    protected String getComponentBaseName() {
        return "timeBlockAggregate";
    }

    @Override
    public TopologyBuilder addToTopology(TopologyBuilder in) {
        in.setBolt(componentName, timeBlockAggregateBolt,parallel).fieldsGrouping(previousName, new Fields("key"));
        return in;
    }
}
