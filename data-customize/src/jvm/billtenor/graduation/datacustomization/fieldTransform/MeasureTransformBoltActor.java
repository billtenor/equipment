package billtenor.graduation.datacustomization.fieldTransform;

import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import org.apache.commons.jexl3.*;
import java.util.Map;

/**
 * Created by lyj on 17-3-14.
 */
public class MeasureTransformBoltActor implements java.io.Serializable{
    private Map<String,String> measureTargetIDMeasureEquation;
    private Map<String,String> measureIDMeasureType;
    private TupleDataTransfer outputData;
    private boolean measureNotCalculate;

    public MeasureTransformBoltActor(Map<String,String> measureTargetIDMeasureEquation,
                                     Map<String,String> measureIDMeasureType){
        this.measureTargetIDMeasureEquation=measureTargetIDMeasureEquation;
        this.measureIDMeasureType=measureIDMeasureType;
        this.outputData=new TupleDataTransfer();
        this.measureNotCalculate=true;
        for(String measureTargetID:measureTargetIDMeasureEquation.keySet()){
            this.measureNotCalculate&=measureTargetIDMeasureEquation.get(measureTargetID).isEmpty();
        }
    }
    Object measureTransformCalculate(JexlContext jexlContext,String measureTargetID){
        Object output=null;
        try {
            JexlEngine jexl = new JexlBuilder().create();
            JexlExpression expression = jexl.createExpression(measureTargetIDMeasureEquation.get(measureTargetID));
            output = expression.evaluate(jexlContext);
        }
        catch (Exception e){
        }
        if(output==null){
            return null;
        }
        else{
            String measureType=measureIDMeasureType.get(measureTargetID);
            if(measureType.equals("Long")){
                Long number=((Number)output).longValue();
                return number;
            }
            else if(measureType.equals("Double")){
                Double number=((Number)output).doubleValue();
                return number;
            }
            else{
                return output;
            }
        }
    }
    public TupleDataTransfer dataTransform(TupleDataTransfer inputData){
        outputData.spaceID=inputData.spaceID;
        outputData.timeStamp=inputData.timeStamp;
        outputData.setDefaultTimeStamp(inputData.getDefaultTimeStamp());

        outputData.cleanData();
        JexlContext jexlContext=null;
        if(!measureNotCalculate){
            jexlContext = new MapContext();
            for(String measureID:inputData.data.rowKeySet()){
                String measureType=measureIDMeasureType.get(measureID);
                String factID=GlobalConfig.getOriginFactID(measureType);
                Object value=inputData.data.get(measureID,factID);
                String key="data_"+measureID;
                jexlContext.set(key,value);
            }
        }
        for(String measureTargetID:measureTargetIDMeasureEquation.keySet()){
            String measureTargetType=measureIDMeasureType.get(measureTargetID);
            String factID=GlobalConfig.getOriginFactID(measureTargetType);
            boolean thisMeasureNotCalculate=measureTargetIDMeasureEquation.get(measureTargetID).isEmpty();
            Object value;
            if(thisMeasureNotCalculate) {
                value = inputData.data.get(measureTargetID, factID);
            }
            else{
                value = measureTransformCalculate(jexlContext,measureTargetID);
            }
            if (value != null)
                outputData.data.put(measureTargetID, factID, value);
        }

        if(outputData.data.rowKeySet().size()!=0){
            return outputData;
        }
        else{
            return null;
        }
    }
}

