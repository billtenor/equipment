package billtenor.graduation.datacustomization.bolt;
import static org.junit.Assert.assertEquals;

import billtenor.graduation.datacustomization.trash.LocalStatusCheck;
import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalSpaceTable;

import java.util.HashMap;

/**
 * Created by lyj on 17-3-24.
 */
public class SpaceAggregateBoltTest {
    //@Test
    public void testDataAggregateBolt(){
        /*
        Map<String,Object[]> buffData=new HashMap<>();
        List<Values> buffValue =  new ArrayList<Values>(){};
        buffValue.add(new Values(1,2));
        buffValue.add(new Values(3,4));
        buffValue.add(new Values(5,6));
        buffValue.add(new Values(7,8));
        buffData.put("aaa",new Object[]{
            buffValue,new Long(10000),new Long(20000),5
        });
        Object[] tupleData=buffData.get("aaa");
        ArrayList<Values> hasTuples=(ArrayList<Values>)tupleData[0];
        Long tuplesStartTime=(Long)tupleData[1];
        Long tuplesEndTime=(Long)tupleData[2];
        int tuplesSize=(int)tupleData[3];
        tuplesStartTime=new Long(10100);
        tuplesEndTime=new Long(20100);
        tupleData[1]=tuplesStartTime;
        hasTuples.add(new Values(9,10));
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
        statusCheck.setStatus("21968420",false);
        statusCheck.setStatus("12354621",false);
        statusCheck.setStatus("21632456",false);
        statusCheck.setStatus("10324652",false);
        //int size=baseStatusCheck.getActiveSetByHighLevel(targetLevel,tupleKey).size();

        return;
    }
}
