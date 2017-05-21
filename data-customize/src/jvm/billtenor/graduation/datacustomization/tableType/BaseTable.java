package billtenor.graduation.datacustomization.tableType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lyj on 17-3-29.
 */
public abstract class BaseTable implements java.io.Serializable{
    protected Map<String,String> switchMap(Map<String,String> inputMap){
        Map<String,String> outputMap = new HashMap<>();
        for(String key:inputMap.keySet()){
            outputMap.put(key,inputMap.get(key));
        }
        return outputMap;
    }
    protected Set<String> switchSet(Set<String> inputSet){
        Set<String> outputSet = new HashSet<>();
        for(String key:inputSet){
            outputSet.add(key);
        }
        return outputSet;
    }
}
