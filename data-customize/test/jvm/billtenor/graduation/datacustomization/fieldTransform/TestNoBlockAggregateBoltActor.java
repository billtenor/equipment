package billtenor.graduation.datacustomization.fieldTransform;

import billtenor.graduation.datacustomization.tableType.BaseFactsTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalFactTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalJSON;
import billtenor.graduation.datacustomization.tableType.localFile.LocalMeasureTable;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by lyj on 17-3-25.
 */
public class TestNoBlockAggregateBoltActor {
    //@Test
    public void testNoBlockAggregateBoltActor(){
        LocalMeasureTable measureTable=new LocalMeasureTable(
                "testWarehouseModel_MeasureConformedDimTable",
                "MeasureID,MeasureType,MeasureConstraints,MeasureRely,MeasureEquation"
        );
        BaseFactsTable factsTable=new LocalFactTable(
                "testWarehouseModel_FactsConformedTable",
                "*"
        );
        LocalJSON localJSON=new LocalJSON("stormData.json");
        String jsonData=localJSON.data;
        Map<String,String[]> factIDTargetFactIDs=new HashMap<String, String[]>(){{
            put("1",new String[]{"10","11","12","13"});
            put("2",new String[]{"16"});
        }};
        NoBlockAggregateBoltActor actor =new NoBlockAggregateBoltActor(
                factsTable.getFactsIDFunction(),
                measureTable.getMeasureIDMeasureType(),
                factIDTargetFactIDs);
        TupleDataTransfer inputData = new TupleDataTransfer();
        inputData.refreshData(jsonData);
        Map<String,Object> para = new HashedMap();
        TupleDataTransfer outputData = actor.init(inputData,inputData.spaceID,"16",para);
        for(int i=1;i<10;i++){
            actor.aggressive(outputData,inputData,"16",para);
        }
        assertEquals( "createJsonData", "createJsonData");
    }
}
