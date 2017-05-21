package billtenor.graduation.datacustomization.tableType.localFile;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lyj on 17-5-11.
 */
public class LocalXML implements java.io.Serializable{
    public Document getXMLFile(String name) {
        try{
            DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String folder = "/xmlExample/";
            String dir = folder + name + ".xml";
            InputStream input = this.getClass().getResourceAsStream(dir);
            Document doc = domBuilder.parse(input);
            return doc;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
