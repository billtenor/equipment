package billtenor.graduation.datacustomization.tableType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanjun on 17-4-6.
 */
public abstract class BaseNodeStatusTable extends BaseTable{
    public Map<String, Boolean> status;
    public abstract void getData();
    protected BaseNodeStatusTable(){
        status = new HashMap<>();
    }
}
