package com.kyrylov.nure.ua.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XMLHelper {

    public static Document marshallToDocument(IXml xmlEntity) throws ParserConfigurationException, JAXBException {
        Marshaller jaxbMarshaller = JAXBContext.newInstance(xmlEntity.getClass()).createMarshaller();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        jaxbMarshaller.marshal(xmlEntity, document);
        return document;
    }

    public static void marshallToFile(IXml xmlEntity, String fileName) throws JAXBException, FileNotFoundException {
        Marshaller jaxbMarshaller = JAXBContext.newInstance(xmlEntity.getClass()).createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        OutputStream os = new FileOutputStream(fileName);
        jaxbMarshaller.marshal(xmlEntity, os);
    }

    public static <T extends IXml> T unmarshallFromFile(Class<T> xmlClass, String fileName) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(xmlClass);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return xmlClass.cast(jaxbUnmarshaller.unmarshal(new File(fileName)));
    }

    public static Document readXMLDocument(String xmlPath) throws ParserConfigurationException, IOException, SAXException {
        File fXmlFile = new File(xmlPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(fXmlFile);
    }

    public static void saveXMLDocument(Document document, String pathToSave) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(pathToSave));
        transformer.transform(source, result);
    }

    public static boolean validate(Document xmlDocument, String xsdFile) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        Source schemaFile = new StreamSource(new File(xsdFile));
        Schema schema = factory.newSchema(schemaFile);

        Validator validator = schema.newValidator();

        try {
            validator.validate(new DOMSource(xmlDocument));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean validate(String doc, String xsdFile) throws SAXException, IOException, ParserConfigurationException {
        return validate(readXMLDocument(doc),xsdFile);
    }

    public static void createHTMLFromXmlDocument(String xmlFileName, String xslFileName, String htmlFileName)
            throws TransformerException, IOException {
        Source xml = new StreamSource(new File(xmlFileName));
        Source xslt = new StreamSource(xslFileName);

        StringWriter sw = new StringWriter();

        FileWriter fw = new FileWriter(htmlFileName);
        TransformerFactory tFactory = TransformerFactory.newInstance();
        Transformer trasform = tFactory.newTransformer(xslt);
        trasform.transform(xml, new StreamResult(sw));
        fw.write(sw.toString());
        fw.close();

    }

}
