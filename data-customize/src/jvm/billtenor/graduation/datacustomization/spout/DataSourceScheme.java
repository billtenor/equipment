package billtenor.graduation.datacustomization.spout;

import billtenor.graduation.datacustomization.dataType.GlobalConfig;
import billtenor.graduation.datacustomization.dataType.IMyComponent;
import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;
import billtenor.graduation.datacustomization.fieldTransform.IBaseKeyTransform;
import billtenor.graduation.datacustomization.fieldTransform.KafkaSpoutSchemeActor;
import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Created by lyj on 17-3-13.
 */
public class DataSourceScheme implements Scheme,IMyComponent {
    private static final Charset UTF8_CHARSET;
    private IBaseKeyTransform keyTransform;
    private KafkaSpoutSchemeActor kafkaSpoutSchemeActor;

    public DataSourceScheme(Map<String,String> measureIDMeasureType)
    {
        this.kafkaSpoutSchemeActor =new KafkaSpoutSchemeActor(measureIDMeasureType);
    }

    public void setKeyTransform(IBaseKeyTransform keyTransform){
        this.keyTransform=keyTransform;
    }
    public IBaseKeyTransform getKeyTransform(){
        return null;
    }

    public List<Object> deserialize(ByteBuffer bytes) {
        String message=deserializeString(bytes);
        TupleDataTransfer outputData = kafkaSpoutSchemeActor.dataTransform(message);
        return new Values(keyTransform.getKey(outputData),outputData.toString());
    }

    public static String deserializeString(ByteBuffer string) {
        if(string.hasArray()) {
            int base = string.arrayOffset();
            return new String(string.array(), base + string.position(), string.remaining());
        } else {
            return new String(Utils.toByteArray(string), UTF8_CHARSET);
        }
    }

    public Fields getOutputFields() {
        return new Fields(GlobalConfig.outputFields);
    }

    static {
        UTF8_CHARSET = StandardCharsets.UTF_8;
    }
}
