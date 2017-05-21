package billtenor.graduation.datacustomization.fieldTransform;

/**
 * Created by lyj on 17-3-28.
 */
public class ToSaveBolt implements java.io.Serializable,IBaseKeyTransform{
    @Override
    public String getKey(TupleDataTransfer data) {
        return data.spaceID;
    }
}
