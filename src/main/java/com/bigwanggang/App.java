package com.bigwanggang;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws XpathSyntaxErrorException, IOException {


        Document document = Jsoup.parse(new File("d:\\workspace\\ideaCode\\crawler-master\\3.html"), "gb2312");

        System.out.println();
        String a = "//*[@id=\"header\"]/div/div[3]/div[3]/div[2]/div[1]/div[1]/form/span/a";
        JXDocument jxDocument = new JXDocument(document);
//        List<Object> rs = jxDocument.sel("//*[@id="Zoom"]/span/p[5]/font[9]");
        List<Object> rs = jxDocument.sel("//div[@id=Zoom]");
        System.out.println(rs.size());
        Element e = (Element) rs.get(0);
        System.out.println(e.text().replace("【", "\n"));
    }
}
