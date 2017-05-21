package billtenor.graduation.datacustomization.statusCheck;

import billtenor.graduation.datacustomization.tableType.BaseMeasureTable;

import java.util.Set;

/**
 * Created by yanjun on 17-4-5.
 */
public abstract class BaseMeasureCheck implements java.io.Serializable{
    protected final String selectField="MeasureID,MeasureName,MeasureDescription,MeasureType,MeasureConstraints,MeasureUnit,MeasureRely";
    protected BaseMeasureTable measureTable;
    public MeasureDataCreator[] measureDataCreators;
    private Set<String> measureIDs;

    protected abstract void initMeasureTable(String dataWarehouseModelID);
    public int getSize(){
        return this.measureIDs.size();
    }
    protected BaseMeasureCheck(String dataWarehouseID){
        initMeasureTable(dataWarehouseID);
        this.measureIDs = measureTable.getFreeMeasureID();
        int size = getSize();
        this.measureDataCreators=new MeasureDataCreator[size];
        int i=0;
        for(String measureID:measureIDs){
            
            this.measureDataCreators[i] = new MeasureDataCreator(
                    measureID,
                    measureTable.getFieldFromMeasureID(measureID,"MeasureName"),
                    measureTable.getFieldFromMeasureID(measureID,"MeasureDescription"),
                    measureTable.getFieldFromMeasureID(measureID,"MeasureType"),
                    measureTable.getFieldFromMeasureID(measureID,"MeasureConstraints"),
                    measureTable.getFieldFromMeasureID(measureID,"MeasureUnit")
            );
            i++;
        }
    }
}
