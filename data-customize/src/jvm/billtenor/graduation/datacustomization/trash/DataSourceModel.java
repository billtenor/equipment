package billtenor.graduation.datacustomization.dataType;

import billtenor.graduation.datacustomization.statusCheck.MeasureDataCreator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by lyj on 17-2-10.
 */
public class DataSourceModel implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    public String dataSourceModelID;
    public int dataGapTime;
    public int dataLifeTime;
    public int dataDimSize;
    public int spaceDimSize;
    public DataDimTable dataDimTable;
    String getNodeValue(Node node){
        Node buff=node.getFirstChild();
        if(buff!=null){
            return buff.getNodeValue();
        }
        else{
            return "";
        }
    }
    String getAttributeValue(Node node,String attribute){
        Node buff=node.getAttributes().getNamedItem(attribute);
        if(buff==null){
            return null;
        }
        else{
            return buff.getNodeValue();
        }
    }
    public DataSourceModel(String dataSourceModelID){
        this.dataSourceModelID=dataSourceModelID;
        DomXML xmlTest = new DomXML(dataSourceModelID);
        if(xmlTest.xmlNodeRoot!=null){
            for (Node node = xmlTest.xmlNodeRoot.getFirstChild(); node != null; node = node.getNextSibling()){
                if(node.getNodeName().equals("dataSourceModelID")){
                    this.dataSourceModelID=getNodeValue(node);
                }
                else if(node.getNodeName().equals("dataGapTime")){
                    this.dataGapTime=Integer.parseInt(getNodeValue(node));
                }
                else if(node.getNodeName().equals("dataLifeTime")){
                    this.dataLifeTime=Integer.parseInt(getNodeValue(node));
                }
                else if(node.getNodeName().equals("dataDimTable")){
                    this.dataDimSize= Integer.parseInt(getAttributeValue(node,"dataDimSize"));
                    this.dataDimTable=new DataDimTable(this.dataDimSize);
                    NodeList dataDims = node.getChildNodes();
                    int dataDimIndex=0;
                    for(int i=0,size=dataDims.getLength();i<size;i++) {
                        Node dataDimNode = dataDims.item(i);
                        if(dataDimNode.getNodeType()==node.ELEMENT_NODE) {
                            this.dataDimTable.measureDataCreators[dataDimIndex] = new MeasureDataCreator();
                            MeasureDataCreator measureDataCreatorObj = this.dataDimTable.measureDataCreators[dataDimIndex];
                            dataDimIndex++;
                            for (Node node1 = dataDimNode.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
                                if (node1.getNodeName().equals("dataDimTableMajorKey")) {
                                    measureDataCreatorObj.dataDimTableMajorKey = getNodeValue(node1);
                                } else if (node1.getNodeName().equals("dataDimName")) {
                                    //Node buff=node1.getFirstChild();
                                    measureDataCreatorObj.dataDimName = getNodeValue(node1);
                                } else if (node1.getNodeName().equals("dataDimDescription")) {
                                    measureDataCreatorObj.dataDimDescription = node1.getFirstChild().getNodeValue();
                                } else if (node1.getNodeName().equals("dataType")) {
                                    measureDataCreatorObj.dataType = getNodeValue(node1);
                                } else if (node1.getNodeName().equals("dataConstraints")) {
                                    measureDataCreatorObj.dataConstraints = getNodeValue(node1);
                                } else if (node1.getNodeName().equals("dataUnit")) {
                                    measureDataCreatorObj.dataUnit = getNodeValue(node1);
                                }
                            }
                            if (measureDataCreatorObj.dataType.equals("Integer")) {
                                measureDataCreatorObj.dataType = Integer.class.getName();
                            } else if (measureDataCreatorObj.dataType.equals("Double")) {
                                measureDataCreatorObj.dataType = Double.class.getName();
                            } else if (measureDataCreatorObj.dataType.equals("Boolean")) {
                                measureDataCreatorObj.dataType = Boolean.class.getName();
                            } else if (measureDataCreatorObj.dataType.equals("String")) {
                                measureDataCreatorObj.dataType = String.class.getName();
                            }
                        }
                    }
                }
                else if(node.getNodeName().equals("spaceDimTable")){
                    this.spaceDimSize=Integer.parseInt(getAttributeValue(node,"spaceDimSize"));
                }
            }
        }
    }
}
