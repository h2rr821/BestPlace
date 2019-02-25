package controlServlet;

import java.io.IOException;
import java.io.PrintWriter;
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
import externalAPI.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/usersearch")
public class UserSearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserSearchItem() {
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
		
		double lat=Double.parseDouble(request.getParameter("lat"));
		double lon=Double.parseDouble(request.getParameter("lon"));
		String keyword=request.getParameter("keyword");
		String userId = request.getParameter("user_id");
		String radius = request.getParameter("radius");
		
		System.out.println("servicelet keyword-->"+keyword);
		
		MySQLConnection connection=new MySQLConnection();
		
		try {
			
			//System.out.println("searchItem function");
			List<Item> items=connection.searchItems(lat, lon, keyword, radius);
			Set<String> favoriteItems = connection.getFavoriteItemIds(userId);
			
			JSONArray array=new JSONArray();
			for(Item item:items) {
				
				//System.out.println("inside servlet: "+item);
				JSONObject obj=item.toJSONObject();
				obj.put("favorite", favoriteItems.contains(item.getItemId()));
				array.put(obj);
				
				
			}
			
			ControlHelper.writeJsonArray(response, array);
				
		}
		catch(Exception e) {
				
			e.printStackTrace();
		}
		finally {
			
			connection.close();
		}
			
		 
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
