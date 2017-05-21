package billtenor.graduation.datacustomization.dataType;

/**
 * Created by lyj on 17-3-24.
 */
public class SpaceAggregateWindow extends BaseAggregateWindow {
    public SpaceAggregateWindow(){
        super(new String[]{"startTime","endTime","size"});
    }
    public void clear(){
        super.clear(new String[]{"startTime","endTime"});
    }
}
