//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.03.02 at 02:12:53 PM EET 
//

package com.kyrylov.nure.ua;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the blog.thoughts.on.java package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public final class ProductsObjectFactory {

    public Products createProducts() {
        return new Products();
    }

    public Products.MakeUpProduct createMakeUpProduct() {
        return new Products.MakeUpProduct();
    }

    public Products.MakeUpProduct.Reviews createMakeUpProductReviews() {
        return new Products.MakeUpProduct.Reviews();
    }

}
