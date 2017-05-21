package billtenor.graduation.datacustomization.statusCheck;

import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lyj on 17-3-22.
 */
public class LocalStatusCheck extends BaseStatusCheck{
    private Map<String,Boolean> manualStatus;
    public LocalStatusCheck(int sourceLevel,int targetLevel, BaseSpaceTable baseSpaceTable){
        super(sourceLevel,targetLevel,baseSpaceTable);
        manualStatus=new HashMap<>();
        for(String key:this.baseSpaceTable.getGrainLevelSet(1)){
            this.manualStatus.put(key,true);
        }
    }
    protected void getStatusOfLowest(){
        for(String key:this.manualStatus.keySet()){
            this.status.put(key,manualStatus.get(key));
        }
    }
    public void setStatus(String key,boolean status){
        this.manualStatus.put(key,status);
    }
}
