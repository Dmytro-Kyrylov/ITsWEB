package com.kyrylov.nure.ua;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class ParserHTML {

    public static Document getDocumentByHTML(String html) throws IOException {
        return Jsoup.connect(html).get();
    }
}
