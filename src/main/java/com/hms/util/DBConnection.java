package com.hms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection = null;

    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";
    private static final String HOST     = System.getenv("2itt2j.h.filess.io");
    private static final String PORT     = System.getenv("61032");
    private static final String DATABASE = System.getenv("hospitaldb_luckycount");
    private static final String USERNAME = System.getenv("hospitaldb_luckycount");
    private static final String PASSWORD = System.getenv("9595ee5653468cef6df1246fc39a8450bf8f51ad");

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
        "?useSSL=true&requireSSL=true&serverTimezone=Asia/Kolkata&autoReconnect=true";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }
}
