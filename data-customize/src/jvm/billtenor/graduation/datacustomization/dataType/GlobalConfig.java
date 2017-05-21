package billtenor.graduation.datacustomization.dataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lyj on 17-3-26.
 */
public class GlobalConfig implements java.io.Serializable {
    public final static Map<String,Set<String>> dataTypeMap=new HashMap<String, Set<String>>(){{
        put("Number",new HashSet<String>(){{add("Long");add("Double");}});
        put("Enum",new HashSet<String>(){{add("Boolean");add("String");}});
    }};
    private final static String originNumberFactID="1";
    private final static String originTimeFactID="2";
    private final static String originEnumFactID="3";
    public static String getOriginTimeFactID(){
        return originTimeFactID;
    }
    public static String getOriginFactID(String measureType){
        String type=null;
        for(String s:dataTypeMap.keySet()){
            if(dataTypeMap.get(s).contains(measureType)){
                type=s;
                break;
            }
        }
        if(type==null)
            return null;
        else if(type.equals("Number")){
            return originNumberFactID;
        }
        else if(type.equals("Enum")){
            return originEnumFactID;
        }
        else
            return null;
    }
    public final static String[] outputFields=new String[]{
        "key","data"
    };
}
