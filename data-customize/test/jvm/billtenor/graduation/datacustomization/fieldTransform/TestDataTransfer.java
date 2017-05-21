package billtenor.graduation.datacustomization.fieldTransform;

import static org.junit.Assert.assertEquals;

import billtenor.graduation.datacustomization.tableType.localFile.LocalJSON;

/**
 * Created by lyj on 17-3-27.
 */
public class TestDataTransfer {
    //@Test
    public void dataTransferTest(){
        LocalJSON localJSON=new LocalJSON("stormData.json");
        TupleDataTransfer tupleDataTransfer =new TupleDataTransfer(localJSON.data);
        String result= tupleDataTransfer.toString();
        TupleDataTransfer tupleDataTransfer2 =new TupleDataTransfer(result);
        tupleDataTransfer.refreshData(result);
        assertEquals( "createJsonData", "createJsonData");
    }
}
