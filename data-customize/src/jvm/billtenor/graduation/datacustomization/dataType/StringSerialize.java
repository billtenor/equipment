package billtenor.graduation.datacustomization.dataType;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lyj on 17-3-29.
 */
public class StringSerialize {
    public static String[] setToStrings(Set<String> inputSet){
        String[] outputStrings = new String[inputSet.size()];
        int i=0;
        for(String s:inputSet){
            outputStrings[i]=s;
            i++;
        }
        return outputStrings;
    }
    public static String stringsToString(String[] inputStrings){
        JSONArray jsonArray=new JSONArray();
        for(int i=0;i<inputStrings.length;i++){
            jsonArray.add(inputStrings[i]);
        }
        return jsonArray.toJSONString();
    }
    public static String[] stringToStrings(String inputString){
        JSONParser parser = new JSONParser();
        String[] outputStrings=null;
        try{
            JSONArray jsonArray = (JSONArray) parser.parse(inputString);
            outputStrings=new String[jsonArray.size()];
            for(int i=0;i<jsonArray.size();i++){
                outputStrings[i]=(String) jsonArray.get(i);
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return outputStrings;
    }
    public static String setToString(Set<String> inputSet){
        JSONArray jsonArray=new JSONArray();
        for(String s:inputSet){
            jsonArray.add(s);
        }
        return jsonArray.toJSONString();
    }
    public static Set<String> stringToSet(String inputString){
        JSONParser parser = new JSONParser();
        Set<String> outputSet = new HashSet<>();
        try{
            JSONArray jsonArray = (JSONArray) parser.parse(inputString);
            for(int i=0;i<jsonArray.size();i++){
                String s = (String)jsonArray.get(i);
                outputSet.add(s);
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        return outputSet;
    }
}
