package controlServlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import recommedation.ContentBasedRecommend;

/**
 * Servlet implementation class UserRecommendation
 */
@WebServlet("/userrecommendation")
public class UserRecommendation extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserRecommendation() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null)
		{
			response.setStatus(403);
			return;
		}
		
		String userId = request.getParameter("user_id");
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		boolean sortByDate = Boolean.parseBoolean(request.getParameter("sortByDate"));
		boolean sortByDistance = Boolean.parseBoolean(request.getParameter("sortByDistance"));
		
		ContentBasedRecommend recommendation=new ContentBasedRecommend();
		
		List<Item> items=recommendation.recommendItems(userId, lat, lon, sortByDate, sortByDistance);
		
		JSONArray result = new JSONArray();
		try {
			
			for (Item item : items) {
				
				result.put(item.toJSONObject().put("favorite", false));
				
			}
			
			ControlHelper.writeJsonArray(response, result);
			
		} catch (Exception e) {
			e.printStackTrace();
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
