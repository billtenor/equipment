package billtenor.graduation.datacustomization.fieldTransform;

import java.util.Map;

/**
 * Created by lyj on 17-3-27.
 */
public class ToSpaceAggregateBolt implements java.io.Serializable,IBaseKeyTransform{
    Map<String,String> lowGrainIDHighGrainID;

    public ToSpaceAggregateBolt(Map<String,String> lowGrainIDHighGrainID){
        this.lowGrainIDHighGrainID=lowGrainIDHighGrainID;
    }

    @Override
    public String getKey(TupleDataTransfer data) {
        return lowGrainIDHighGrainID.get(data.spaceID);
    }
}
