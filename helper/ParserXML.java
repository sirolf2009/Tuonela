package helper;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParserXML {

	public static Document parse(String path) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document doc = documentBuilder.parse(path);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static NodeList getNodeListByName(Document doc, String element) {
		return doc.getElementsByTagName(element);
	}

	public static Element retrieveValueFromNodeWithAttribute(NodeList list, String attribute, String value) {
		for(int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.hasAttribute(attribute) && element.getAttribute(attribute).equals(value)) {
					return element;
				}
			}
		}
		return null;
	}
	
	public static String retrieveValueFromNodeName(NodeList list, String nodeName) {
		for(int i = 0; i < list.getLength(); i++) {
			if(list.item(i).getNodeName()==nodeName) {
				return list.item(i).getTextContent();
			}
		}
		return null;
	}
	
	public static Node retrieveNode(NodeList list, String nodeName) {
		for(int i = 0; i < list.getLength(); i++) {
			if(list.item(i).getNodeName()==nodeName) {
				return list.item(i);
			}
		}
		return null;
	}

	public static Document createDocument(URI path) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			saveDocument(doc, path);
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document saveDocument(Document doc, URI path) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			System.out.println("save file: "+new File(path));
			Result output = new StreamResult(new File(path));
			Source input = new DOMSource(doc);

			transformer.transform(input, output);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

}
