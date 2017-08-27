package com.bigwanggang;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by gustaov on 2017/8/27.
 */
public class ParseDianYingTianTang {
    private String url;
    private String pageString;

    public ParseDianYingTianTang(String url) {
        this.url = url;
    }

    public void parse() throws IOException {
        Document document = Jsoup.connect(url).get();
        Elements elements = document.getElementsByTag("p");
        for (Element e : elements) {
            System.out.println(e.text());
        }
    }

    public static void main(String[] args) throws IOException {
        ParseDianYingTianTang parseDianYingTianTang = new ParseDianYingTianTang("http://www.ygdy8.net/html/gndy/jddy/20170822/54788.html");
        parseDianYingTianTang.parse();
    }

}
