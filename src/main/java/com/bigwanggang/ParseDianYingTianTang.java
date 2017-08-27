package com.bigwanggang;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParseDianYingTianTang {
    Document document;
    private String pageString;
    private String[] infos;

    public void getInfoFromURL(String url) throws IOException {
        document = Jsoup.connect(url).get();
    }

    public void getInfoFromHTMLStr(String html) {
        document = Jsoup.parse(html);
    }

    public void parse() throws IOException {
        Elements elements = document.getElementsByTag("p");
        for (Element e : elements) {
            if (e.text().contains("◎")) {
                pageString = e.text();
                infos = pageString.split("◎");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ParseDianYingTianTang parseDianYingTianTang = new ParseDianYingTianTang();
        parseDianYingTianTang.getInfoFromURL("http://www.ygdy8.net/html/gndy/jddy/20170822/54788.html");
        parseDianYingTianTang.parse();
    }

    public String getPageString() {
        return pageString;
    }

    public String[] getInfos() {
        return infos;
    }
}
