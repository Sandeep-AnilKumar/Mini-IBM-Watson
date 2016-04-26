package com.NLPProject;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JdbcSQLiteConnection {

	public static ResultSet executeQuery(String query) 
	{
		try 
		{
			Class.forName("org.sqlite.JDBC");
			//Please give DB URL specific to your machine.
			String dbURL = "jdbc:sqlite::resource:oscar-movie_imdb.sqlite";
			Connection conn = DriverManager.getConnection(dbURL);
			if (conn != null) 
			{
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);
				return rs;
			}
		}  
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		return null;
	}
}