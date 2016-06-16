package com.osthus.casis.index;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.io.DOMReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.osthus.ambeth.log.ILogger;
import de.osthus.ambeth.log.LogInstance;

public class XmlUtil {

	@LogInstance
	private static ILogger log;

	public String renameTagsDotToMinus(String value)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException,
			ParserConfigurationException, SAXException, IOException {
		Document doc = convertStringToXmlDocumnet(value);
		loopRenameTagsDotToMinus(doc, doc.getDocumentElement());

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

		return output;
	}

	public Document convertStringToXmlDocumnet(String xmlStr)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc = null;

		builder = factory.newDocumentBuilder();
		doc = builder.parse(new InputSource(new StringReader(xmlStr)));

		return doc;
	}

	private void loopRenameTagsDotToMinus(Document doc, Node node) {
		// TODO string ? to StringBuilder?
		String nodeName = node.getNodeName();
		if (nodeName.contains("."))
			changeTagName(doc, nodeName, nodeName.replace(".", "-"));
		// StringBuilder nodeName = new StringBuilder(node.getNodeName());
		// if (nodeName.indexOf(".")!=-1)
		// changeTagName(doc, nodeName, nodeName.replaceall(".", "-"));

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				loopRenameTagsDotToMinus(doc, currentNode);
			}
		}
	}

	private void changeTagName(Document doc, String fromTag, String toTag) {
		NodeList nodes = doc.getElementsByTagName(fromTag);
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i) instanceof Element) {
				Element elem = (Element) nodes.item(i);
				doc.renameNode(elem, elem.getNamespaceURI(), toTag);
			}
		}
	}

	// TODO change String to String Buffer
	public void getNodesValue(org.dom4j.Element node, org.dom4j.Element root, String parentString,
			Map<String, ArrayList<String>> map) throws DocumentException {
		List<org.dom4j.Element> listElement = node.elements();

		if (node == root)
			parentString = node.getName();
		else
			parentString = parentString + "_" + node.getName();
		// travel all the data nodes

		if (listElement.isEmpty()) {
			List<Attribute> listAttr = node.attributes();
			for (Attribute attr : listAttr) {
				ArrayList<String> arrayList = new ArrayList<String>();
				String newParentString = parentString;
				String name = attr.getName();
				String value = attr.getValue();
				newParentString = parentString + "_" + name;
				if (map.get(newParentString) != null)
					arrayList = map.get(newParentString);
				arrayList.add(value);
				map.put(newParentString, arrayList);
			}

			// TODO element's atrributes should be there also
			ArrayList<String> arrayList1 = new ArrayList<String>();
			if (map.get(parentString) != null)
				arrayList1 = map.get(parentString);
			arrayList1.add(node.getStringValue());
			map.put(parentString, arrayList1);
		}

		for (org.dom4j.Element e : listElement) {
			this.getNodesValue(e, root, parentString, map);//
		}
	}

	public HashMap<String, ArrayList<String>> getXmlKeyValuesPairs(String valueUnderline)
			throws DocumentException, ParserConfigurationException, SAXException, IOException {
		String parentString = "";
		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		Document doc = convertStringToXmlDocumnet(valueUnderline);

		org.dom4j.io.DOMReader reader = new DOMReader();
		org.dom4j.Document document = reader.read(doc);

		org.dom4j.Element root = document.getRootElement();

		this.getNodesValue(root, root, parentString, map);

		return map;
	}


}
