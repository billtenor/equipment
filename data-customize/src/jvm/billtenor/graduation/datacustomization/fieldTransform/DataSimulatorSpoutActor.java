package billtenor.graduation.datacustomization.fieldTransform;

import billtenor.graduation.datacustomization.statusCheck.LocalMeasureCheck;
import billtenor.graduation.datacustomization.statusCheck.StatusCheck;
import billtenor.graduation.datacustomization.tableType.localFile.LocalNodeStatusTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalSpaceTable;
import billtenor.graduation.datacustomization.tableType.localFile.LocalXML;
import billtenor.graduation.datacustomization.xmlModel.DataSourceModel;
import billtenor.graduation.datacustomization.xmlModel.DataWarehouseModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by lyj on 17-5-12.
 */
public class DataSimulatorSpoutActor implements java.io.Serializable {
    public final DataSourceModel dataSourceModel;
    public final List<String> spaceID;
    public final List<String> areaID;

    public DataSimulatorSpoutActor(String dataSourceModelID){
        this.dataSourceModel=new DataSourceModel();
        LocalXML localXML=new LocalXML();
        this.dataSourceModel.setFromXML(localXML.getXMLFile(dataSourceModelID));
        this.dataSourceModel.measureCheck= new LocalMeasureCheck(this.dataSourceModel.dataWarehouseModelID);
        this.dataSourceModel.statusTable = new LocalNodeStatusTable(
                this.dataSourceModel.getNodeStatusTableName()
        );

        DataWarehouseModel dataWarehouseModel=new DataWarehouseModel();
        dataWarehouseModel.dataWarehouseModelID=this.dataSourceModel.dataWarehouseModelID;
        LocalSpaceTable spaceTable=new LocalSpaceTable(
                new HashMap<Integer, String>(){{
                    put(1,"spaceID");
                    put(2,"AreaID");
                }},
                dataWarehouseModel.getSpaceConformedTableName(),
                "spaceID,AreaID"
        );
        StatusCheck statusCheck = new StatusCheck(
                1,1,spaceTable,this.dataSourceModel.statusTable
        );
        statusCheck.refresh();

        this.spaceID = new ArrayList<>();
        this.areaID = new ArrayList<>();
        for(String activeNode:statusCheck.getActiveSetOfThis()){
            this.spaceID.add(activeNode);
            this.areaID.add(spaceTable.getHighGrainKey(2,1,activeNode));
        }
    }

    public String createJsonData(String spaceID, Random _rand){
        JSONObject result=new JSONObject();
        //result.put("dataSourceModelID",_dataModel.dataSourceModelID);
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        result.put("timeStamp",dateFormat.format(new Date(System.currentTimeMillis())));
        result.put("spaceID",spaceID);

        JSONArray array=new JSONArray();
        for(int i=0;i<dataSourceModel.measureCheck.getSize();i++){
            JSONObject obj=new JSONObject();
            obj.put("dataDimTableKey",dataSourceModel.measureCheck.measureDataCreators[i].dataDimTableMajorKey);
            obj.put("value",dataSourceModel.measureCheck.measureDataCreators[i].sampleData(_rand));
            array.add(i,obj);
        }
        result.put("data",array);
        return result.toJSONString();
    }
}
