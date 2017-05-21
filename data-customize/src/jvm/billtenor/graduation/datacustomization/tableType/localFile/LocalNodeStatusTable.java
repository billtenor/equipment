package billtenor.graduation.datacustomization.tableType.localFile;


import billtenor.graduation.datacustomization.tableType.BaseNodeStatusTable;

/**
 * Created by yanjun on 17-4-7.
 */
public class LocalNodeStatusTable extends BaseNodeStatusTable{
    final private String tableName;
    public LocalNodeStatusTable(String tableName){
        this.tableName=tableName;
        getData();
    }
    @Override
    public void getData() {
        LocalFileTable localFileTable=new LocalFileTable(
                "/localDatabase",tableName+".txt","*");
        for(String nodeID:localFileTable.data.rowKeySet()){
            if(localFileTable.data.get(nodeID,"status").equals("true")){
                this.status.put(nodeID,true);
            }
            else{
                this.status.put(nodeID,false);
            }
        }
    }
}
