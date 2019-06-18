package com.bigwanggang;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

public class ParseDianYingTianTang {

    public static final String FILEPATH = "D:\\crawler\\movie.txt";
    Document document;
    private String pageString;
    private String infos;

    public void getInfoFromURL(String url) throws IOException {
        document = Jsoup.connect(url).get();
    }

    public void getInfoFromHTMLStr(String html) {
        document = Jsoup.parse(html);
    }

    public void parse() throws IOException, XpathSyntaxErrorException {
        JXDocument jxDocument = new JXDocument(document);
        List<Object> rs = jxDocument.sel("//div[@id=Zoom]");
        if (rs.size() == 1) {
            Element e = (Element) rs.get(0);
            infos = e.text().replace("◎", "\n");
        } else infos = null;
    }

    public static void main(String[] args) throws IOException, XpathSyntaxErrorException {
        ParseDianYingTianTang parseDianYingTianTang = new ParseDianYingTianTang();
        parseDianYingTianTang.getInfoFromURL("http://www.ygdy8.net/html/gndy/dyzz/index.html");
        parseDianYingTianTang.parse();
    }

    public String getPageString() {
        return pageString;
    }

    public String getInfos() {
        return infos;
    }
}
