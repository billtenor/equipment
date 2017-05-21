package billtenor.graduation.datacustomization.fieldTransform;

import org.junit.Test;

import java.util.Random;

/**
 * Created by lyj on 17-5-12.
 */
public class TestDataSimulatorSpoutActor {
    //@Test
    public void testDataSimulatorSpoutActor(){
        DataSimulatorSpoutActor actor = new DataSimulatorSpoutActor("testDataSourceModel");
        Random random=new Random();
        String result = actor.createJsonData("111",random);
    }
}
