package com.hms.util;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection {

	private static Connection connection = null;
	
	public static Connection getConnection() throws SQLException, IOException, ClassNotFoundException{
		if(connection==null || connection.isClosed()) {
			Properties props = new Properties();
			
			props.load(new FileReader("driver.properties"));
			
			IO.println("Properites loaded");
			
			String driver = props.getProperty("DRIVER");
			String url = props.getProperty("URL");
			String username = props.getProperty("USERNAME");
			String password = props.getProperty("PASSWORD");
			
			Class.forName(driver);
			IO.println("class loaded");
			
			connection=DriverManager.getConnection(url,username,password);
			IO.println("Connection stablished");
		}
		return connection;
	}
}
