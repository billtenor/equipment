package billtenor.graduation.datacustomization.fieldTransform;

import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by lyj on 17-3-26.
 */
public class KafkaSpoutSchemeActor implements java.io.Serializable{
    private Map<String,String> measureIDMeasureType;
    private TupleDataTransfer outputTupleDataTransfer;

    public KafkaSpoutSchemeActor(Map<String,String> measureIDMeasureType){
        this.measureIDMeasureType=measureIDMeasureType;
        this.outputTupleDataTransfer =new TupleDataTransfer();
    }
    public TupleDataTransfer dataTransform(String stringInput) {
        JSONParser parser=new JSONParser();
        try{
            JSONObject jsonInput = (JSONObject)parser.parse(stringInput);

            outputTupleDataTransfer.spaceID=(String)jsonInput.get("spaceID");

            outputTupleDataTransfer.cleanTimeStamp();
            String timeStamp = (String)jsonInput.get("timeStamp");
            DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date date = dateFormat.parse(timeStamp);
            Long timeStampLong=date.getTime();
            outputTupleDataTransfer.timeStamp.put(GlobalConfig.getOriginTimeFactID(),timeStampLong);
            outputTupleDataTransfer.setDefaultTimeStamp(timeStampLong);

            outputTupleDataTransfer.cleanData();
            JSONArray inputData = (JSONArray)jsonInput.get("data");
            for(int i=0;i<inputData.size();i++){
                JSONObject buff=(JSONObject)inputData.get(i);
                String measureID=(String)buff.get("dataDimTableKey");
                String factID=GlobalConfig.getOriginFactID(measureIDMeasureType.get(measureID));
                Object value=buff.get("value");
                outputTupleDataTransfer.data.put(measureID,factID,value);
            }
            return outputTupleDataTransfer;
        }
        catch (ParseException e){
            e.printStackTrace();
        }
        catch (java.text.ParseException e){
            e.printStackTrace();
        }
        return null;
    }
}
