package billtenor.graduation.datacustomization.dataType;
import static org.junit.Assert.assertEquals;

import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalSpaceTable;
import org.junit.Test;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by lyj on 17-3-22.
 */
public class LocalSpaceTableTest {
    //@Test
    public void testLocalSpaceTable(){
        BaseSpaceTable localSpaceTable = new LocalSpaceTable(
                new HashMap<Integer, String>(){{
                    put(1,"spaceID");
                    put(2,"AreaID");
                    put(3,"ZoneID");
                }},
                "testWarehouseModel_SpaceConformedDimTable",
                "spaceID,AreaID,ZoneID"
        );
        String result1=localSpaceTable.getHighGrainKey(3,1,"15346248");
        Set<String> result2=localSpaceTable.getLowGrainSet(3,2,"153");
        assertEquals( "createJsonData", "createJsonData");
    }
}
