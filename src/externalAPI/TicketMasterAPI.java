package externalAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;
import externalAPI.GeoHash;

public class TicketMasterAPI {

	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String API_KEY = "zs229wGICxZ7vdfo0vPiHqsA5vylIIsA";  //you need to have your own key

	
	//https://www.codejava.net/java-se/networking/how-to-use-java-urlconnection-and-httpurlconnection
	//https://docs.oracle.com/javase/tutorial/networking/urls/readingWriting.html
	
	
	public List<Item> search(double lat, double lon, String keyword, String radius)
	{	
		boolean sortByDistance=true;
		boolean sortByDate=false;
		List<Item> itemList;
		if(keyword==null) {
			
			keyword="";
		}
		try
		{
			keyword=java.net.URLEncoder.encode(keyword,"UTF-8");
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}
		
		String geoHash=GeoHash.encodeGeohash(lat, lon, 8);
		
				
		if(radius==null) {
			
			radius="50";
		}
		
		String query=String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s",
				API_KEY, geoHash,keyword,radius);

		//String query=String.format("apikey=%s",
		//	API_KEY);
		System.out.println(query);
		
		try {
			
			//1. create a url obj
			URL url=new URL(URL+"?"+query);
			
			//2. obtain a HTTP connection obj from TicketMaster based URL
			HttpURLConnection httpCon=
				(HttpURLConnection) url.openConnection();
		
			//3. configure the HttpURL connection
			httpCon.setRequestMethod("GET");
			
			//4. sent request to tickmaster and get response, response code could be returned directly
			//response body is saved in inputStream of connection
			int responseCode=httpCon.getResponseCode();
		
			System.out.println("\nsending 'GET' request to URL:"+ URL+"?"+query);
		
			System.out.println("Response Code:"+responseCode);
			
			//5. reading data
			BufferedReader read =new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			String inputLine;
			StringBuilder response=new StringBuilder();
			
			//read the whole response
			while((inputLine=read.readLine())!=null)
			{
			    	response.append(inputLine);
			}
			
			//6. close the connection
			read.close();
			
			
			JSONObject obj=new JSONObject(response.toString());
			JSONObject embedded= obj.getJSONObject("_embedded");
			JSONArray events=embedded.getJSONArray("events");
			System.out.println("===================================================");
			//System.out.println(events.toString(2));
			System.out.println("resquest");
			System.out.println();
			System.out.println();
			System.out.println("===================================================");
			
		
			//return events;
			//call getItemList function
			itemList=getItemList(events);
			
			//sort the event by distance
			if(sortByDistance) {
				
			
				Collections.sort(itemList, new Comparator<Item>() {

					@Override
					public int compare(Item o1, Item o2) {
					
						return Double.compare(o1.getDistance(), o2.getDistance());
					}
				
				});
			}
			
			//sort the event by day
			
			if(sortByDate) {
				
				Collections.sort(itemList, new Comparator<Item>() {
				
					LocalDate d1;
					LocalDate d2;
				
					@Override
					public int compare(Item o1, Item o2) {
					
						d1=LocalDate.parse(o1.getDate());
						d2=LocalDate.parse(o2.getDate());
						if(d1.isBefore(d2))
						{
							return -1;
						}
						else {
						
							return 1;
						}
					}		
				});
			
             }
			
			 return itemList;
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		
		}
		
		
		return new ArrayList();
		
		
	}//end of search
	
	
	
	//event->_embedded-> venues ->address -> line1
	//                                    -> line2
	//                                    -> line3
	//                          -> city
	private String getAddress(JSONObject event) throws JSONException 
	{
		if (!event.isNull("_embedded")) 
		{
			JSONObject embedded = event.getJSONObject("_embedded");
			
			if (!embedded.isNull("venues")) {
				
				JSONArray venues = embedded.getJSONArray("venues");
				
				for (int i = 0; i < venues.length(); ++i) 
				{
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder sb = new StringBuilder();
					
					//address
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						
						if (!address.isNull("line1")) {
							sb.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							sb.append('\n');
							sb.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							sb.append('\n');
							sb.append(address.getString("line3"));
						}
					}
					
					//city
					if (!venue.isNull("city")) {
						
						JSONObject city = venue.getJSONObject("city");
						
						if (!city.isNull("name")) {
							sb.append('\n');
							sb.append(city.getString("name"));
						}
					}
					
					//state
					if(!venue.isNull("state"))
					{
						JSONObject state = venue.getJSONObject("state");
						
						if (!state.isNull("stateCode")) {
							sb.append(',');
							sb.append(state.getString("stateCode"));
						}
					}
					
					//postalCode
					if(!venue.isNull("postalCode"))
					{
						
						sb.append(' ');
						sb.append(venue.getString("postalCode"));
						
					}
					
					String addr = sb.toString();
					if (!addr.equals("")) {
						return addr;
					}
				}
			}
		}
		
		//empty
		return "";

	}

	
	//event->images-> url 
		private String getImageUrl(JSONObject event) throws JSONException 
		{
				
			if(!event.isNull("images"))
			{
				JSONArray array=event.getJSONArray("images");
				
				for(int i=0;i<array.length();i++)
				{
					JSONObject image = array.getJSONObject(i);
					if (!image.isNull("url")) {
						return image.getString("url");
					}

				}
				
			}
			
			
			
			return "";
		}
	
		//event->classifications-> segment->name
		//
		private Set<String> getCategories(JSONObject event) throws JSONException {
		    
			//the unique
			Set<String> categories = new HashSet<>();

			if (!event.isNull("classifications")) {
				JSONArray classifications = event.getJSONArray("classifications");
				
				for (int i = 0; i < classifications.length(); ++i) {
					JSONObject classification = classifications.getJSONObject(i);
					
					if (!classification.isNull("segment")) {
						JSONObject segment = classification.getJSONObject("segment");
						
						if (!segment.isNull("name")) {
							categories.add(segment.getString("name"));
						}
					}
				}
			}

			return categories;

		}

		
		//event->dates ->start-> localDate
		private String getDate(JSONObject event) throws JSONException 
		{
			if (!event.isNull("dates")) 
			{
				JSONObject dates = event.getJSONObject("dates");
				
				if (!dates.isNull("start")) {
					
					JSONObject start = dates.getJSONObject("start");
					
					if(!start.isNull("localDate"))
					{
						return start.getString("localDate");
					}
					
				}
			}
			
			//empty
			return "";

		}

		//event->priceRanges-> min
		//                   -> max
		public String getPrice(JSONObject event) throws JSONException 
		{
			if (!event.isNull("priceRanges")) 
			{
				
				JSONArray priceRanges=event.getJSONArray("priceRanges");
				for (int i = 0; i < priceRanges.length(); ++i) 
				{
					JSONObject prices = priceRanges.getJSONObject(i);
					StringBuilder sb = new StringBuilder();
					
					//address
					if (!prices.isNull("min")) {
						
						sb.append(prices.getDouble("min"));
					}
					
					if (!prices.isNull("max")) {
						
						sb.append('~');
						sb.append(prices.getDouble("max"));
					}
					
					String price = sb.toString();
					if (!price.equals("")) {
						return price;
					}
	        
				}
		     }
			 //empty
			 return "price hided";
       }
		
		
		// Convert JSONArray to a list of item objects.
		//=======================================================================
		
		private List<Item> getItemList(JSONArray events) throws JSONException {

			List<Item> itemList = new ArrayList<Item>();

			for (int i = 0; i < events.length(); ++i) {
				
                JSONObject event = events.getJSONObject(i);
			
			    ItemBuilder builder = new ItemBuilder();
			    
			    if(!event.isNull("name"))
			    {
			    	builder.setName(event.getString("name"));
			    }
			    if(!event.isNull("id"))
			    {
			    	builder.setItemId(event.getString("id"));
			    }
			    
			    if(!event.isNull("url"))
			    {
			    	builder.setUrl(event.getString("url"));
			    }
			    if(!event.isNull("distance"))
			    {
			    	builder.setDistance(event.getDouble("distance"));
			    }
			    
			    builder.setAddress(getAddress(event));
			    builder.setCategories(getCategories(event));
			    builder.setDate(getDate(event));
			    builder.setPrice(getPrice(event));
			    builder.setImageUrl(getImageUrl(event));
			    
			    itemList.add(builder.build());
		     
			}	//end of the for loop
			
			
			
			
			return itemList;
		}
		
		private void queryAPI(double lat, double lon)
		{
			List<Item> itemList=search(lat, lon, null, null);
			//JSONArray events=search(lat, lon, null);
		
			
			

			try
			{
				for (Item item : itemList) {
					JSONObject jsonObject = item.toJSONObject();
					System.out.println(jsonObject);
				}
				
			}
			catch(Exception e)
			{
					e.printStackTrace( );
					
			}
		}
		
		public static void main(String[] args) {
			
			TicketMasterAPI tmApi = new TicketMasterAPI();
			//Ontario, CA
			tmApi.queryAPI(34.03, -117.61);
			//34.026803970336914,-117.61430740356445
			
		}
			
}
