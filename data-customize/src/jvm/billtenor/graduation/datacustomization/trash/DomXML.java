package billtenor.graduation.datacustomization.dataType;

/**
 * Created by yanjun on 17-2-13.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class DomXML {
    public Element xmlNodeRoot;
    public DomXML(String DataSourceModelID) {
        try{
            DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String folder = "/xmlExample/";
            String name = DataSourceModelID + ".xml";
            String dir = folder+name;
            InputStream input = this.getClass().getResourceAsStream(dir);
            Document doc = domBuilder.parse(input);
            this.xmlNodeRoot = doc.getDocumentElement();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
