package billtenor.graduation.datacustomization;

import billtenor.graduation.datacustomization.DataConsumer;
import org.junit.Test;

/**
 * Created by lyj on 17-5-12.
 */
public class DataConsumerTest {
    //@Test
    public void testDataConsumer() throws Exception{
        DataConsumer.main(new String[]{"sh/consumer.json"});
    }
}
