package controlServlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		JSONArray array=new JSONArray();
		double lat=Double.parseDouble(request.getParameter("lat"));
		double lon=Double.parseDouble(request.getParameter("lon"));
		String keyword=request.getParameter("keyword");
		//boolean sortByDate = Boolean.parseBoolean(request.getParameter("sortByDate"));
		//boolean sortByDistance = Boolean.parseBoolean(request.getParameter("sortByDistance"));
		
		
		
		MySQLConnection connection=new MySQLConnection();
		
		try {
			
			HttpSession session = request.getSession();
			//session.setAttribute("user_id", userId);
			// Set session to expire in 1 minutes.
			session.setMaxInactiveInterval(1* 60);
			//System.out.println("searchItem function");
			List<Item> items=connection.searchItems(lat, lon, keyword, null, false, true);
			for(Item item:items) {
				
				//System.out.println("inside servlet: "+item);
				JSONObject obj=item.toJSONObject();
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
