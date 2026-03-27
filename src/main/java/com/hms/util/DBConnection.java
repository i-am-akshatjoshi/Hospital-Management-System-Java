package com.hms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection = null;

    // ── Change these 4 values to match your driver.properties ──
    private static final String DRIVER   = "oracle.jdbc.driver.OracleDriver";
    private static final String URL      = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USERNAME = "ROLE";   // ← change this
    private static final String PASSWORD = "akshat";   // ← change this

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        }
        return connection;
    }
}