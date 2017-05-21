package billtenor.graduation.datacustomization;

import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;

/**
 * Created by lyj on 17-2-11.
 */
public class DataSimulatorTest {
    //@Test
    public void dataSimulatorTest(){
        TupleDataTransfer tupleDataTransfer =new TupleDataTransfer();
        String s1="{\"timeStamp\":{\"2\":1490784724435},\"spaceID\":\"6b3e57e9-3e6a-422a-90aa-342fedb6c617\",\"data\":[{\"measureID\":\"12312313\",\"facts\":{\"1\":13.064567395395027}},{\"measureID\":\"19123451\",\"facts\":{\"1\":0.4665916926926795}},{\"measureID\":\"14943157\",\"facts\":{\"3\":\"inactive\"}},{\"measureID\":\"15642345\",\"facts\":{\"1\":28}},{\"measureID\":\"45862154\",\"facts\":{\"1\":60.00964105985957}}],\"defaultTimeStamp\":1490784724435}";
        tupleDataTransfer.refreshData(s1);
        String s2="{\"timeStamp\":{},\"spaceID\":\"6b3e57e9-3e6a-422a-90aa-342fedb6c617\",\"data\":[{\"measureID\":\"12312313\",\"facts\":{\"1\":13.064429882962644}},{\"measureID\":\"19123451\",\"facts\":{\"1\":0.3186446312917718}},{\"measureID\":\"14943157\",\"facts\":{\"3\":\"inactive\"}},{\"measureID\":\"15642345\",\"facts\":{\"1\":41}},{\"measureID\":\"45862154\",\"facts\":{\"1\":128.66998522393973}}],\"defaultTimeStamp\":1490784736860}";
        tupleDataTransfer.refreshData(s2);
        return;
    }

}
