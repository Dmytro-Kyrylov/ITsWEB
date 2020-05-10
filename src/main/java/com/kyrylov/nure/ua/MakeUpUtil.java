package com.kyrylov.nure.ua;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DC_11;
import org.apache.xerces.dom.DeferredElementImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;

public final class MakeUpUtil {

    public static Products parseAndGetNailPolishProducts(Document document) {
        ProductsObjectFactory productsObjectFactory = new ProductsObjectFactory();
        Products products = productsObjectFactory.createProducts();
        for (Element element : document.body().getElementsByClass("simple-slider-list__link")) {
            Products.MakeUpProduct makeUpProduct = productsObjectFactory.createMakeUpProduct();

            makeUpProduct.setLink(document.baseUri().substring(0, document.baseUri().indexOf("ua") + 2)
                    + element.children().get(0).attributes().get("href"));

            Elements mainInformationList = element.children().get(1)
                    .getElementsByClass("info-product-wrapper").get(0).children();

            makeUpProduct.setStatus(mainInformationList.get(0).text());
            makeUpProduct.setReviews(productsObjectFactory.createMakeUpProductReviews());
            makeUpProduct.getReviews().setStars(mainInformationList.get(1).children().get(0).text().length());
            makeUpProduct.getReviews().setComments(Integer.valueOf(mainInformationList.get(1).children()
                    .get(1).text().replaceAll("[^0-9]", "")));
            makeUpProduct.setName(mainInformationList.get(2).text());
            makeUpProduct.setBrand(mainInformationList.get(3).text());
            makeUpProduct.setPrice(Integer.valueOf(mainInformationList.get(4).text()));

            products.getMakeUpProduct().add(makeUpProduct);
        }
        return products;
    }

    public static void addSomeDataToProductsXML(org.w3c.dom.Document document) {
        org.w3c.dom.Element productsNode = document.getDocumentElement();
        NodeList makeUpProductList = document.getDocumentElement().getElementsByTagName("makeUpProduct");
        for (int i = 0; i < makeUpProductList.getLength(); i++) {
            Node makeUpProduct = makeUpProductList.item(i);

            Attr countAttr = document.createAttribute("count");
            countAttr.setValue(String.valueOf(new Random().nextInt() & Integer.MAX_VALUE));
            makeUpProduct.getAttributes().setNamedItem(countAttr);

            if (makeUpProduct.getAttributes().getNamedItem("link") != null) {
                makeUpProduct.getAttributes().removeNamedItem("link");
            }

            NodeList productElements = makeUpProduct.getChildNodes();
            for (int elIdx = 0; elIdx < productElements.getLength(); elIdx++) {
                Node productElement = productElements.item(elIdx);

                if (productElement.getNodeName().equals("brand")) {
                    productElement.getFirstChild().setNodeValue(productElement.getFirstChild().getNodeValue() + "!");
                }

                if (productElement.getNodeName().equals("reviews")) {
                    NodeList reviewData = productElement.getChildNodes();
                    for (int rwdIdx = 0; rwdIdx < reviewData.getLength(); rwdIdx++) {
                        Node reviewElement = reviewData.item(rwdIdx);
                        if (reviewElement.getNodeName().equals("comments")) {
                            reviewElement.getFirstChild().setNodeValue(String.valueOf(new Random().nextInt() & Integer.MAX_VALUE));
                        }
                    }
                }

                if (elIdx == productElements.getLength() - 1) {
                    Node ownersNode;
                    if (((DeferredElementImpl) makeUpProduct).getElementsByTagName("owners").getLength() != 0) {
                        ownersNode = ((DeferredElementImpl) makeUpProduct).getElementsByTagName("owners").item(0);
                    } else {
                        ownersNode = document.createElement("owners");
                        makeUpProduct.appendChild(ownersNode);
                    }

                    org.w3c.dom.Element ownerElement = document.createElement("owner");

                    org.w3c.dom.Element firstName = document.createElement("firstName");
                    firstName.appendChild(document.createTextNode("Dima"));
                    org.w3c.dom.Element lastName = document.createElement("lastName");
                    lastName.appendChild(document.createTextNode("Kirillov"));
                    org.w3c.dom.Element phone = document.createElement("phone");
                    phone.appendChild(document.createTextNode(String.valueOf(new Random().nextLong() & Long.MAX_VALUE)));

                    ownerElement.appendChild(firstName);
                    ownerElement.appendChild(lastName);
                    ownerElement.appendChild(phone);

                    ownersNode.appendChild(ownerElement);
                }

            }

        }
    }

    public static void xmlToRdf(org.w3c.dom.Document document) throws IOException {
        Model model = ModelFactory.createDefaultModel();

        org.w3c.dom.Element productsNode = document.getDocumentElement();
        NodeList makeUpProductList = document.getDocumentElement().getElementsByTagName("makeUpProduct");
        for (int i = 0; i < makeUpProductList.getLength() / 3; i++) {
            Node makeUpProduct = makeUpProductList.item(i);

            Resource productResource = model.createResource(makeUpProduct.getAttributes().getNamedItem("link").getNodeValue());

            NodeList productElements = makeUpProduct.getChildNodes();
            for (int elIdx = 0; elIdx < productElements.getLength(); elIdx++) {
                Node productElement = productElements.item(elIdx);

                if (productElement.getNodeName().equals("name")) {
                    productResource.addProperty(DC_11.title, productElement.getFirstChild().getNodeValue());
                }
                if (productElement.getNodeName().equals("brand")) {
                    String brandRus = translate("en", "ru", productElement.getFirstChild().getNodeValue());
                    productResource.addProperty(DC_11.description, brandRus);
                }
                productResource.addProperty(DC_11.language, "ru");
            }
        }
        for (int i = makeUpProductList.getLength() / 3; i < makeUpProductList.getLength(); i++) {
            Node makeUpProduct = makeUpProductList.item(i);

            Resource productResource = model.createResource(makeUpProduct.getAttributes().getNamedItem("link").getNodeValue());

            NodeList productElements = makeUpProduct.getChildNodes();
            for (int elIdx = 0; elIdx < productElements.getLength(); elIdx++) {
                Node productElement = productElements.item(elIdx);

                if (productElement.getNodeName().equals("name")) {
                    String name = productElement.getFirstChild().getNodeValue();
                    String nameEng = translate("ru", "en", name);
                    productResource.addProperty(DC_11.title, nameEng);
                }
                if (productElement.getNodeName().equals("brand")) {
                    productResource.addProperty(DC_11.description, productElement.getFirstChild().getNodeValue());
                }
                productResource.addProperty(DC_11.language, "en");
            }
        }

        FileOutputStream rdfStream = new FileOutputStream(new File("products.rdf"));
        model.write(rdfStream,"application/rdf+xml");
        rdfStream.close();
    }

    private static String translate(String langFrom, String langTo, String text) throws IOException {
        String urlStr = "https://script.google.com/macros/s/AKfycbz6UGaRMUODk35W9eZIjSz5K3Wc7yi2dHyQeZBph2E2jzeELr8K/exec" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

}
