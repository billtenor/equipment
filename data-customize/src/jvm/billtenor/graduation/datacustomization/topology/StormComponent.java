package billtenor.graduation.datacustomization.topology;

import billtenor.graduation.datacustomization.dataType.IMyComponent;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by lyj on 17-4-6.
 */
public abstract class StormComponent implements IMyComponent {
    final protected int parallel;
    protected String previousName;
    protected String componentName;

    abstract public TopologyBuilder addToTopology(TopologyBuilder in);
    abstract protected String getComponentBaseName();

    protected StormComponent(int parallel){
        this.parallel=parallel;
    }
    public TopologyBuilder addToTopology(TopologyBuilder in,String previousName){
        this.previousName=previousName;
        setComponentName(previousName);
        return addToTopology(in);
    }
    public void setComponentName(String previousName){
        String[] names = previousName.split("_");
        if(getComponentBaseName().equals(names[0])){
            int num = Integer.parseInt(names[1]);
            this.componentName = getComponentBaseName() + "_" + Integer.toString(num+1);
        }
        else{
            this.componentName = getComponentBaseName() + "_1";
        }
    }
}
