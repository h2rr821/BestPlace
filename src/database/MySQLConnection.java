package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import java.sql.PreparedStatement;
import entity.Item;
import entity.Item.ItemBuilder;
import externalAPI.TicketMasterAPI;

public class MySQLConnection {
   
	private Connection conn;
	private PreparedStatement stmt;
	private String sql;
	public MySQLConnection() {
		
		try {
			    System.out.println("MySQLConnection created");
			    Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			    
				conn=DriverManager.getConnection(DBUtil.URL);
				
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		
	}
	
	
	public void close() {
		
		if(conn!=null) {
			
			try {
				conn.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			
	}
	
	
	public void setFavoriteItems(String userId, List<String> itemIds, Double dis) {
		// TODO Auto-generated method stub
		if(conn==null)
		{
           System.err.println("DB connection failed");
           return;
		}
		try {
	   		 
			 sql = "INSERT IGNORE INTO favorite_history(user_id, item_id) VALUES (?, ?)";
			 stmt = conn.prepareStatement(sql);
			 stmt.setString(1, userId);
	   		 for (String itemId : itemIds) {
	   			stmt.setString(2, itemId);
	   			stmt.execute();
	   		 }
	   		 
	   		 System.out.println("try to update distance");
	   		 //update distance 
	   		sql = "UPDATE items SET "+"distance=?"+"WHERE item_id=?";
	   		stmt = conn.prepareStatement(sql);
	   		stmt.setDouble(1, dis); 
	   		for (String itemId : itemIds) {
	   			stmt.setString(2, itemId); 
	   			stmt.execute();
	   		}
	   		 
	   		 
	   	 } catch (Exception e) {
	   		 e.printStackTrace();
	   	 }

	}
	
	
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		
		if(conn==null)
		{
           System.err.println("DB connection failed");
           return;
		}
		
		try {
			
	   		 sql = "DELETE FROM favorite_history WHERE user_id=? AND item_id=?";
	   		 stmt = conn.prepareStatement(sql);
	   		 stmt.setString(1, userId);
	   		 
	   		for (String itemId : itemIds) {
	   			stmt.setString(2, itemId);
	   			stmt.execute();
	   		 }
	   		
	   	 } catch (Exception e) {
	   		 e.printStackTrace();
	   	 }
		

	}
	
	public Set<String> getFavoriteItemIds(String userId) {

		if(conn==null)
        {
     	   return new HashSet<>();
        }
		 
		Set<String> favoriteItems=new HashSet<>();		
				
		 try {
	      	   
		      sql="SELECT item_id FROM favorite_history WHERE user_id=?";
		      stmt = conn.prepareStatement(sql);
     		  stmt.setString(1,userId);
     		  ResultSet rs=stmt.executeQuery();
     		  
     		  while(rs.next())
     		  {
     			 String itemId = rs.getString("item_id");
 				 favoriteItems.add(itemId);
     		  }
     		
	   		
	   	   } catch (SQLException e) {
	   		   
	   		 e.printStackTrace();
	   	   }
				
			
		return favoriteItems;	
				
				
	}

	public Set<Item> getFavoriteItems(String userId) {
		
		 if(conn==null)
        {
     	   return new HashSet<>();
        }
        
        Set<Item> favoriteItems=new HashSet<>();
        Set<String> itemIds=getFavoriteItemIds(userId);
        
        try {
     	   
		      sql="SELECT * FROM items WHERE item_id=?";
		      stmt=conn.prepareStatement(sql);
		      
     	      for(String itemId:itemIds)
     	      {
     		      stmt.setString(1, itemId);
     		      
     		      ResultSet rs=stmt.executeQuery(); 
     		      ItemBuilder builder=new ItemBuilder();
     		      
     		      while(rs.next())
     		      {
     		    	builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setDate(rs.getString("date"));
					builder.setDistance(rs.getDouble("distance"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setPrice(rs.getString("price"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					
					favoriteItems.add(builder.build());

     		      }
     		  
     	      }
              		  
	   		
	   	   } catch (SQLException e) {
	   		   
	   		 e.printStackTrace();
	   	   }
		
         return favoriteItems;
         
	}
	
	
    public Set<String> getCategories(String itemId) {
		
		
		if(conn==null)
        {
     	   return new HashSet<>();
        }
		 
		Set<String> categories=new HashSet<>();		
				
		 try {
	      	   
		      String sql="SELECT category FROM categories WHERE item_id=?";
		      PreparedStatement stmt = conn.prepareStatement(sql);
     		  stmt.setString(1,itemId);
     		  ResultSet rs=stmt.executeQuery();
     		  
     		  while(rs.next())
     		  {
     			String category = rs.getString("category");
     			categories.add(category);
     		  }
     		
	   		
	   	   } catch (SQLException e) {
	   		   
	   		 e.printStackTrace();
	   	   }
				
			
		return categories;	
				
				
	}
	
	
	
	public List<Item> searchItems(double lat, double lon, String keyword, String radius, boolean sortByDate, boolean sortByDistance){
		
		TicketMasterAPI ticketMasterAPI= new TicketMasterAPI();
		List<Item> items=ticketMasterAPI.search(lat, lon, keyword, radius, sortByDate, sortByDistance);
		//System.out.println("try to save data to db");
		for(Item item:items) {
			
			//System.out.println(item);
			saveItems(item);
			
		}
		System.out.println("save");
		return items;
	}
	
	
	//save content into database
	public void saveItems(Item item) {
		
		if(conn==null) {
			
			System.err.println("Data Base Connection failed");
			return;
		}
		
		try {
			
			//System.out.println("save...");
			//ignore repeat
			//items=========================================
			sql = "INSERT IGNORE INTO items "+
			            "VALUES (?,?,?,?,?,?,?,?)";
			stmt=conn.prepareStatement(sql);
			
			stmt.setString(1, item.getItemId());
			stmt.setString(2, item.getName());
			stmt.setString(3, item.getAddress());
			stmt.setString(4, item.getDate());
			stmt.setDouble(5, item.getDistance());
			stmt.setString(6, item.getImageUrl());
			stmt.setString(7, item.getPrice());
			stmt.setString(8, item.getUrl());
			stmt.execute();
					
			//categories=======================================
			sql = "INSERT IGNORE INTO categories "+
			             "VALUES (?,?)";
			stmt=conn.prepareStatement(sql);
			
			stmt.setString(1,item.getItemId());
			for(String category: item.getCategories()) {
				
				stmt.setString(2, category);
				stmt.execute();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public String getFullname(String userId) {

		if (conn == null) {
			return "";
		}
		
		String name = "";
		
		try {
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				name = String.join(" ", rs.getString("first_name"), rs.getString("last_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return name;

		
		
	}
	
    public boolean verifyLogin(String userId, String password) {

        
		if (conn == null) {
			return false;
		}
		
		try {
			String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;


		
	}
	
	public int registerUser(String userId, String pwd, String firstName, String lastName) {
		
		int rowAffected=0;
		
		if (conn == null) {
			return 0;
		}
		
		try {
			
			//"INSERT INTO users VALUES ('1111', '3229c1097c00d497a0fd282d586be050', 'John', 'Smith')";
			
			String sql = "INSERT INTO users VALUES (?,?,?,?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, pwd);
			stmt.setString(3, firstName);
			stmt.setString(4, lastName);
			rowAffected = stmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rowAffected;
		
	}
		
        
	
}
