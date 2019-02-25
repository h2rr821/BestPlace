package controlServlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import database.MySQLConnection;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		MySQLConnection connection=new MySQLConnection();
		
		try {
			
			JSONObject input = ControlHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			String pwd = input.getString("password");
			String firstName=input.getString("firstname");
			String lastName=input.getString("lastname");
			JSONObject obj = new JSONObject();
			String name=connection.getFullname(userId);
			
			//System.out.println("check the user exist? "+ name);
			if(name.equals("")) {
				//create a user
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				int res=connection.registerUser(userId, pwd, firstName, lastName);
				
				if(res==0) {
					
					obj.put("result","Sorry, an error occured");
				}
				else {
					
					obj.put("result", "User registered successfully");
				}
				
			}
			else {
				//user exist
				
				obj.put("result", "user exist");
				
			}
			
			ControlHelper.writeJsonObject(response, obj);
			
			
		}
         catch (Exception e) {
		
   		     e.printStackTrace();
   	 
	    } finally {
	    	
    	     connection.close();
        }
	
	}

}
