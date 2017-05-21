package billtenor.graduation.datacustomization.fieldTransform;

import java.util.List;

/**
 * Created by lyj on 17-5-12.
 */
public class NoneDataSaveActor extends BaseDataSaveActor {
    public NoneDataSaveActor(){
        super(null);
    }

    @Override
    public boolean dataTransfer(Object[] objects) {
        return true;
    }

    @Override
    public boolean save(List<DataSave> data, String tableName, String[] factsTitle) {
        return true;
    }

    @Override
    public void init() {

    }

    @Override
    public void close() {

    }
}
