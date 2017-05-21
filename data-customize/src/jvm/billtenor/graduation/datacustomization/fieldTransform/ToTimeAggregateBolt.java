package billtenor.graduation.datacustomization.fieldTransform;

/**
 * Created by lyj on 17-4-13.
 */
public class ToTimeAggregateBolt implements java.io.Serializable,IBaseKeyTransform{
    @Override
    public String getKey(TupleDataTransfer data) {
        return data.spaceID;
    }
}
