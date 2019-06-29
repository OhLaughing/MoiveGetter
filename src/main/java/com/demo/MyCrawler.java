package com.demo;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MyCrawler extends WebCrawler {

    public static final Pattern CHINESE_NAME_PATTERN = Pattern.compile("译　　名\\s*(.*)");
    public static final Pattern ENGLISTH_NAME_PATTERN = Pattern.compile("片　　名\\s*(.*)");
    public static final Pattern YEAR = Pattern.compile("年　　代\\s*(.*)");
    public static final Pattern COUNTRY = Pattern.compile("产　　地\\s*(.*)");
    public static final Pattern CATEGORY = Pattern.compile("类　　别\\s*(.*)");
    public static final Pattern LANGUAGE = Pattern.compile("语　　言\\s*(.*)");
    public static final Pattern IMDB_SCORE = Pattern.compile("[i,I][m,M][d,D][b,B]评分\\s*(.*)/10");
    public static final Pattern DOUBAN = Pattern.compile("豆瓣评分\\s*(.*)/10");
    public static final Pattern LENGTH = Pattern.compile("片　　长\\s*(.*)分钟");
    public static final Pattern DIRECTOR = Pattern.compile("导　　演\\s*(.*)");
    public static final Pattern ACTORS = Pattern.compile("主　　演\\s*(.*)");
    public static final Pattern TAG = Pattern.compile("标　　签\\s*(.*)");
    public static final Pattern DESC = Pattern.compile("简　　介\\s*(.*)【下载地址】");
    public static final Pattern Score_point = Pattern.compile("(\\d+.\\d+)");
    public static final Pattern Score = Pattern.compile("(\\d+)");


    public static final String sql = "INSERT INTO MOIVE(chinese_name, english_name, director, performer, year, country,category," +
            "tag, language, imdb_score, douban_score, film_length, url) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

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
        log.info("URL: " + url);

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

//                writeFile(strings, ParseDianYingTianTang.FILEPATH, url);
            try {
                writeInfoToDb(strings, url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            log.info("Text length: " + text.length());
            log.info("Html length: " + html.length());
            log.info("Number of outgoing links: " + links.size());
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

    public static void writeInfoToDb(String infos, String url) throws UnsupportedEncodingException {
        Connection c = DbResource.getConnection();
        Matcher chinese_matcher = CHINESE_NAME_PATTERN.matcher(infos);
        Matcher english_matcher = ENGLISTH_NAME_PATTERN.matcher(infos);
        Matcher year_m = YEAR.matcher(infos);
        Matcher country_m = COUNTRY.matcher(infos);
        Matcher category_m = CATEGORY.matcher(infos);
        Matcher language = LANGUAGE.matcher(infos);
        Matcher imdb = IMDB_SCORE.matcher(infos);
        Matcher douban = DOUBAN.matcher(infos);
        Matcher length = LENGTH.matcher(infos);
        Matcher director = DIRECTOR.matcher(infos);
        Matcher actor = ACTORS.matcher(infos);
        Matcher tag = TAG.matcher(infos);

        boolean if_chinese = chinese_matcher.find();
        boolean if_enlisth = english_matcher.find();
        boolean if_year = year_m.find();
        boolean if_country = country_m.find();
        boolean if_category = category_m.find();
        boolean if_language = language.find();
        boolean if_imdb = imdb.find();
        boolean if_douban = douban.find();
        boolean if_director = director.find();
        boolean if_actor = actor.find();
        boolean if_tag = tag.find();
        boolean if_len = length.find();

        boolean if_score = if_imdb || if_douban;

        if (if_chinese && if_enlisth && if_score) {
            log.info("chinese: " + if_chinese);
            log.info("english: " + if_enlisth);
            log.info("if_year: " + if_year);
            log.info("if_country: " + if_country);
            log.info("if_category: " + if_category);
            log.info("if_language: " + if_language);
            log.info("if_imdb: " + if_imdb);
            log.info("if_douban: " + if_douban);
            log.info("if_director: " + if_director);
            log.info("if_actor: " + if_actor);
            log.info("if_tag: " + if_tag);

            log.info("###match moive info success");
            String chineseName = chinese_matcher.group(1).trim();
            String enlishName = english_matcher.group(1).trim();
            String year = if_year ? year_m.group(1).trim() : "";
            String country = if_country ? country_m.group(1).trim() : "";
            String category = if_category ? category_m.group(1).trim() : "";
            String languageStr = if_language ? language.group(1).trim() : "";
            String imdbScore = getScore(if_imdb, imdb);

            String doubanScore = getScore(if_douban, douban);

            String directorStr = if_director ? director.group(1).trim() : "";
            String mainActor = if_actor ? actor.group(1).trim() : "";
            String tagStr = if_tag ? tag.group(1).trim() : "";
            String len = if_len ? length.group(1).trim().replaceAll("　", "") : "0";
            try {
                PreparedStatement ps = c.prepareStatement(sql);
                ps.setString(1, chineseName);
                ps.setString(2, enlishName);
                ps.setString(3, directorStr);
                ps.setString(4, mainActor);
                ps.setString(5, year);
                ps.setString(6, country);
                ps.setString(7, category);
                ps.setString(8, tagStr);
                ps.setString(9, languageStr);
                ps.setDouble(10, Double.valueOf(imdbScore));
                ps.setDouble(11, Double.valueOf(doubanScore));
                ps.setInt(12, Integer.valueOf(len));
                ps.setString(13, url);
                int i = ps.executeUpdate();
                log.info("###write to db result: " + i);
            } catch (SQLException e) {
                String message = e.getMessage();
                if (message.contains("SQLITE_CONSTRAINT_UNIQUE")) {
                    log.info(chineseName + " has saved in db");
                } else {
                    e.printStackTrace();
                }
            }
        } else {
            log.info("###match moive info failuee");
            log.info("chinese: " + if_chinese);
            log.info("english: " + if_enlisth);
            log.info("if_year: " + if_year);
            log.info("if_country: " + if_country);
            log.info("if_category: " + if_category);
            log.info("if_language: " + if_language);
            log.info("if_imdb: " + if_imdb);
            log.info("if_douban: " + if_douban);
            log.info("if_director: " + if_director);
            log.info("if_actor: " + if_actor);
            log.info("if_tag: " + if_tag);
        }
    }

    private static String getScore(boolean if_imdb, Matcher imdb) {
        String score = "0";
        if (if_imdb) {
            score = imdb.group(1).trim();
            if (score.contains(".")) {
                Matcher m = Score_point.matcher(score);
                if (m.find()) {
                    score = m.group(1);
                }
            } else {
                Matcher m = Score.matcher(score);
                if (m.find()) {
                    if (m.find()) {
                        score = m.group(1);
                    }
                }
            }
        }
        return score;
    }

    public static void main(String[] args) {
        String infos = "译　　名　钢铁苍穹2：即临种族 \n" +
                "片　　名　Iron Sky: The Coming Race \n" +
                "年　　代　2019 \n" +
                "产　　地　芬兰/德国 \n" +
                "类　　别　喜剧/动作/科幻 \n" +
                "语　　言　英语 \n" +
                "字　　幕　中英双字幕 \n" +
                "上映日期　2019-01-16(芬兰)/2019-03-21(德国) \n" +
                "IMDb评分 5.2/10 from 1633 users \n" +
                "豆瓣评分　6.4/10 from 116 users \n" +
                "文件格式　x264 + aac \n" +
                "视频尺寸　1280 x 720 \n" +
                "文件大小　1CD \n" +
                "导　　演　季莫·沃伦索拉 Timo Vuorensola \n" +
                "编　　剧　季莫·沃伦索拉 Timo Vuorensola/Dalan Musson \n" +
                "主　　演　茱莉亚·迭泽 Julia Dietze 　　　　　　汤姆·格林 Tom Green 　　　　　　乌多·奇尔 Udo Kier 　　　　　　洛伊德·考夫曼 Lloyd Kaufman 　　　　　　斯黛芬妮·保罗 Stephanie Paul 　　　　　　卡里·凯托宁 Kari Ketonen 　　　　　　Jukka Hilden \n" +
                "标　　签　科幻 | 黑色幽默 | 喜剧 | 德国 | 纳粹 | 德国电影 | 电影 | 芬兰 \n" +
                "简　　介 　　《钢铁苍穹2》(Iron Sky: The Coming Race)得到Atlas International和V International Media的融资，拍摄计划正式启动。续集预算约为1400万美元，九月开拍，预计明年底后期制作完成，打算在2017年的柏林电影节亮相。影片将于戛纳市场卖片。 电影故事设定在前作结局的20年后，地球已经因为核战争沦为空洞荒漠，纳粹月球基地成为人类仅存的避难所。为了寻找传说中的圣杯，人类重返地球，而在他们寻索的路上等待着的将是一个古老的地下人形种族Vril和希特勒带领的恐龙大军…… 【下载地址】 磁力链下载点击这里   ftp://ygdy8:ygdy8@yg45.dydytt.net:3212/阳光电影www.ygdy8.com.钢铁苍穹2：即临种族.BD.720p.中英双字幕.mkv 下载地址2：点击进入     温馨提示：如遇迅雷无法下载可换用无限制版尝试用磁力下载! 下载方法：安装软件后,点击即可下载,谢谢大家支持，欢迎每天来！喜欢本站,请使用Ctrl+D进行添加收藏！ 点击进入：想第一时间下载本站的影片吗？ 下载方法:不会下载的网友先看看 本站电影下载教程\n" +
                "21:31:18.383 [Crawler 3] INFO com.demo.MyCrawler - Text length: 3429";
        Connection c = DbResource.getConnection();

        Matcher chinese_matcher = CHINESE_NAME_PATTERN.matcher(infos);
        Matcher english_matcher = ENGLISTH_NAME_PATTERN.matcher(infos);
        Matcher year_m = YEAR.matcher(infos);
        Matcher country_m = COUNTRY.matcher(infos);
        Matcher category_m = CATEGORY.matcher(infos);
        Matcher language = LANGUAGE.matcher(infos);
        Matcher imdb = IMDB_SCORE.matcher(infos);
        Matcher douban = DOUBAN.matcher(infos);
        Matcher director = DIRECTOR.matcher(infos);
        Matcher actor = ACTORS.matcher(infos);
        Matcher tag = TAG.matcher(infos);


        if (chinese_matcher.find() && english_matcher.find() && year_m.find() && country_m.find()
                && category_m.find() && language.find() && imdb.find() && douban.find() &&
                director.find() && actor.find() && tag.find()) {
            log.info("###match moive info success");
            String chineseName = chinese_matcher.group(1).trim();
            String enlishName = english_matcher.group(1).trim();
            String year = year_m.group(1).trim();
            String country = country_m.group(1).trim();
            String category = category_m.group(1).trim();
            String languageStr = language.group(1).trim();
            String imdbScore = imdb.group(1).trim();
            String doubanScore = douban.group(1).trim().replaceAll("　", "");
            String len = "1";
            String directorStr = director.group(1).trim();
            String mainActor = actor.group(1).trim();
            String tagStr = tag.group(1).trim();

            try {
                PreparedStatement ps = c.prepareStatement(sql);
                ps.setString(1, chineseName);
                ps.setString(2, enlishName);
                ps.setString(3, directorStr);
                ps.setString(4, mainActor);
                ps.setString(5, year);
                ps.setString(6, country);
                ps.setString(7, category);
                ps.setString(8, tagStr);
                ps.setString(9, languageStr);
                ps.setDouble(10, Double.valueOf(imdbScore));
                ps.setDouble(11, Double.valueOf(doubanScore));
                ps.setInt(12, Integer.valueOf(len));
                ps.setString(13, "");
                int i = ps.executeUpdate();
                log.info("###write to db result: " + i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            log.info("###match moive info failuee");
        }


    }
}
