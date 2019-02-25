package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import entity.Item;
import externalAPI.TicketMasterAPI;

public class MySQLTableCreation {

	public static void main(String [] args) throws SQLException{
		
		Connection conn=null;
		Statement stmt=null;
		
		try {
			
			//Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn=DriverManager.getConnection(DBUtil.URL);

			//determine whether it connected
			if(conn==null) {
				
				return;
			}
			
			stmt=conn.createStatement();
		    
			
			//Order matter
			//drop all the table if exists
			String sql="DROP TABLE IF EXISTS categories"; 
			stmt.executeUpdate(sql);
		
			sql="DROP TABLE IF EXISTS favorite_history";
            //sql="DROP TABLE IF EXISTS history";
            stmt.executeUpdate(sql);
            
            //test=========================================
            
            sql="DROP TABLE IF EXISTS history";
            stmt.executeUpdate(sql);
            //=========================================
            
            sql="DROP TABLE IF EXISTS items";
            stmt.executeUpdate(sql);
            
            sql="DROP TABLE IF EXISTS users";
            stmt.executeUpdate(sql);
            
         
			//Create table items 
			sql="CREATE TABLE items ("
			           +"item_id VARCHAR(255) NOT NULL PRIMARY KEY,"
					   +"name VARCHAR(255),"
			           +"address VARCHAR(255),"
					   +"date VARCHAR(255),"
					   +"distance FLOAT,"
			           +"image_url VARCHAR(255),"
					   +"price VARCHAR(255),"
					   +"url VARCHAR(255)"
					   +");";
			
			stmt.executeUpdate(sql); 
			
			//Create table users 
			sql="CREATE TABLE users ("
			           +"user_id VARCHAR(255) NOT NULL PRIMARY KEY,"
					   +"password VARCHAR(255) NOT NULL,"
			           +"first_name VARCHAR(255),"
					   +"last_name VARCHAR(255)"
			           +");";
			
			stmt.executeUpdate(sql); 
			
			//Create table categories 
			sql="CREATE TABLE categories ("
			           +"item_id VARCHAR(255) NOT NULL,"
					   +"category VARCHAR(255) NOT NULL,"
			           +"PRIMARY KEY (item_id,category),"
					   +"FOREIGN KEY (item_id) REFERENCES items(item_id)"
					   +");";
			
			stmt.executeUpdate(sql); 
			
			//Create table favorite_history 
			
			sql="CREATE TABLE favorite_history ("
			           +"user_id VARCHAR(255) NOT NULL,"
					   +"item_id VARCHAR(255) NOT NULL,"
			           +"add_fav_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
			           +"PRIMARY KEY (user_id,item_id),"
					   +"FOREIGN KEY (user_id) REFERENCES users(user_id),"
					   +"FOREIGN KEY (item_id) REFERENCES items(item_id)"
					   +");";
			
			stmt.executeUpdate(sql); 
			
			
			//System.out.println("Table create successfully");
			
		}
		catch(Exception e) {
			
			e.printStackTrace();
		}
		finally {
			
			if(stmt!=null) {
				stmt.close();
			}
			
			if(conn!=null) {
				
				conn.close();
			}
		}
		
		
	}
	
	
}
