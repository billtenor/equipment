package billtenor.graduation.datacustomization.xmlModel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;

/**
 * Created by lyj on 17-5-11.
 */
public abstract class XMLModel implements java.io.Serializable{
    abstract public void setFromXML(Document document);
    protected String getNodeValue(Node node){
        Node buff=node.getFirstChild();
        if(buff!=null){
            return buff.getNodeValue();
        }
        else{
            return "";
        }
    }
    protected String getAttributeValue(Node node,String attribute){
        Node buff=node.getAttributes().getNamedItem(attribute);
        if(buff==null){
            return null;
        }
        else{
            return buff.getNodeValue();
        }
    }
    protected DimGrain[] getDimGrain(Node node){
        ArrayList<DimGrain> dimGrains = new ArrayList<>();
        for(Node node1 = node.getFirstChild();node1!=null;node1=node1.getNextSibling()){
            if(node1.getNodeType()==Node.ELEMENT_NODE) {
                String dimGrainKey = node1.getNodeName();
                int dimGrainLevel = Integer.parseInt(getAttributeValue(node1,"grainLevel"));
                ArrayList<String> dimsBuff = new ArrayList<>();
                for(Node node2 = node1.getFirstChild();node2!=null;node2=node2.getNextSibling()){
                    if(node2.getNodeType()== Node.ELEMENT_NODE){
                        dimsBuff.add(node2.getNodeName());
                    }
                }
                String[] dims = dimsBuff.toArray(new String[dimsBuff.size()]);
                dimGrains.add(new DimGrain(dimGrainKey,dimGrainLevel,dims));
            }
        }
        return dimGrains.toArray(new DimGrain[dimGrains.size()]);
    }
    protected String[] getFactHeads(Node node){
        ArrayList<String> factsBuff = new ArrayList<>();
        for(Node node1 = node.getFirstChild();node1 != null; node1 = node1.getNextSibling()){
            if(node1.getNodeType()==Node.ELEMENT_NODE) {
                factsBuff.add(node1.getNodeName());
            }
        }
        return factsBuff.toArray(new String[factsBuff.size()]);
    }
}
