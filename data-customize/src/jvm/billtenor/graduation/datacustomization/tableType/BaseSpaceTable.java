package billtenor.graduation.datacustomization.tableType;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lyj on 17-3-22.
 */
public abstract class BaseSpaceTable extends BaseTable{
    protected Table<String,String,String> data;
    protected Map<Integer,String> grainLevelGrainID;

    public BaseSpaceTable(Map<Integer,String> grainLevelGrainID){
        this.data=HashBasedTable.create();
        this.grainLevelGrainID=grainLevelGrainID;
    }
    protected abstract void getData();
    private String getDataByLevel(String rowKey,int level){
        if(level==1)
            return rowKey;
        else
            return data.get(rowKey,grainLevelGrainID.get(level));
    }
    public Map<String,String> getLowGrainKeyHighGrainKey(final int lowLevel, final int highLevel){
        if(lowLevel==1){
            return switchMap(data.column(grainLevelGrainID.get(highLevel)));
        }
        else{
            return new HashMap<String,String>(){{
                for(String lowGrainKey:getGrainLevelSet(lowLevel)){
                    put(lowGrainKey,getHighGrainKey(highLevel,lowLevel,lowGrainKey));
                }
            }};
        }
    }
    public Set<String> getGrainLevelSet(int level){
        if(level==1){
            return data.rowKeySet();
        }
        else{
            Set<String> result=new HashSet<>();
            for(String rowKey:data.rowKeySet()){
                result.add(getDataByLevel(rowKey,level));
            }
            return result;
        }
    }
    public String getHighGrainKey(int highLevel,int lowLevel,String key){
        if(highLevel<lowLevel){
            return null;
        }
        else{
            if(lowLevel==1){
                return getDataByLevel(key,highLevel);
            }
            else{
                for(String rowKey:data.rowKeySet()){
                    String lowLevelKey = getDataByLevel(rowKey,lowLevel);
                    if(lowLevelKey.equals(key)){
                        return getDataByLevel(rowKey,highLevel);
                    }
                }
                return null;
            }
        }
    }
    public Set<String> getLowGrainSet(int highLevel,int lowLevel,String key){
        Set<String> result=new HashSet<>();
        if(highLevel<lowLevel){
            return null;
        }
        else {
            for (String rowKey : data.rowKeySet()) {
                String highLevelKey = getDataByLevel(rowKey, highLevel);
                if(highLevelKey.equals(key)){
                    if (highLevel == lowLevel) {
                        result.add(highLevelKey);
                        return result;
                    }
                    else{
                        String lowLevelKey = getDataByLevel(rowKey,lowLevel);
                        result.add(lowLevelKey);
                    }
                }
            }
            return result;
        }
    }
}
