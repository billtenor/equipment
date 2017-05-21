package billtenor.graduation.datacustomization.statusCheck;

import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalSpaceTable;
import billtenor.graduation.datacustomization.trash.LocalStatusCheck;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
/**
 * Created by lyj on 17-3-30.
 */
public class TestLocalStatusCheck {
    //@Test
    public void testLocalStatusCheck(){
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
        statusCheck.setStatus("21968543",false);
        statusCheck.setStatus("66511289",false);
        statusCheck.setStatus("21681231",false);
        statusCheck.setStatus("32195123",false);
        statusCheck.setStatus("21684213",false);
        statusCheck.setStatus("21968420",false);
        statusCheck.setStatus("12354621",false);
        statusCheck.setStatus("21632456",false);
        statusCheck.setStatus("10324652",false);
        statusCheck.refresh();
        int size1=statusCheck.getActiveSetByKey("1").size();
        int size2=statusCheck.getActiveSetByKey("2").size();
        int size3=statusCheck.getActiveSetByKey("3").size();
        int size4=statusCheck.getActiveSetByKey("4").size();
        int size5=statusCheck.getActiveSetByKey("5").size();
        return;
    }
}
