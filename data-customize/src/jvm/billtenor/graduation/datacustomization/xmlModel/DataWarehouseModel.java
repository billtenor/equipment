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
public class DataWarehouseModel extends XMLModel{
    public String dataWarehouseModelID;
    public DimGrain[] dateTimeConformedDim;
    public DimGrain[] spaceConformedDim;
    public DimGrain[] measureConformedDim;
    public String[] conformedFacts;
    public BaseFactsTable factsTable;
    public BaseMeasureTable measureTable;
    public BaseSpaceTable spaceTable;
    public String getSpaceConformedTableName(){
        return dataWarehouseModelID+"_SpaceConformedDimTable";
    }
    public String getFactsConformedTableName(){
        return dataWarehouseModelID+"_FactsConformedTable";
    }
    public String getMeasureConformedTableName(){
        return dataWarehouseModelID+"_MeasureConformedDimTable";
    }
    public void setFromXML(Document document){
        Element xmlNodeRoot = document.getDocumentElement();
        if(xmlNodeRoot!=null){
            for (Node node1 = xmlNodeRoot.getFirstChild(); node1 != null; node1 = node1.getNextSibling()){
                if(node1.getNodeName().equals("DataWarehouseModelID")){
                    this.dataWarehouseModelID = getNodeValue(node1);
                }
                else if(node1.getNodeName().equals("ConformedDims")){
                    for(Node node2 = node1.getFirstChild();node2 != null; node2 = node2.getNextSibling()){
                        if(node2.getNodeName().equals("DateTimeConformedDim")){
                            this.dateTimeConformedDim = getDimGrain(node2);
                        }
                        else if(node2.getNodeName().equals("SpaceConformedDim")){
                            this.spaceConformedDim = getDimGrain(node2);
                        }
                        else if(node2.getNodeName().equals("MeasureConformedDim")){
                            this.measureConformedDim = getDimGrain(node2);
                        }
                    }
                }
                else if(node1.getNodeName().equals("ConformedFacts")){
                    this.conformedFacts = getFactHeads(node1);
                }
            }
        }
    }
}
