package CustomImplamentation;

import Objects.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.util.*;

class Parser {

    public static void parse(String page, HashTable guitars) throws IOException {

        Document document;
        Guitar guitar;
        try {
            document = Jsoup.connect(page).get();
            Element productList = document.getElementsByClass("products columns-4").first();
            ArrayList<Element> products = productList.children();
            for (Element e : products) {
                guitar = new Guitar();
                guitar.setName(e.getElementsByTag("h2").text());
                guitar.setPrice(e.getElementsByClass("price").text());
                guitars.put(guitar);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


