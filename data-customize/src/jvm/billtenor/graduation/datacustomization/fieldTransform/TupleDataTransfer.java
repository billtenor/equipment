package billtenor.graduation.datacustomization.fieldTransform;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.storm.tuple.Tuple;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lyj on 17-3-28.
 */
public class TupleDataTransfer implements java.io.Serializable{
    public String spaceID;
    public Map<String,Long> timeStamp;
    public Table<String,String,Object> data;
    private Long defaultTimeStamp;

    public TupleDataTransfer(){
        timeStamp = new HashMap<>();
        data = HashBasedTable.create();
    }
    public TupleDataTransfer(String stringObject){
        timeStamp = new HashMap<>();
        data = HashBasedTable.create();
        refreshData(stringObject);
    }
    public void cleanTimeStamp(){
        timeStamp.clear();
    }
    public void cleanData(){
        data.clear();
    }
    private void clean(){
        timeStamp.clear();
        data.clear();
    }
    public Long getDefaultTimeStamp(){
        return defaultTimeStamp;
    }
    public void setDefaultTimeStamp(String timeStampID){
        defaultTimeStamp = timeStamp.get(timeStampID);
    }
    public void setDefaultTimeStamp(Long timeStamp){
        defaultTimeStamp = timeStamp;
    }
    public void refreshData(Object object){
        if(object instanceof String)
            refreshData((String)object);
        else if(object instanceof Tuple)
            refreshData((Tuple)object);
    }
    public void refreshData(Tuple tuple){
        refreshData(tuple.getStringByField("data"));
    }
    public void refreshData(String stringObject){
        clean();
        JSONParser parser=new JSONParser();
        try {
            JSONObject jsonObject=(JSONObject)parser.parse(stringObject);
            spaceID = (String) jsonObject.get("spaceID");
            defaultTimeStamp = (Long) jsonObject.get("defaultTimeStamp");
            JSONObject timeStampJSON = (JSONObject) jsonObject.get("timeStamp");
            for (String factID : (Set<String>) timeStampJSON.keySet()) {
                timeStamp.put(factID, (Long) timeStampJSON.get(factID));
            }
            JSONArray dataJSONs = (JSONArray) jsonObject.get("data");
            for (int i = 0; i < dataJSONs.size(); i++) {
                JSONObject dataJSON = (JSONObject) dataJSONs.get(i);
                String measureID = (String) dataJSON.get("measureID");
                JSONObject factsJSON = (JSONObject) dataJSON.get("facts");
                for (String factID : (Set<String>) factsJSON.keySet()) {
                    data.put(measureID, factID, factsJSON.get(factID));
                }
            }
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }
    public String toString(){
        JSONObject jsonOutput=new JSONObject(){{
            put("spaceID",spaceID);
            put("defaultTimeStamp",defaultTimeStamp);
            put("timeStamp",new JSONObject(){{
                for(String factID:timeStamp.keySet()){
                    put(factID,timeStamp.get(factID));
                }
            }});
            put("data",new JSONArray(){{
                for(final String measureID:data.rowKeySet()){
                    final Map<String,Object> facts = data.row(measureID);
                    add(new JSONObject(){{
                        put("measureID",measureID);
                        put("facts",new JSONObject(){{
                            for(String factID:facts.keySet()){
                                put(factID,facts.get(factID));
                            }
                        }});
                    }});
                }
            }});
        }};
        return jsonOutput.toJSONString();
    }
    public static String getData(Tuple tuple){
        return tuple.getStringByField("data");
    }
    public static String getKey(Tuple tuple){
        return tuple.getStringByField("key");
    }
    public String getTupleMark(){
        if(spaceID==null)
            return null;
        else
            return spaceID+"_"+defaultTimeStamp.toString();
    }
}
