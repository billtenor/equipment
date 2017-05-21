package billtenor.graduation.datacustomization.statusCheck;

import billtenor.graduation.datacustomization.tableType.localFile.LocalMeasureTable;
import billtenor.graduation.datacustomization.xmlModel.DataWarehouseModel;

/**
 * Created by lyj on 17-5-12.
 */
public class LocalMeasureCheck extends BaseMeasureCheck{
    public LocalMeasureCheck(String dataSourceID){
        super(dataSourceID);
    }
    @Override
    protected void initMeasureTable(String dataWarehouseModelID) {
        DataWarehouseModel dataWarehouseModel = new DataWarehouseModel();
        dataWarehouseModel.dataWarehouseModelID = dataWarehouseModelID;
        this.measureTable = new LocalMeasureTable(
                dataWarehouseModel.getMeasureConformedTableName(),
                this.selectField
        );
    }
}
