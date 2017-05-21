package billtenor.graduation.datacustomization.fieldTransform;
import static org.junit.Assert.assertEquals;

import billtenor.graduation.datacustomization.tableType.BaseFactsTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalFactTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalJSON;
import billtenor.graduation.datacustomization.tableType.localFile.LocalMeasureTable;

import java.util.*;

/**
 * Created by lyj on 17-3-25.
 */
public class TestAggregateBoltActor {
    //@Test
    public void testSpaceAggregateBoltActor(){
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
        BlockAggregateBoltActor blockAggregateBoltActor =new BlockAggregateBoltActor(
                factsTable.getFactsIDFunction(),
                measureTable.getMeasureIDMeasureType(),
                factIDTargetFactIDs);
        String[] jsonDatas=new String[]{
          jsonData,jsonData,jsonData,jsonData,jsonData,jsonData,jsonData,jsonData,jsonData,jsonData
        };
        TupleDataTransfer outputData;
        outputData = blockAggregateBoltActor.aggressive(jsonDatas,"aaa", "16");
        String result1= outputData.toString();
        outputData = blockAggregateBoltActor.aggressive(jsonDatas,"aaa", "16");
        String result2= outputData.toString();
        outputData = blockAggregateBoltActor.aggressive(jsonDatas,"aaa", "16");
        String result3= outputData.toString();

        assertEquals( "createJsonData", "createJsonData");
    }
}
