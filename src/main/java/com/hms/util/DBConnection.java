package com.hms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection = null;

    private static final String DRIVER   = "com.mysql.cj.jdbc.Driver";

    // ✅ Paste your exact values from Railway → MySQL → Connect tab
    private static final String HOST     = "monorail.proxy.rlwy.net";  // ← Railway host
    private static final String PORT     = "3306";                       // ← Railway port
    private static final String DATABASE = "railway";                    // ← Railway DB name
    private static final String USERNAME = "root";                       // ← Railway user
    private static final String PASSWORD = "AbCdEfGhIjKlMnOp";         // ← Railway password

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE +
        "?useSSL=true&serverTimezone=Asia/Kolkata&autoReconnect=true";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }
}
