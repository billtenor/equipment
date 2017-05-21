package billtenor.graduation.datacustomization.xmlModel;

import billtenor.graduation.datacustomization.tableType.BaseFactsTable;
import billtenor.graduation.datacustomization.tableType.BaseMeasureTable;
import billtenor.graduation.datacustomization.tableType.BaseSpaceTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Created by lyj on 17-5-11.
 */
public class DataModel extends XMLModel{
    public String dataWarehouseModelID;
    public String dataModelID;
    public String dataModelEngine;
    public String dataModelPara;
    public DimGrain[] dateTimeDim;
    public DimGrain[] spaceDim;
    public DimGrain[] measureDim;
    public String[] factsHeadsTable;
    public BaseFactsTable factsTable;
    public BaseMeasureTable measureTable;
    public BaseSpaceTable spaceTable;
    public String getDataModelSpaceTableName(){
        return dataModelID+"_SpaceDimTable";
    }
    public String getDataModelMeasureTableName(){
        return dataModelID+"_MeasureDimTable";
    }
    public String getDataModelFactsHeadsTableName(){
        return dataModelID+"_FactsHeadsTable";
    }
    public String getBuffKafkaTopicName(){
        return dataWarehouseModelID+"_"+dataModelID;
    }
    public void setFromXML(Document document){
        Element xmlNodeRoot = document.getDocumentElement();
        if(xmlNodeRoot!=null){
            for(Node node1 = xmlNodeRoot.getFirstChild(); node1 != null; node1 = node1.getNextSibling()){
                if(node1.getNodeName().equals("DataWarehouseModelID")){
                    this.dataWarehouseModelID=getNodeValue(node1);
                }
                else if(node1.getNodeName().equals("DataModelID")){
                    this.dataModelID=getNodeValue(node1);
                }
                else if(node1.getNodeName().equals("DataModelEngine")){
                    this.dataModelEngine=getNodeValue(node1);
                }
                else if(node1.getNodeName().equals("DataModelPara")){
                    this.dataModelPara=getNodeValue(node1);
                }
                else if(node1.getNodeName().equals("DateTimeDim")){
                    this.dateTimeDim=getDimGrain(node1);
                }
                else if(node1.getNodeName().equals("SpaceDim")){
                    this.spaceDim=getDimGrain(node1);
                }
                else if(node1.getNodeName().equals("MeasureDim")){
                    this.measureDim=getDimGrain(node1);
                }
                else if(node1.getNodeName().equals("FactsHeadsTable")){
                    this.factsHeadsTable=getFactHeads(node1);
                }
            }
        }
    }
}
