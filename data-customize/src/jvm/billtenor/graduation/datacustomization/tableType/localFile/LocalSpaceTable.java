package billtenor.graduation.datacustomization.tableType.localFile;

import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;

import java.util.Map;

/**
 * Created by lyj on 17-3-22.
 */
public class LocalSpaceTable extends BaseSpaceTable {
    final private String tableName;
    final private String selectField;

    protected void getData(){
        LocalFileTable localFileTable=new LocalFileTable(
                "/localDatabase",tableName+".txt",selectField);
        this.data=localFileTable.data;
    }
    public LocalSpaceTable(
            Map<Integer,String> grainLevelGrainID,String tableName,String selectField
    ){
        super(grainLevelGrainID);
        this.tableName=tableName;
        this.selectField=selectField;
        getData();
    }
}
