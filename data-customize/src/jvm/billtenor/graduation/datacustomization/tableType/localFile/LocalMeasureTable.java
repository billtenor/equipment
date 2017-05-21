package billtenor.graduation.datacustomization.tableType.localFile;

import billtenor.graduation.datacustomization.tableType.BaseMeasureTable;

/**
 * Created by lyj on 17-3-25.
 */
public class LocalMeasureTable extends BaseMeasureTable {
    final private String tableName;
    final private String selectField;
    @Override
    protected void getData() {
        LocalFileTable localFileTable=new LocalFileTable(
                "/localDatabase",tableName+".txt",selectField);
        this.data=localFileTable.data;
    }
    public LocalMeasureTable(String tableName,String selectField){
        this.tableName=tableName;
        this.selectField=selectField;
        getData();
    }
}
