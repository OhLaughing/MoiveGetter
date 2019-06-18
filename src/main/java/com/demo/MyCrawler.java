package com.demo;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        boolean shouleVisit = !FILTERS.matcher(href).matches();
//                && href.contains(".sina.com.cn/");
        return shouleVisit;
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();

            ParseDianYingTianTang parseDianYingTianTang = new ParseDianYingTianTang();
            parseDianYingTianTang.getInfoFromHTMLStr(html);
            try {
                parseDianYingTianTang.parse();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XpathSyntaxErrorException e) {
                e.printStackTrace();
            }

            String strings = parseDianYingTianTang.getInfos();
            try {
                writeFile(strings, ParseDianYingTianTang.FILEPATH, url);
            } catch (Exception e) {
                System.out.println("将电影信息写进文件失败");
            }
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        }
    }

    private static void writeFile(String infos, String file, String url) throws IOException {
        if (infos == null || "".equals(infos.trim()))
            return;
        File file1 = new File(file);
        if (!file1.getParentFile().exists()) {
            file1.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter(file, true);
        fw.write(infos + "\r\n");
        fw.append("url: " + url + "\n\n\n");
        fw.close();
    }
}
