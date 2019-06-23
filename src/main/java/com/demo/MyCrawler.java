package com.demo;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    public static final Pattern CHINESE_NAME_PATTERN = Pattern.compile("译　　名\\s*(.*)\\s*.*片　　名\\s*(.*)\\s*.*年　　代\\s*(.*)\\s*.*产　　地\\s*(.*)\\s*.*" +
            "类　　别\\s*(.*)\\s*.*语　　言\\s*(.*)\\s*.*IMDb评分\\s*(.*)\\s*/10.*豆瓣评分\\s*(.*)\\s*/10.*片　　长\\s*(.*)\\s*分钟.*导　　演\\s*(.*)\\s*.*" +
            "主　　演\\s*(.*)\\s*.*标　　签\\s*(.*)\\s*.*简　　介\\s*(.*)【下载地址】");

    public static final String sql ="insert into MOIVE(chinese_name, english_name, director, performer, year, country,category," +
            "tag, language, imdb_score, douban_score, file_length, url) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

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

    public static void writeInfoToDb(String infos) {
        Connection c = DbResource.getConnection();
        Matcher m = CHINESE_NAME_PATTERN.matcher(infos);
        if (m.find()) {
            String chineseName = m.group(1).trim();
            String enlishName = m.group(2).trim();
            String year = m.group(3).trim();
            String country = m.group(4).trim();
            String category = m.group(5).trim();
            String language = m.group(6).trim();
            String imdbScore = m.group(7).trim();
            String doubanScore = m.group(8).trim();
            String length = m.group(9).trim();
            String director = m.group(10).trim();
            String mainActor = m.group(11).trim();
            String tag = m.group(12).trim();
            String desc = m.group(13).trim();

            try {
                PreparedStatement ps = c.prepareStatement(sql);
                ps.setString(1, chineseName);
                ps.setString(2, enlishName);
                ps.setString(3, director);
                ps.setString(4, mainActor);
                ps.setString(5, year);
                ps.setString(6, country);
                ps.setString(7, category);
                ps.setString(8, tag);
                ps.setString(9, language);
                ps.setLong(10, Long.valueOf(imdbScore));
                ps.setLong(11, Long.valueOf(doubanScore));
                ps.setInt(12, Integer.valueOf(length));
                ps.setString(13, "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String s = "淘气小女巫 BD英语中字 2018年奇幻喜剧 \n" +
                "译　　名　淘气小女巫 \n" +
                "片　　名　Die kleine Hexe/The Little Witch \n" +
                "年　　代　2018 \n" +
                "产　　地　德国/瑞士 \n" +
                "类　　别　喜剧/奇幻 \n" +
                "语　　言　英语 \n" +
                "字　　幕　中文 \n" +
                "上映日期　2018(德国) \n" +
                "IMDb评分 6.3/10 from 570 users \n" +
                "豆瓣评分　6.0/10 from 108 users \n" +
                "文件格式　x264 + aac \n" +
                "视频尺寸　1280 x 720 \n" +
                "文件大小　1CD \n" +
                "片　　长　103分钟 \n" +
                "导　　演　Mike Schaerer \n" +
                "编　　剧　Matthias Pacht/Otfried Preußler \n" +
                "主　　演　Karoline Herfurth ....Kleine Hexe 　　　　　　Axel Prahl ....Abraxas (voice) 　　　　　　Luis Vorbach ....Thomas 　　　　　　Momo Beier ....Vroni 　　　　　　Suzanne von Borsody ....Rumpumpel 　　　　　　Barbara Melzl ....Feuerhexe 　　　　　　Carolin Spie? ....Sumpfhexe (as Carolin Spiess) 　　　　　　Eveline Hall ....Nebelhexe 　　　　　　Therese Affolter ....Oberhexe 　　　　　　Angelika B?ttiger ....Mooshexe 　　　　　　Katharina Bohny ....Holzsammlerin 　　　　　　Marina Guerrini ....Holzsammlerin 　　　　　　Verena Bosshard ....Holzsammlerin 　　　　　　Thomas Loibl ....Revierf?rster 　　　　　　Marek Harbort ....Konrad \n" +
                "标　　签　德国 | 2018 | 童话 | 奇幻魔幻 | 奇幻 | 喜剧 | 瑞士 | 电影 \n" +
                "简　　介 　　一个小女巫的故事。她决心成为森林中最好的女巫，并与她的乌鸦Abraxas在另一个冒险中一起滚动。 这样一个小女巫需要你的爱。 【下载地址】 磁力链下载点击这里   ftp://ygdy8:ygdy8@yg45.dydytt.net:7448/阳光电影www.ygdy8.com.淘气小女巫.BD.720p.英语中字.mkv 下载地址2：点击进入     温馨提示：如遇迅雷无法下载可换用无限制版尝试用磁力下载! 下载方法：安装软件后,点击即可下载,谢谢大家支持，欢迎每天来！喜欢本站,请使用Ctrl+D进行添加收藏！ 点击进入：想第一时间下载本站的影片吗？ 下载方法:不会下载的网友先看看 本站电影下载教程";
        Connection c = DbResource.getConnection();
        s = s.replace("\n", "");
        Matcher m = CHINESE_NAME_PATTERN.matcher(s);
        if (m.find()) {
            String chineseName = m.group(1).trim();
            String enlishName = m.group(2).trim();
            String year = m.group(3).trim();
            String country = m.group(4).trim();
            String category = m.group(5).trim();
            String language = m.group(6).trim();
            String imdbScore = m.group(7).trim();
            String doubanScore = m.group(8).trim();
            String length = m.group(9).trim();
            String director = m.group(10).trim();
            String mainActor = m.group(11).trim();
            String tag = m.group(12).trim();
            String desc = m.group(13).trim();

            try {
                PreparedStatement ps = c.prepareStatement(sql);
                ps.setString(1, chineseName);
                ps.setString(2, enlishName);
                ps.setString(3, director);
                ps.setString(4, mainActor);
                ps.setString(5, year);
                ps.setString(6, country);
                ps.setString(7, category);
                ps.setString(8, tag);
                ps.setString(9, language);
                ps.setLong(10, Long.valueOf(imdbScore));
                ps.setLong(11, Long.valueOf(doubanScore));
                ps.setInt(12, Integer.valueOf(length));
                ps.setString(13, "");
                int i = ps.executeUpdate();
                System.out.println(i);
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }
}
