package com.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by gustaov on 2019/6/23.
 */
public class DbResource {
    private static Connection c;

    private static DbResource dbResource = new DbResource();

    private DbResource() {
        String dbFile = "jdbc:sqlite:moive.db";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(dbFile);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getC(){
        return c;
    }


    public static Connection getConnection(){
        return dbResource.getC();
    }

    public static void main(String[] args) {
        System.out.println(DbResource.getConnection());
    }
}
