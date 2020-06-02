package com.kyrylov.nure.ua;

import com.google.common.collect.Streams;
import com.kyrylov.nure.ua.util.XMLHelper;
import com.saxonica.xqj.SaxonXQDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.sparql.core.DatasetImpl;
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

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Main {

    private static final String REFIX =
            "PREFIX mk: <https://makeup.com.ua/#> \n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
            "PREFIX dc: <http://purl.org/dc/elements/1.1/> \n";


    public static void main(String[] args) throws Exception {
        //first();
        //second();
        //third();
        //fourth();
        sixth();
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
        Document htmlDocument = ParserHTML.getDocumentByHTML("https://sota.kh.ua/apple-iphone-11-128gb-black-63719.html");
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
                .map(x -> Streams.stream(x.listProperties()).count()).mapToLong(aLong -> aLong).sum());

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
        }).subscribe(x -> System.out.println(x.listProperties().toModel().toString()));
    }

    private static void sixth() throws Exception {
        Model model = FileManager.get().loadModel("lab-fifth.rdf");

        System.out.println("11111111111111111");
        String query = REFIX +
                "SELECT (COUNT(*) as ?Triples)" +
                " WHERE {?s ?p ?o .}";

        ResultSet results = executeQuery(query, model);
        Flux.<QuerySolution>generate(sink -> {
            if (results.hasNext())
                sink.next(results.nextSolution());
            else sink.complete();
        }).subscribe(x -> {
            System.out.println(x);
        });

        System.out.println("2222222222222222");
         query = REFIX +
                 "SELECT ?p \n" +
                 "WHERE {?s dc:title ?p .}" +
                 "LIMIT 1";

        ResultSet results2 = executeQuery(query, model);
        Flux.<QuerySolution>generate(sink -> {
            if (results2.hasNext())
                sink.next(results2.nextSolution());
            else sink.complete();
        }).subscribe(x -> {
            System.out.println(x);
        });

        System.out.println("333333333333333");
         query = REFIX +
                 "SELECT ?entity\n" +
                 "WHERE {\n" +
                 "    ?entity rdf:type/rdfs:subClassOf* mk:shaving\n" +
                 "}";

        ResultSet results3 = executeQuery(query, model);
        Flux.<QuerySolution>generate(sink -> {
            if (results3.hasNext())
                sink.next(results3.nextSolution());
            else sink.complete();
        }).subscribe(x -> {
            System.out.println(x);
        });

        System.out.println("44444444444444");
         query = REFIX +
                 "SELECT ?price\n" +
                 "WHERE {\n" +
                 "    ?entity mk:price ?price\n" +
                 "}";

        ResultSet results4 = executeQuery(query, model);
        Flux.<QuerySolution>generate(sink -> {
            if (results4.hasNext())
                sink.next(results4.nextSolution());
            else sink.complete();
        }).subscribe(x -> {
            System.out.println(x);
        });

        System.out.println("55555555555555");
         query = REFIX +
                 "SELECT ?entity\n" +
                 "WHERE {\n" +
                 "    ?entity rdfs:subClassOf* mk:man-product\n" +
                 "}";

        ResultSet results5 = executeQuery(query, model);
        Flux.<QuerySolution>generate(sink -> {
            if (results5.hasNext())
                sink.next(results5.nextSolution());
            else sink.complete();
        }).subscribe(x -> {
            System.out.println(x);
        });

        System.out.println("66666666666");
         query = REFIX +
                 "SELECT ?title\n" +
                 "WHERE {\n" +
                 "   ?g mk:price ?s " +
                 "FILTER(?s = '579')" +
                 "?g dc:title ?title" +
                 "}";

        ResultSet results6 = executeQuery(query, model);
        Flux.<QuerySolution>generate(sink -> {
            if (results6.hasNext())
                sink.next(results6.nextSolution());
            else sink.complete();
        }).subscribe(x -> {
            System.out.println(x);
        });


        System.out.println("77777777777");
         query = REFIX +
                 "SELECT ?title WHERE {?x dc:title ?title\n" +
                 "          FILTER regex(?title, '2' ) }";

        ResultSet results7 = executeQuery(query, model);
        Flux.<QuerySolution>generate(sink -> {
            if (results7.hasNext())
                sink.next(results7.nextSolution());
            else sink.complete();
        }).subscribe(x -> {
            System.out.println(x);
        });
    }

    public static ResultSet executeQuery(String queryString, Model model) throws Exception {
        QueryExecution exec =  QueryExecutionFactory.create(QueryFactory.create(queryString), new
                DatasetImpl(model));
        return exec.execSelect();

    }
}
