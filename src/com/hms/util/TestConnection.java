package com.hms.util;

import java.sql.Connection;

public class TestConnection {
	void main() throws Exception{
		Connection con = DBConnection.getConnection();
		
		if(con!=null) {
			IO.println("SUCCESS! Connected to Oracle DB.");
			IO.println("Connectionn object: "+con);
		}
		else {
			IO.println("FAILED.Connection is null.");
		}
	}
}
