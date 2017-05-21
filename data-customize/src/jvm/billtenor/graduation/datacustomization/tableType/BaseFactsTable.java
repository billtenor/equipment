package billtenor.graduation.datacustomization.tableType;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

/**
 * Created by lyj on 17-3-25.
 */
public abstract class BaseFactsTable extends BaseTable{
    protected Table<String,String,String> data;
    protected abstract void getData();
    protected BaseFactsTable(){
        data = HashBasedTable.create();
    }
    public Map<String,String> getFactsIDFunction(){
        return switchMap(data.column("FactFunction"));
    }
    public String getFatherFactID(String factID){
        return data.get(factID,"FatherFactID");
    }
    public String getDefaultTimeStampFactID(Collection<String> factIDs){
        int minID=Integer.MAX_VALUE;
        for(String factID:factIDs){
            if(data.get(factID,"FactType").equals("Time")){
                int thisFactID = Integer.parseInt(factID);
                if(thisFactID<minID)
                    minID=thisFactID;
            }
        }
        return Integer.toString(minID);
    }
    public int getSpaceGrain(String factID){
        return Integer.parseInt(data.get(factID,"SpaceGrain"));
    }
    public Long getTimeGap(String factID){
        return Long.parseLong(data.get(factID,"TimeGap"));
    }
    public Map<String,String[]> getFactIDsTargetFactIDs(Set<String> facIDs){
        Map<String,List<String>> buff = new HashMap<>();
        for(String factID:facIDs){
            String fatherFactID = getFatherFactID(factID);
            if(!buff.keySet().contains(fatherFactID)){
                buff.put(fatherFactID,new ArrayList<String>());
            }
            buff.get(fatherFactID).add(factID);
        }
        Map<String,String[]> result = new HashMap<>();
        for(String factID:buff.keySet()){
            List<String> targetFactIDs = buff.get(factID);
            result.put(factID,targetFactIDs.toArray(new String[targetFactIDs.size()]));
        }
        return result;
    }
}
