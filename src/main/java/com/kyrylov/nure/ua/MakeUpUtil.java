package com.kyrylov.nure.ua;

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

}
