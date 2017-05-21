package billtenor.graduation.datacustomization.dataType;

import static org.junit.Assert.assertEquals;

import billtenor.graduation.datacustomization.trash.DataSourceModel;

/**
 * Created by yanjun on 17-2-13.
 */
public class DataSourceModelTest {
    //@Test
    public void testDataSimulatorSpout(){
        DataSourceModel dataSourceModel=new DataSourceModel("libuznaksier");
        assertEquals( "createJsonData", "createJsonData");
    }
}
