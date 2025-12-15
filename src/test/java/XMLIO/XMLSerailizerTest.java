package XMLIO;


import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import metaModel.Model;


class XMLSerailizerTest {

	@Test
	void test0() throws TransformerException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		// Use a Transformer for output
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		Result result = new StreamResult(new File("exemple0-out.xml"));
		transformer.transform(source, result);
	}

	@Test
	void test1() throws TransformerException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		Element el0 = document.createElement("Model");
		document.appendChild(el0);
		Attr attr = document.createAttribute("id");
		attr.setValue("#3");
		el0.setAttributeNode(attr);
		// Use a Transformer for output
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		Result result = new StreamResult(new File("exemple3-out.xml"));
		transformer.transform(source, result);
	}
	
	@Test
	void test2() throws TransformerException {
		StreamSource source = new StreamSource(new StringReader("<Entity id=\"#5\" name=\"Balise\"/>"));
	    StringWriter writer = new StringWriter();
	    StreamResult result = new StreamResult(writer);
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();
	    transformer.transform(source,result);
	    String strResult = writer.toString();
	    System.out.println(strResult);
	}
	
	@Test
	void test3() throws ParserConfigurationException, TransformerException {
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromFilenamed("Exemple1.xml");
		XMLSerializer serializer = new XMLSerializer();
		model.accept(serializer);
		Document document = serializer.result();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		Result result = new StreamResult(new File("exemple1-out.xml"));
		transformer.transform(source, result);
	}
	
	@Test
	void test4() throws ParserConfigurationException, TransformerException {
		XMLAnalyser analyser = new XMLAnalyser();
		Model model = analyser.getModelFromFilenamed("Exemple2.xml");
		XMLSerializer serializer = new XMLSerializer();
		model.accept(serializer);
		Document document = serializer.result();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		Result result = new StreamResult(new File("exemple2-out.xml"));
		transformer.transform(source, result);
	}



}
