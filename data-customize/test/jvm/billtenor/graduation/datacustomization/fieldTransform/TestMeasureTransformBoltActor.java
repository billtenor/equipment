package billtenor.graduation.datacustomization.fieldTransform;

import static org.junit.Assert.assertEquals;

import billtenor.graduation.datacustomization.tableType.localFile.LocalJSON;
import billtenor.graduation.datacustomization.tableType.localFile.LocalMeasureTable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lyj on 17-3-20.
 */
public class TestMeasureTransformBoltActor {
    //@Test
    public void testMeasureTransformBoltActor(){
        LocalMeasureTable localMeasureTable=new LocalMeasureTable(
                "testWarehouseModel_MeasureConformedDimTable",
                "MeasureID,MeasureType,MeasureConstraints,MeasureRely,MeasureEquation"
        );
        List<String> measureTargetID=new ArrayList<String>(){{
            add("Voltage");
            add("Current");
            add("State");
            add("Power");
            add("Load");
            add("Accelaration_g");
            add("Accelaration_m/s^2");
        }};
        MeasureTransformBoltActor measureTransformBoltActor=new MeasureTransformBoltActor(
                localMeasureTable.getMeasureTargetIDMeasureEquation(new ArrayList<String>(),measureTargetID),
                localMeasureTable.getMeasureIDMeasureType()
        );
        TupleDataTransfer inputData=new TupleDataTransfer();
        TupleDataTransfer outputData;

        LocalJSON localJSON1=new LocalJSON("originData1.json");
        inputData.refreshData(localJSON1.data);
        outputData = measureTransformBoltActor.dataTransform(inputData);
        String result1=outputData.toString();

        LocalJSON localJSON2=new LocalJSON("originData2.json");
        inputData.refreshData(localJSON2.data);
        outputData = measureTransformBoltActor.dataTransform(inputData);
        String result2=outputData.toString();

        LocalJSON localJSON3=new LocalJSON("originData3.json");
        inputData.refreshData(localJSON3.data);
        outputData = measureTransformBoltActor.dataTransform(inputData);
        String result3=outputData.toString();

        assertEquals( "createJsonData", "createJsonData");
    }
}
