package helpers;

import models.helpers.ExportParam;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Команды для экспорта и выполнение команд
 */
public class CommandHelper {
    private final static String defaultXsd = "";

    public static ArrayList<String> execute(String command) {
        ArrayList<String> result = new ArrayList<String>();
        result.add("user 1");
        result.add("user 2");
        result.add("user 3");
        return result;
    }

    public static ArrayList<String> executeLinux(String command, String regexp) throws IOException, InterruptedException {
        ArrayList<String> result = new ArrayList<String>();
        Process p = null;
        p = Runtime.getRuntime().exec(command);
        p.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder sb = new StringBuilder();
        String line = "";
        while ((line = reader.readLine())!= null) {
            sb.append(line + "\n");
        }
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(sb.toString());
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    public static String generateXsd(ArrayList<ExportParam> parameters) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Attr attr;
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("xs:schema");

            attr = doc.createAttribute("attributeFormDefault");
            attr.setValue("unqualified");
            rootElement.setAttributeNode(attr);

            attr = doc.createAttribute("elementFormDefault");
            attr.setValue("qualified");
            rootElement.setAttributeNode(attr);

            attr = doc.createAttribute("xmlns:xs");
            attr.setValue("http://www.w3.org/2001/XMLSchema");
            rootElement.setAttributeNode(attr);

            doc.appendChild(rootElement);

            // element
            Element elem = doc.createElement("xs:element");
            attr = doc.createAttribute("name");
            attr.setValue("params");
            elem.setAttributeNode(attr);
            rootElement.appendChild(elem);

            Element type = doc.createElement("xs:complexType");
            elem.appendChild(type);

            Element choice = doc.createElement("xs:choice");
            type.appendChild(choice);

            for(ExportParam param : parameters) {
                Element par = doc.createElement("xs:element");
                attr = doc.createAttribute("name");
                attr.setValue(param.getName());
                par.setAttributeNode(attr);
                choice.appendChild(par);

                Element parType = doc.createElement("xs:simpleType");
                par.appendChild(parType);

                Element restriction = doc.createElement("xs:restriction");
                attr = doc.createAttribute("base");
                attr.setValue("xs:string");
                restriction.setAttributeNode(attr);
                parType.appendChild(restriction);

                ArrayList<String> values;
                if (param.getType() == 2) {
                    values = param.getVariants();
                } else {
                    values = new ArrayList<String>();
                    values.add(param.getValue());
                }
                for (String paramValue : values) {
                    Element choiceElem = doc.createElement("xs:enumeration");
                    attr = doc.createAttribute("value");
                    attr.setValue(paramValue);
                    choiceElem.setAttributeNode(attr);
                    restriction.appendChild(choiceElem);
                }
            }

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("C:\\tmp\\wewe.xml"));
//            StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String xsd = "";
        return xsd;
    }
}
