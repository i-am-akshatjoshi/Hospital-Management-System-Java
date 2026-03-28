package com.hms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection = null;

    private static final String DRIVER   = "org.postgresql.Driver";

    // Copy these exact values from Railway → PostgreSQL → Connect tab
    private static final String HOST     = "monorail.proxy.rlwy.net";  // ← your Railway host
    private static final String PORT     = "12345";                      // ← your Railway port
    private static final String DATABASE = "railway";                    // ← your Railway DB name
    private static final String USERNAME = "postgres";                   // ← your Railway user
    private static final String PASSWORD = "AbCdEfGhIjKlMnOp";         // ← your Railway password

    private static final String URL =
        "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }
}
