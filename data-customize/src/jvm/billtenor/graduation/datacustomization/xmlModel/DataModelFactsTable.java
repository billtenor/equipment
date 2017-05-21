package billtenor.graduation.datacustomization.xmlModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Created by lyj on 17-5-11.
 */
public class DataModelFactsTable extends XMLModel {
    public String dataModelID;
    public String dataModelFactsTableID;
    public String[] factIDs;
    public String getFactsTableName(){
        return this.dataModelID+"_"+this.dataModelFactsTableID;
    }
    public void setFromXML(Document document){
        Element xmlNodeRoot = document.getDocumentElement();
        if(xmlNodeRoot!=null) {
            for (Node node1 = xmlNodeRoot.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
                if(node1.getNodeName().equals("DataModelID")){
                    this.dataModelID=getNodeValue(node1);
                }
                else if(node1.getNodeName().equals("DataModelFactsTableID")){
                    this.dataModelFactsTableID=getNodeValue(node1);
                }
                else if(node1.getNodeName().equals("FactIDs")){
                    int factSize = Integer.parseInt(getAttributeValue(node1,"factSize"));
                    this.factIDs=new String[factSize];
                    NodeList nodeList = node1.getChildNodes();
                    int factIDIndex=0;
                    for(int j=0,size=nodeList.getLength();j<size;j++) {
                        Node node2 = nodeList.item(j);
                        if (node2.getNodeType() == Node.ELEMENT_NODE) {
                            this.factIDs[factIDIndex]=getNodeValue(node2);
                            factIDIndex++;
                        }
                    }
                }
            }
        }
    }
}
