package com.kyrylov.nure.ua;

import com.google.common.collect.Streams;
import com.kyrylov.nure.ua.util.ResourceHelper;
import com.kyrylov.nure.ua.util.XMLHelper;
import com.saxonica.xqj.SaxonXQDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.DC_11;
import org.jsoup.nodes.Document;
import org.xml.sax.SAXException;
import reactor.core.publisher.Flux;


import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xquery.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    public static void main(String[] args) throws IOException, JAXBException, ParserConfigurationException, SAXException, TransformerException, XQException {
        //first();
        //second();
        //third();
        fourth();
    }

    private static void third() throws IOException, SAXException, ParserConfigurationException, XQException {
        InputStream inputStream = new FileInputStream(new File("products.xqy"));
        XQDataSource ds = new SaxonXQDataSource();
        XQConnection conn = ds.getConnection();
        XQPreparedExpression exp = conn.prepareExpression(inputStream);
        XQResultSequence result = exp.executeQuery();

        while (result.next()) {
            System.out.println(result.getItemAsString(null));
        }
    }

    private static void second() throws JAXBException, IOException, TransformerException, ParserConfigurationException, SAXException {
        org.w3c.dom.Document xmlDocument = XMLHelper.readXMLDocument("products.xml");
        MakeUpUtil.addSomeDataToProductsXML(xmlDocument);
        XMLHelper.saveXMLDocument(xmlDocument, "products.xml");
        XMLHelper.createHTMLFromXmlDocument("products.xml", "products.xsl", "products.html");
    }

    private static void first() throws IOException, ParserConfigurationException, JAXBException, SAXException {
        Document htmlDocument = ParserHTML.getDocumentByHTML(ResourceHelper.getProperties()
                .getProperty(ResourceHelper.ProjectProperties.MAKEUP_SITE));
        Products products = MakeUpUtil.parseAndGetNailPolishProducts(htmlDocument);
        org.w3c.dom.Document xmlDocument = XMLHelper.marshallToDocument(products);
        XMLHelper.marshallToFile(products, "products.xml");
        System.out.println("Document is valid - " + XMLHelper.validate(xmlDocument,
                "C:\\Users\\50601\\OneDrive\\JAVA\\ITsWEB\\ITsWEB\\target\\schemagen-work\\compile_scope\\schema1.xsd"));
    }

    private static void fourth() throws IOException, SAXException, ParserConfigurationException {
        org.w3c.dom.Document xmlDocument = XMLHelper.readXMLDocument("products.xml");
        //MakeUpUtil.xmlToRdf(xmlDocument);
        Model model = ModelFactory.createDefaultModel();

        InputStream in = FileManager.get().open("products.rdf");
        model.read(in, null);
        in.close();

        System.out.println("first");
        System.out.println(Streams.stream(model.listResourcesWithProperty(DC_11.language))
                .map(x->Streams.stream(x.listProperties()).count()).mapToLong(aLong -> aLong).sum());

        System.out.println("second");
        ResIterator resIterator1 = model.listResourcesWithProperty(DC_11.language);
        Flux.<ResourceImpl>generate(sink -> {
            if (resIterator1.hasNext())
                sink.next((ResourceImpl) resIterator1.next());
            else sink.complete();
        }).subscribe(x -> {
            System.out.println(x.listProperties().toModel().toString());
        });

        System.out.println("third");
        ResIterator enIterator = model.listResourcesWithProperty(DC_11.language, "en");
        Flux.<ResourceImpl>generate(sink -> {
            if (enIterator.hasNext())
                sink.next((ResourceImpl) enIterator.next());
            else sink.complete();
        }).subscribe(x ->  System.out.println(x.listProperties().toModel().toString()));
    }
}
