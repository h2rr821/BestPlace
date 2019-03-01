package controlServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import database.MySQLConnection;
import entity.Item;

/**
 * Servlet implementation class FavoriteHistory
 */
@WebServlet("/history")
public class FavoriteHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FavoriteHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession(false);
		if (session == null)
		{
			response.setStatus(403);
			return;
		}
		
		String userId=request.getParameter("user_id");
		if(userId.equals("")) {
			
			response.setStatus(403);
			return;
		}
		JSONArray array=new JSONArray();
		
		
        MySQLConnection connection=new MySQLConnection();
		
		try {
		
			Set<Item> items=connection.getFavoriteItems(userId);
			for(Item item:items) {
				
				//System.out.println(item);
				JSONObject obj=item.toJSONObject();
				obj.append("favorite", true);
				
				array.put(obj);
				
				
			}
			
			ControlHelper.writeJsonArray(response, array);
			
			
		} catch (Exception e) {
		     e.printStackTrace();

		}finally
		{
			
			connection.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		if (session == null)
		{
			response.setStatus(403);
			return;
		}
		
		MySQLConnection connection=new MySQLConnection();
		System.out.println("try to set fav");
		try {
			 
			 JSONObject obj = new JSONObject();
			 JSONObject setfav = ControlHelper.readJSONObject(request);
	   		 String userId = setfav.getString("user_id");
	   		 List<String> itemIds = new ArrayList<>();
	   		 
	   		 itemIds.add(setfav.getString("favorite"));
	   		 
	   		 connection.setFavoriteItems(userId, itemIds);
	   		 
	   		 ControlHelper.writeJsonObject(response, obj.put("result", "ok"));
	   		
		} catch (Exception e) {
	   		 e.printStackTrace();
	   	 } finally {
	   		 connection.close();
	   	 }	
		
	}

	
	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 HttpSession session = request.getSession(false);
		 if (session == null)
		 {
			response.setStatus(403);
			return;
		 }
		
		MySQLConnection connection=new MySQLConnection();
		System.out.println("try to delete fav");
		try {
			 
			 JSONObject obj = new JSONObject();
			 JSONObject deletefav = ControlHelper.readJSONObject(request);
	   		 String userId = deletefav.getString("user_id");
	   		 List<String> itemIds = new ArrayList<>();
	   		 
	   		 itemIds.add(deletefav.getString("favorite"));
	   		 
	   		 connection.unsetFavoriteItems(userId, itemIds);
	   		 
	   		 ControlHelper.writeJsonObject(response, obj.put("result", "ok"));
	   		
		} catch (Exception e) {
	   		 e.printStackTrace();
	   	 } finally {
	   		 connection.close();
	   	 }	
				
				
	}
}
