package billtenor.graduation.datacustomization.fieldTransform;


import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.*;

/**
 * Created by lyj on 17-3-27.
 */
public class BlockAggregateBoltActor extends BaseAggregateBoltActor {

    private Table<String,String,List<Object>> dataBuff;
    private Map<String,List<Long>> timeStampBuff;
    private TupleDataTransfer inputTupleDataTransfer;
    private TupleDataTransfer outputTupleDataTransfer;
    private boolean dataBuffStructInit;

    public BlockAggregateBoltActor(Map<String,String> factIDFunction,
                                   Map<String,String> measureIDMeasureType,
                                   Map<String,String[]> factIDTargetFactIDs){
        super(factIDFunction,measureIDMeasureType,factIDTargetFactIDs);

        this.dataBuff=HashBasedTable.create();
        this.timeStampBuff=new HashMap<>();
        this.inputTupleDataTransfer =new TupleDataTransfer();
        this.outputTupleDataTransfer =new TupleDataTransfer();
        this.dataBuffStructInit=false;
    }
    private void clearBuff(){
        for(String measureID:dataBuff.rowKeySet()){
            for(String factID:dataBuff.row(measureID).keySet()){
                dataBuff.get(measureID,factID).clear();
            }
        }
        for(String timeStampFactID:timeStampBuff.keySet()){
            timeStampBuff.get(timeStampFactID).clear();
        }
    }
    private void initBuffStruct(String dataJSON){
        if(!dataBuffStructInit) {
            inputTupleDataTransfer.refreshData(dataJSON);
            //initDataBuffStruct
            for (String measureID : inputTupleDataTransfer.data.rowKeySet()) {
                for (String factID : inputTupleDataTransfer.data.row(measureID).keySet()) {
                    if (measureMatchFact(measureID, factID, "entire")) {
                        dataBuff.put(measureID, factID, new ArrayList());
                    }
                }
            }
            //initTimeStampBuffStruct
            if (dataBuff.rowKeySet().size() != 0) {
                for (String timeStampFactID : inputTupleDataTransfer.timeStamp.keySet()) {
                    if (factIDTargetFactIDs.keySet().contains(timeStampFactID)) {
                        timeStampBuff.put(timeStampFactID, new ArrayList());
                    }
                }
            }
            dataBuffStructInit = true;
        }
    }
    public TupleDataTransfer aggressive(String[] dataJSON, String spaceID, String defaultTimeStampID){
        //initBuffStruct
        initBuffStruct(dataJSON[0]);
        if(dataBuff.isEmpty()||timeStampBuff.isEmpty())
            return null;
        //import other data
        for(int i=0;i<dataJSON.length;i++) {
            inputTupleDataTransfer.refreshData(dataJSON[i]);
            //read the data in dataJSON to dataBuff
            for (String measureID : dataBuff.rowKeySet()) {
                for (String factID : dataBuff.row(measureID).keySet()) {
                    Object value = inputTupleDataTransfer.data.get(measureID, factID);
                    if (value != null)
                        dataBuff.get(measureID, factID).add(value);
                }
            }
            //read the timeStamp in dataJSON to timeStampBuff
            for (String timeStampFactID : timeStampBuff.keySet()) {
                Long time = inputTupleDataTransfer.timeStamp.get(timeStampFactID);
                timeStampBuff.get(timeStampFactID).add(time);
            }
        }
        //start the aggressive calculate
        outputTupleDataTransfer.spaceID=spaceID;
        outputTupleDataTransfer.cleanTimeStamp();
        for(String timeStampFactID:timeStampBuff.keySet()){
            String[] targetFactIDs = factIDTargetFactIDs.get(timeStampFactID);
            for(int i=0;i<targetFactIDs.length;i++){
                List<Long> timeStampIn = timeStampBuff.get(timeStampFactID);
                Long timeStampOut = (Long)AggregateCalculate.calculate(
                        timeStampIn.toArray(new Long[timeStampIn.size()]),
                        factIDFunction.get(targetFactIDs[i])
                        );
                outputTupleDataTransfer.timeStamp.put(targetFactIDs[i],timeStampOut);
            }
        }
        outputTupleDataTransfer.setDefaultTimeStamp(defaultTimeStampID);
        outputTupleDataTransfer.cleanData();
        for(String measureID:dataBuff.rowKeySet()){
            for(String factID:dataBuff.row(measureID).keySet()){
                String[] thisFactIDTargetFactID=factIDTargetFactIDs.get(factID);
                for(int i=0;i<thisFactIDTargetFactID.length;i++) {
                    List<Object> valueIn=dataBuff.get(measureID,factID);
                    Object valueOut = AggregateCalculate.calculate(
                            valueIn.toArray(new Object[valueIn.size()]),
                            factIDFunction.get(thisFactIDTargetFactID[i])
                    );
                    outputTupleDataTransfer.data.put(measureID,thisFactIDTargetFactID[i],valueOut);
                }
            }
        }
        clearBuff();
        return outputTupleDataTransfer;
    }
}
