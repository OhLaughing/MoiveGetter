package com.demo;

import lombok.Data;

/**
 * Created by gustaov on 2019/6/18.
 */
@Data
public class Moive {
    private int id;
    private String chineseName;
    private String englishName;
    private String director;
    private String performer;
    private String year;
    private String country;
    private String category;
    private String language;
    private double imdb_score;
    private double douban_score;
    private int length;
    private String url;
}
