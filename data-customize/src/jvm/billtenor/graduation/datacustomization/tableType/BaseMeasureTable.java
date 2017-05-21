package billtenor.graduation.datacustomization.tableType;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

/**
 * Created by lyj on 17-3-25.
 */
public abstract class BaseMeasureTable extends BaseTable{
    protected Table<String,String,String> data;
    protected abstract void getData();
    protected BaseMeasureTable(){
        data= HashBasedTable.create();
    }
    public Map<String,String> getMeasureIDMeasureType(){
        return switchMap(data.column("MeasureType"));
    }
    public String getFieldFromMeasureID(String measureID,String field){
        return data.get(measureID,field);
    }
    public Map<String,String> getMeasureTargetIDMeasureEquation(
            Collection<String> measureTargetDoneIDs,Collection<String> measureTargetToDoIDs
    ){
        Map<String,String> result=new HashMap<>();
        for(String measureTargetID:measureTargetToDoIDs){
            String equation=data.get(measureTargetID,"MeasureEquation");
            if(equation!=null)
                result.put(measureTargetID,equation);
        }
        for(String measureTargetID:measureTargetDoneIDs){
            String equation="";
            result.put(measureTargetID,equation);
        }
        if(result.keySet().size()==0)
            return null;
        else
            return result;
    }
    public Set<String> getMeasureID(){
        return switchSet(data.rowKeySet());
    }
    public String[] getRelyMeasureID(String measureID){
        String measureRely = data.get(measureID,"MeasureRely");
        if(measureRely.equals("")){
            return null;
        }
        else{
            return measureRely.split(",");
        }
    }
    public Set<String> getFreeMeasureID(){
        Set<String> result = new HashSet<>();
        for(String measureID:data.rowKeySet()){
            if(getRelyMeasureID(measureID)==null)
                result.add(measureID);
        }
        return result;
    }
}
