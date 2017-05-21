package billtenor.graduation.datacustomization.statusCheck;

import billtenor.graduation.datacustomization.tableType.BaseNodeStatusTable;
import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lyj on 17-3-22.
 */
public class StatusCheck implements java.io.Serializable{
    final private int sourceLevel;
    final private int targetLevel;
    private Set<String> active;
    private BaseSpaceTable spaceTable;
    private BaseNodeStatusTable statusTable;

    public StatusCheck(
            int sourceLevel, int targetLevel, BaseSpaceTable baseSpaceTable,BaseNodeStatusTable statusTable
    ){
        active=new HashSet<>();
        this.sourceLevel = sourceLevel;
        this.targetLevel = targetLevel;
        this.spaceTable = baseSpaceTable;
        this.statusTable = statusTable;
    }
    private void getActive(){
        if(sourceLevel==1) {
            for (Map.Entry<String, Boolean> entry : statusTable.status.entrySet()) {
                if (entry.getValue()) {
                    active.add(entry.getKey());
                } else {
                    active.remove(entry.getKey());
                }
            }
        }
        else{
            for (String thisGrainLevelKey : spaceTable.getGrainLevelSet(sourceLevel)){
                boolean isActive=false;
                for(String LowestGrainLevelKey : spaceTable.getLowGrainSet(sourceLevel,1,thisGrainLevelKey)){
                    isActive|=statusTable.status.get(LowestGrainLevelKey);
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
        statusTable.getData();
        getActive();
    }
    public Set<String> getActiveSetByKey(String key){
        Set<String> result = new HashSet<>();
        result.addAll(spaceTable.getLowGrainSet(targetLevel,sourceLevel,key));
        result.retainAll(active);
        return result;
    }
    public Set<String> getActiveSetOfThis(){
        return active;
    }
}
