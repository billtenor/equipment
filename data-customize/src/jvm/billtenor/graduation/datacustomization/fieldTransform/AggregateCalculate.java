package billtenor.graduation.datacustomization.fieldTransform;

import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import clojure.lang.Obj;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

/**
 * Created by lyj on 17-3-24.
 */
public class AggregateCalculate {
    public final static Map<String,String[]> functionType=new HashMap<String, String[]>(){{
        put("SumNumber",new String[]{"Number","entire,single"});
        put("MaxNumber",new String[]{"Number","entire,single"});
        put("MinNumber",new String[]{"Number","entire,single"});
        put("AvgNumber",new String[]{"Number","entire,single"});
        put("EarliestTime",new String[]{"Time","entire,single"});
        put("LatestTime",new String[]{"Time","entire,single"});
        put("AvgTime",new String[]{"Time","entire,single"});
        //put("MidNumber",new String[]{"Number","entire"});
        //put("CountNumber",new String[]{"Number","entire,single"});
    }};
    public static Object calculate(Object[] data,String function){
        if(function.equals("EarliestTime")){
            Long result=(Long)data[0];
            for(int i=1;i<data.length;i++){
                if((Long)data[i]<result)
                    result=(Long)data[i];
            }
            return result;
        }
        else if(function.equals("LatestTime")){
            Long result=(Long)data[0];
            for(int i=1;i<data.length;i++){
                if((Long)data[i]>result)
                    result=(Long)data[i];
            }
            return result;
        }
        else if(function.equals("AvgTime")){
            Long result=(Long)data[0];
            double count=1;
            for(int i=1;i<data.length;i++){
                Double buff = result.doubleValue()*(count/(count+1)) + ((Long)data[i]).doubleValue()/(count+1);
                result = buff.longValue();
                count++;
            }
            return result;
        }
        else if(function.equals("SumNumber")){
            if(data[0].getClass().getName().equals(Long.class.getName())) {
                Long result = 0l;
                for(int i=0;i<data.length;i++){
                    result+=(Long)data[0];
                }
                return result;
            }
            else{
                Double result = 0.0;
                for(int i=0;i<data.length;i++){
                    result+=(Double) data[i];
                }
                return result;
            }
        }
        else if(function.equals("MaxNumber")){
            if(data[0].getClass().getName().equals(Long.class.getName())) {
                Long result = (Long)data[0];
                for(int i=1;i<data.length;i++){
                    if((Long)data[i]>result)
                        result=(Long)data[i];
                }
                return result;
            }
            else{
                Double result = (Double)data[0];
                for(int i=1;i<data.length;i++){
                    if((Double)data[i]>result)
                        result=(Double)data[i];
                }
                return result;
            }
        }
        else if(function.equals("MinNumber")){
            if(data[0].getClass().getName().equals(Long.class.getName())) {
                Long result = (Long)data[0];
                for(int i=1;i<data.length;i++){
                    if((Long)data[i]<result)
                        result=(Long)data[i];
                }
                return result;
            }
            else{
                Double result = (Double)data[0];
                for(int i=1;i<data.length;i++){
                    if((Double)data[i]<result)
                        result=(Double)data[i];
                }
                return result;
            }
        }
        else if(function.equals("AvgNumber")){
            if(data[0].getClass().getName().equals(Long.class.getName())) {
                Long sum = 0l;
                int count=0;
                for(int i=1;i<data.length;i++){
                    sum+=(Long)data[i];
                    count+=1;
                }
                return sum/count;
            }
            else{
                Double sum = 0.0;
                int count=0;
                for(int i=1;i<data.length;i++){
                    sum+=(Double) data[i];
                    count+=1;
                }
                return sum/count;
            }
        }
        else{
            return null;
        }
    }
    public static void initPara(String function,Map<String,Object> para){
        if(function.equals("AvgNumber")||function.equals("AvgTime")){
            if(!para.containsKey("count")){
                Integer count=1;
                para.put("count",count);
            }
        }
    }
    public static Object calculate(Object newData,Object preData,String function,Map<String,Object> para){
        if(function.equals("AvgNumber")||function.equals("AvgTime")){
            if(preData.getClass().getName().equals(Long.class.getName())) {
                Integer count= (Integer) para.get("count");
                Double sum = ((Long)preData).doubleValue()*count;
                sum+=((Long)newData).doubleValue();
                Double result = sum/(count+1);
                return result.longValue();
            }
            else{
                Integer count= (Integer) para.get("count");
                Double sum = (Double)preData*count;
                sum+=(Double)newData;
                return sum/(count+1);
            }
        }
        else if(function.equals("SumNumber")){
            if(preData.getClass().getName().equals(Long.class.getName())) {
                Long result = (Long)preData + (Long) newData;
                return result;
            }
            else{
                Double result = (Double)preData + (Double) newData;
                return result;
            }
        }
        else if(function.equals("MaxNumber")){
            if(preData.getClass().getName().equals(Long.class.getName())) {
                if((Long)newData>(Long)preData)
                    return newData;
                else
                    return preData;
            }
            else{
                if((Double)newData>(Double)preData)
                    return newData;
                else
                    return preData;
            }
        }
        else if(function.equals("MinNumber")){
            if(preData.getClass().getName().equals(Long.class.getName())) {
                if((Long)newData<(Long)preData)
                    return newData;
                else
                    return preData;
            }
            else{
                if((Double)newData<(Double)preData)
                    return newData;
                else
                    return preData;
            }
        }
        else if(function.equals("EarliestTime")){
            if((Long)newData<(Long)preData)
                return newData;
            else
                return preData;
        }
        else if(function.equals("LatestTime")){
            if((Long)newData>(Long)preData)
                return newData;
            else
                return preData;
        }
        return preData;
    }
    public static void iteratePara(Map<String,Object> para){
        for(String key:para.keySet()){
            if(key.equals("count")){
                Integer result = (Integer)para.get(key)+1;
                para.put(key,result);
            }
        }
    }
}
