package recommedation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import database.MySQLConnection;
import entity.Item;

public class ContentBasedRecommend {

	private String getMonth(String mon) {
		
		String month;
		if(mon.equals("01")) {
			
			month="Jan";
		}
		else if (mon.equals("02")) {
			
			
			month="Feb";
		}
		else if (mon.equals("03")) {
			
			month="Mar";
		}
		else if (mon.equals("04")) {
	
	         month="Apr";
        }
        else if (mon.equals("05")) {
	
	          month="May";
        }
        else if (mon.equals("06")) {
	
        	 month="Jun";
        }
        else if (mon.equals("07")) {
	
	         month="Jul";
        }
        else if (mon.equals("08")) {
	
	         month="Aug";
         }
        else if (mon.equals("09")) {
	
	         month="Sep";
        }
        else if (mon.equals("10")) {
	
	         month="Oct";
        }
        else if (mon.equals("11")) {
        	
	         month="Nov";
        }
        else if (mon.equals("12"))
        {
        	month="Dec";
        }
        else {
        	
        	month="";
        }
		
		return month;
	}
	
	public List<Item> recommendItems(String userId, double lat, double lon){
		
		List<Item> recommendedItems = new ArrayList<>();
	    MySQLConnection connection=new MySQLConnection();
		
		try {
			
			Set<String> favoriteItemsId = connection.getFavoriteItemIds(userId);
			
			Set<Item> favoriteItems=connection.getFavoriteItems(userId);
			
			Map<String, Integer> allPair=new HashMap<>();
			for(Item favoriteItem: favoriteItems) {
				
				StringBuilder sb=new StringBuilder();
				sb.append(getMonth(favoriteItem.getDate().substring(5, 7))+" ");
				sb.append(favoriteItem.getCategories().toString()
						.replace("[","").replace("]",""));
			
				if (allPair.containsKey(sb.toString())) {
					
					if(allPair.get(sb.toString())<favoriteItem.getDistance()) {
						
						allPair.put(sb.toString(),(int) Math.ceil(favoriteItem.getDistance()));
					}
					
				
				} else {
					allPair.put(sb.toString(),(int) Math.ceil(favoriteItem.getDistance()+1.5));
				}
				
				
			}
			
			
			List<Entry<String, Integer>> recommendList = new ArrayList<>(allPair.entrySet());
			
			
			Set<Item> visitedItems = new HashSet<>();
			List<Item> filteredItems = new ArrayList<>();
			for (Entry<String, Integer> recKey : recommendList) {
				List<Item> items = connection.searchItems(lat, lon, recKey.getKey(), recKey.getValue().toString());
				System.out.println("search: " +recKey.getKey()+" -- "+ recKey.getValue());
				
				for (Item item : items) {
					if (!favoriteItemsId.contains(item.getItemId()) &&
							!visitedItems.contains(item)) {
						filteredItems.add(item);
					}
				}
	
				visitedItems.addAll(items);
				
			}
			
			//sort the all recommendItem by distance
			Collections.sort(filteredItems, (Item item1, Item item2) -> {
				return Double.compare(item1.getDistance(), item2.getDistance());
			});
			recommendedItems.addAll(filteredItems);
		}
		finally {
			connection.close();
		}
		
		
		return recommendedItems;
	}
}
