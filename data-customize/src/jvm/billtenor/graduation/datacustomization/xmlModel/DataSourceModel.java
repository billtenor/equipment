package billtenor.graduation.datacustomization.xmlModel;

import billtenor.graduation.datacustomization.statusCheck.BaseMeasureCheck;
import billtenor.graduation.datacustomization.tableType.BaseNodeStatusTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by lyj on 17-5-11.
 */
public class DataSourceModel extends XMLModel{
    public String dataWarehouseModelID;
    public Long dataGapTime;
    public Long dataLifeTime;
    public BaseMeasureCheck measureCheck;
    public BaseNodeStatusTable statusTable;
    public String getNodeStatusTableName(){
        return dataWarehouseModelID+"_NodeStatusTable";
    }
    public void setFromXML(Document document){
        Element xmlNodeRoot = document.getDocumentElement();
        if(xmlNodeRoot!=null){
            for(Node node1 = xmlNodeRoot.getFirstChild(); node1 != null; node1 = node1.getNextSibling()){
                if(node1.getNodeName().equals("DataWarehouseModelID")){
                    this.dataWarehouseModelID=getNodeValue(node1);
                }
                else if(node1.getNodeName().equals("DataGapTime")){
                    this.dataGapTime = Long.parseLong(getNodeValue(node1));
                }
                else if(node1.getNodeName().equals("DataLifeTime")){
                    this.dataLifeTime = Long.parseLong(getNodeValue(node1));
                }
            }
        }
    }
}
