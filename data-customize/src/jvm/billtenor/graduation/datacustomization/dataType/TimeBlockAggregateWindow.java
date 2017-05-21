package billtenor.graduation.datacustomization.dataType;

import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;
import org.apache.storm.tuple.Tuple;

/**
 * Created by lyj on 17-4-13.
 */
public class TimeBlockAggregateWindow extends BaseAggregateWindow {
    public TimeBlockAggregateWindow(){
        super(new String[]{"startTime"});
    }
    public void clear(){
        super.clear();
    }
}
