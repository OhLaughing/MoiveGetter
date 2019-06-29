package com.demo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by gustaov on 2019/6/29.
 */
public class Select {
    public static final String IMDB_SELECT = "SELECT id, imdb_score, chinese_name, english_name FROM moive WHERE imdb_score > 8.5  ORDER BY imdb_score DESC";
    public static final String DOUBAN_SELECT = "select id, douban_score, chinese_name, english_name from moive where douban_score > 8.5 order by douban_score desc";

    public static void main(String[] args) throws SQLException {
        Connection c = DbResource.getConnection();

        PreparedStatement ps = c.prepareStatement(IMDB_SELECT);
        ResultSet result = ps.executeQuery();
        while (result.next()) {
            int id = result.getInt("id");
            String chineseName = result.getString("chinese_name");
            String englishName = result.getString("english_name");
            double imdbScore = result.getDouble("imdb_score");
            System.out.println("id: " + id + " imdb: " + imdbScore + " chinese_name: " + chineseName + " english: " + englishName);
        }

        PreparedStatement ps1 = c.prepareStatement(DOUBAN_SELECT);
        ResultSet result1 = ps1.executeQuery();
        while (result1.next()) {
            int id = result1.getInt("id");
            String chineseName = result1.getString("chinese_name");
            String englishName = result1.getString("english_name");
            double imdbScore = result1.getDouble("douban_score");
            System.out.println("id: " + id + " douban: " + imdbScore + " chinese_name: " + chineseName + " english: " + englishName);
        }

    }
}
