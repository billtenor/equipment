package billtenor.graduation.datacustomization.fieldTransform;

import billtenor.graduation.datacustomization.dataType.MySqlConfig;
import billtenor.graduation.datacustomization.tableType.localFile.LocalJSON;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
/**
 * Created by lyj on 17-3-31.
 */
public class TestMySqlDataSaveActor {
    //@Test
    public void testMySqlDataSaveActor(){
        BaseDataSaveActor dataSaveActor = new MySqlDataSaveActor(
                new MySqlConfig("192.168.100.254","3306","warehouseUser","zhu88jie","warehouseTest"),
                new HashMap<String,String[]>(){{
                    put("table1",new String[]{"2","1"});
                    put("table2",new String[]{"2","3"});
                }}
        );
        LocalJSON localJSON1 = new LocalJSON("originData1.json");
        LocalJSON localJSON2 = new LocalJSON("originData2.json");
        LocalJSON localJSON3 = new LocalJSON("originData3.json");
        dataSaveActor.init();
        boolean result = dataSaveActor.dataTransfer(
                new String[]{localJSON1.data,localJSON2.data,localJSON3.data}
        );
        return;
    }
}
