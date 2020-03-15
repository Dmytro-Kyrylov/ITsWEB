package com.kyrylov.nure.ua;

import com.kyrylov.nure.ua.util.ResourceHelper;
import com.kyrylov.nure.ua.util.XMLHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    public static void main(String[] args) throws IOException, JAXBException, ParserConfigurationException, SAXException, TransformerException {
        //first();
        second();
    }

    private static void second() throws JAXBException, IOException, TransformerException, ParserConfigurationException, SAXException {
        org.w3c.dom.Document xmlDocument = XMLHelper.readXMLDocument("products.xml");
        MakeUpUtil.addSomeDataToProductsXML(xmlDocument);
        XMLHelper.saveXMLDocument(xmlDocument,"products.xml");
        XMLHelper.createHTMLFromXmlDocument("products.xml","products.xsl","products.html");
    }

    private static void first() throws IOException, ParserConfigurationException, JAXBException, SAXException {
        Document htmlDocument = ParserHTML.getDocumentByHTML(ResourceHelper.getProperties()
                .getProperty(ResourceHelper.ProjectProperties.MAKEUP_SITE));
        Products products = MakeUpUtil.parseAndGetNailPolishProducts(htmlDocument);
        org.w3c.dom.Document xmlDocument = XMLHelper.marshallToDocument(products);
        XMLHelper.marshallToFile(products, "products.xml");
        System.out.println("Document is valid - " + XMLHelper.validate(xmlDocument,
                "D:\\NURE\\ITsWEB\\ITsWEB\\target\\schemagen-work\\compile_scope\\schema1.xsd"));
    }
}
