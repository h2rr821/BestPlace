package database;

public class DBUtil {
  
	private static final String HOST_ADDRESS="localhost";
	private static final String PORT_NUM="3306";
	
	private static final String USERNAME="root";
	private static final String PASSWORD="root";
	public static final String DB_NAME="bestplace";
	
	public static final String URL="jdbc:mysql://"+HOST_ADDRESS+":"+PORT_NUM+"/"
			+DB_NAME+"?user="+USERNAME+"&password="+PASSWORD+ "&useUnicode=true&characterEncoding=utf8"+
			"&autoReconnect=true&failOverReadOnly=false&serverTimeZone=UTC";
	
	
}
