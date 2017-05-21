package billtenor.graduation.datacustomization.fieldTransform;

import static org.junit.Assert.assertEquals;

import billtenor.graduation.datacustomization.tableType.localFile.LocalJSON;
import billtenor.graduation.datacustomization.tableType.localFile.LocalMeasureTable;

/**
 * Created by lyj on 17-3-26.
 */
public class TestKafkaSpoutActor {
    //@Test
    public void KafkaSpoutActorTest(){
        LocalMeasureTable localMeasureTable=new LocalMeasureTable(
                "testWarehouseModel_MeasureConformedDimTable",
                "MeasureID,MeasureType,MeasureConstraints,MeasureRely,MeasureEquation"
        );
        KafkaSpoutSchemeActor kafkaSpoutSchemeActor =new KafkaSpoutSchemeActor(localMeasureTable.getMeasureIDMeasureType());
        LocalJSON localJSON=new LocalJSON("kafka_message.json");
        TupleDataTransfer outputData = kafkaSpoutSchemeActor.dataTransform(localJSON.data);
        String result=outputData.toString();
        assertEquals( "createJsonData", "createJsonData");
    }
}
