package billtenor.graduation.datacustomization;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by lyj on 17-5-11.
 */
public class CreateSpaceTableTest {
    @Test
    public void testCreateSpaceTable() throws IOException{
        CreateSpaceTable createSpaceTable=new CreateSpaceTable(10000,30,"performanceTest");
        createSpaceTable.create();
        createSpaceTable.active(2000);
    }
}
