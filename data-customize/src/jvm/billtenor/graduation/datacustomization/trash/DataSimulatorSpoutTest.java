package billtenor.graduation.datacustomization.spout;

import static org.junit.Assert.assertEquals;

import billtenor.graduation.datacustomization.trash.LocalStatusCheck;
import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalSpaceTable;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by lyj on 17-2-11.
 */
public class DataSimulatorSpoutTest {
    //@Test
    public void testDataSimulatorSpout(){
    /*
        DataSimulatorSpout test=new DataSimulatorSpout("libuznaksier");
        test._rand=new Random();
        test._spaceDimTableKey = UUID.randomUUID().toString();
        String result = test.createJsonData();
        assertEquals( "createJsonData", result);
    */
        BaseSpaceTable spaceTable = new LocalSpaceTable(
                new HashMap<Integer, String>(){{
                    put(1,"spaceID");
                    put(2,"AreaID");
                    put(3,"ZoneID");
                }},
                "testWarehouseModel_SpaceConformedDimTable",
                "spaceID,AreaID,ZoneID"
        );
        LocalStatusCheck statusCheck = new LocalStatusCheck(1,2,spaceTable);
        statusCheck.setStatus("95135742",false);
        statusCheck.setStatus("21968543",false);
        statusCheck.setStatus("66511289",false);
        statusCheck.setStatus("21681231",false);
        statusCheck.setStatus("32195123",false);
        statusCheck.setStatus("21684213",false);
        statusCheck.setStatus("12354621",false);
        statusCheck.setStatus("21632456",false);
        statusCheck.setStatus("10324652",false);
        statusCheck.refresh();
        Set<String> activeNode = statusCheck.getActiveSetOfThis();
        return;
    }
    /*
    public static String createJsonData(){
        DataSimulatorSpout test=new DataSimulatorSpout("libuznaksier");
        test._rand=new Random();
        test._spaceDimTableKey = UUID.randomUUID().toString();
        return test.createJsonData();
    }
    */
}
