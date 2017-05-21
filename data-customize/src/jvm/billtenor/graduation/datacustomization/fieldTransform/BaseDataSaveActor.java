package billtenor.graduation.datacustomization.fieldTransform;

import billtenor.graduation.datacustomization.tableType.BaseDateTable;

import java.util.*;

/**
 * Created by lyj on 17-3-31.
 */
public abstract class BaseDataSaveActor implements java.io.Serializable{
    class DataSave{
        public String dateID;
        public String measureID;
        public String spaceID;
        public Object[] facts;
    }
    final private Map<String,String[]> tableNameFactIDs;
    private TupleDataTransfer tupleDataTransfer;

    protected BaseDataSaveActor(Map<String,String[]> tableNameFactIDs){
        this.tupleDataTransfer=new TupleDataTransfer();
        this.tableNameFactIDs = tableNameFactIDs;
    }
    public boolean dataTransfer(Object[] objects){
        Map<String,ArrayList<DataSave>> data = new HashMap<String, ArrayList<DataSave>>(){{
            for(String tableName:tableNameFactIDs.keySet()){
                put(tableName,new ArrayList<DataSave>());
            }
        }};
        for(int i=0;i<objects.length;i++){
            tupleDataTransfer.refreshData(objects[i]);
            String dateID = BaseDateTable.createDateID(tupleDataTransfer.getDefaultTimeStamp());
            String spaceID = tupleDataTransfer.spaceID;
            for(String tableName:tableNameFactIDs.keySet()) {
                String[] thisTableFactIDs = tableNameFactIDs.get(tableName);
                for(String measureID:tupleDataTransfer.data.rowKeySet()) {
                    boolean factsExist=true;
                    for (int factsIndex = 0; factsIndex < thisTableFactIDs.length; factsIndex++) {
                        boolean thisFactExist =
                                tupleDataTransfer.data.row(measureID).keySet().contains(thisTableFactIDs[factsIndex])
                                        | tupleDataTransfer.timeStamp.keySet().contains(thisTableFactIDs[factsIndex]);
                        if(!thisFactExist){
                            factsExist=false;
                            break;
                        }
                    }
                    if(factsExist) {
                        DataSave buff = new DataSave();
                        buff.dateID = dateID;
                        buff.spaceID = spaceID;
                        buff.measureID = measureID;
                        buff.facts = new Object[thisTableFactIDs.length];
                        for(int factsIndex=0; factsIndex < thisTableFactIDs.length; factsIndex++){
                            Object fact = tupleDataTransfer.data.get(measureID,thisTableFactIDs[factsIndex]);
                            if(fact==null)
                                fact = tupleDataTransfer.timeStamp.get(thisTableFactIDs[factsIndex]);
                            buff.facts[factsIndex] = fact;
                        }
                        data.get(tableName).add(buff);
                    }
                }
            }
        }
        for(String tableName:tableNameFactIDs.keySet()) {
            if (!save(data.get(tableName), tableName, tableNameFactIDs.get(tableName))){
                return false;
            }
        }
        return true;
    }
    abstract public boolean save(List<DataSave> data,String tableName,String[] factsTitle);
    abstract public void init();
    abstract public void close();
}
