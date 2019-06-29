package com.demo;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.demo.MyCrawler.writeInfoToDb;

/**
 * Created by gustaov on 2019/6/26.
 */
@Slf4j
public class OkHttpDemo {
    public static final String base = "https://www.dytt8.net";

    public static void main(String[] args) {
        String url1 = "https://www.dytt8.net/html/gndy/dyzz/list_23_%s.html";
        for (int i = 60; i <195; i++) {

            String url = String.format(url1, String.valueOf(i));
            OkHttpClient okHttpClient = new OkHttpClient();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    log.info("onFailure: ");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String homepage = new String(response.body().bytes(), "gb2312");
                    log.info("onResponse: " + homepage);
                    Document doc = Jsoup.parse(homepage);
                    Elements elements = doc.select("#header > div > div.bd2 > div.bd3 > div.bd3r > div.co_area2 > div.co_content8 > ul > table");
                    for (Element element : elements) {
                        Elements e = element.select("a[href]");
                        String title = e.first().text();
                        System.out.println(title);
                        System.out.println(e.attr("href"));
                        String url = e.attr("href");
                        ParseDianYingTianTang dianYingTianTang = new ParseDianYingTianTang();
                        dianYingTianTang.getInfoFromURL(base + url);
                        try {
                            dianYingTianTang.parse();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (XpathSyntaxErrorException e1) {
                            e1.printStackTrace();
                        }

                        String strings = dianYingTianTang.getInfos();

//                writeFile(strings, ParseDianYingTianTang.FILEPATH, url);
                        writeInfoToDb(strings, url);
                        try {
                            TimeUnit.SECONDS.sleep(2);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }

                    }
                }
            });
            System.out.println("ok");

            try {
                TimeUnit.MINUTES.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("********************************: " + i);
        }
    }
}
