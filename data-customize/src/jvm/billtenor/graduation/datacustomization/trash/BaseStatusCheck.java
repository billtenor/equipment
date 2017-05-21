package billtenor.graduation.datacustomization.statusCheck;

import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lyj on 17-3-22.
 */
public abstract class BaseStatusCheck implements java.io.Serializable{
    final private int sourceLevel;
    final private int targetLevel;
    protected Set<String> active;
    protected Map<String, Boolean> status;
    protected BaseSpaceTable baseSpaceTable;
    protected BaseStatusCheck(int sourceLevel,int targetLevel,BaseSpaceTable baseSpaceTable){
        status=new HashMap<>();
        active=new HashSet<>();
        this.sourceLevel = sourceLevel;
        this.targetLevel = targetLevel;
        this.baseSpaceTable = baseSpaceTable;
    }
    protected abstract void getStatusOfLowest();
    private void getActive(){
        if(sourceLevel==1) {
            for (Map.Entry<String, Boolean> entry : status.entrySet()) {
                if (entry.getValue()) {
                    active.add(entry.getKey());
                } else {
                    active.remove(entry.getKey());
                }
            }
        }
        else{
            for (String thisGrainLevelKey : baseSpaceTable.getGrainLevelSet(sourceLevel)){
                boolean isActive=false;
                for(String LowestGrainLevelKey : baseSpaceTable.getLowGrainSet(sourceLevel,1,thisGrainLevelKey)){
                    isActive|=status.get(LowestGrainLevelKey);
                }
                if(isActive) {
                    active.add(thisGrainLevelKey);
                }else {
                    active.remove(thisGrainLevelKey);
                }
            }
        }
    }
    public void refresh(){
        getStatusOfLowest();
        getActive();
    }
    public Set<String> getActiveSetByKey(String key){
        Set<String> result = new HashSet<>();
        result.addAll(baseSpaceTable.getLowGrainSet(targetLevel,sourceLevel,key));
        result.retainAll(active);
        return result;
    }
    public Set<String> getActiveSetOfThis(){
        return active;
    }
}
