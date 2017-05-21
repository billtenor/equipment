package billtenor.graduation.datacustomization.fieldTransform;


import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

/**
 * Created by lyj on 17-3-27.
 */
public class NoBlockAggregateBoltActor extends BaseAggregateBoltActor {
    private Map<String,Set<String>> sourceDataStruct;
    private Set<String> sourceTimeStampSet;

    public NoBlockAggregateBoltActor(Map<String,String> factIDFunction,
                                     Map<String,String> measureIDMeasureType,
                                     Map<String,String[]> factIDTargetFactIDs){
        super(factIDFunction,measureIDMeasureType,factIDTargetFactIDs);
        sourceDataStruct=new HashMap<>();
        sourceTimeStampSet=new HashSet<>();
    }
    public TupleDataTransfer init(TupleDataTransfer inTransfer,String spaceID, String defaultTimeStampID,Map<String,Object> para){
        TupleDataTransfer outTransfer = new TupleDataTransfer();
        //initData
        for (String measureID : inTransfer.data.rowKeySet()) {
            for (String factID : inTransfer.data.row(measureID).keySet()) {
                if (measureMatchFact(measureID, factID, "single")) {
                    if(!sourceDataStruct.containsKey(measureID)) {
                        sourceDataStruct.put(measureID, new HashSet<String>());
                    }
                    sourceDataStruct.get(measureID).add(factID);
                    Object data = inTransfer.data.get(measureID,factID);
                    for(String targetFactID:factIDTargetFactIDs.get(factID)){
                        outTransfer.data.put(measureID, targetFactID, data);
                        AggregateCalculate.initPara(factIDFunction.get(targetFactID),para);
                    }
                }
            }
        }
        //initTimeStamp
        if (outTransfer.data.rowKeySet().size() != 0) {
            for (String timeStampFactID : inTransfer.timeStamp.keySet()) {
                if (factIDTargetFactIDs.keySet().contains(timeStampFactID)) {
                    sourceTimeStampSet.add(timeStampFactID);
                    Long timeStamp = inTransfer.timeStamp.get(timeStampFactID);
                    for(String targetFactID:factIDTargetFactIDs.get(timeStampFactID)){
                        outTransfer.timeStamp.put(targetFactID, timeStamp);
                        AggregateCalculate.initPara(factIDFunction.get(targetFactID),para);
                    }
                }
            }
        }
        outTransfer.spaceID=spaceID;
        outTransfer.setDefaultTimeStamp(defaultTimeStampID);
        return outTransfer;
    }
    public void aggressive(TupleDataTransfer outTransfer,TupleDataTransfer inTransfer,String defaultTimeStampID, Map<String,Object> para){
        for(String sourceFactID:sourceTimeStampSet){
            String[] targetFactIDs = factIDTargetFactIDs.get(sourceFactID);
            for(String targetFactID:targetFactIDs){
                Long timeStampOut = (Long)AggregateCalculate.calculate(
                        inTransfer.timeStamp.get(sourceFactID),outTransfer.timeStamp.get(targetFactID),
                        factIDFunction.get(targetFactID),para
                );
                outTransfer.timeStamp.put(targetFactID,timeStampOut);
            }
        }
        outTransfer.setDefaultTimeStamp(outTransfer.timeStamp.get(defaultTimeStampID));
        for(String measureID:sourceDataStruct.keySet()){
            for(String factID:sourceDataStruct.get(measureID)){
                String[] targetFactIDs=factIDTargetFactIDs.get(factID);
                for(String targetFactID:targetFactIDs){
                    Object valueOut = AggregateCalculate.calculate(
                            inTransfer.data.get(measureID,factID),outTransfer.data.get(measureID,targetFactID),
                            factIDFunction.get(targetFactID),para
                    );
                    outTransfer.data.put(measureID,targetFactID,valueOut);
                }
            }
        }
        AggregateCalculate.iteratePara(para);
    }
}
