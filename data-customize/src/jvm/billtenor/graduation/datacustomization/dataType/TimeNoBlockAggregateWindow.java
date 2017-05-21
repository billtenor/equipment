package billtenor.graduation.datacustomization.dataType;

import billtenor.graduation.datacustomization.fieldTransform.TupleDataTransfer;

import java.util.Map;

/**
 * Created by lyj on 17-4-15.
 */
public class TimeNoBlockAggregateWindow extends BaseAggregateWindow {
    public TupleDataTransfer data;
    public Map<String,Object> para;
    public TimeNoBlockAggregateWindow(){
        super(new String[]{"startTime"});
    }
}
