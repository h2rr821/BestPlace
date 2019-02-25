package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	
	private String itemId;
	private String name;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;
    private String price;
    private String date;
    
    
    public String getItemId() {
		return itemId;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public double getDistance() {
		return distance;
	}
	
	public String getPrice() {
		
		return price;
	}
	
	public String getDate() {
		
		return date;
	}
    
	
	
	@Override
	public String toString() {
		return "Item [itemId=" + itemId + ", name=" + name + ", address=" + address + ", categories=" + categories
				+ ", imageUrl=" + imageUrl + ", url=" + url + ", distance=" + distance + ", price=" + price + ", date="
				+ date + "]";
	}
	
	public JSONObject toJSONObject()
	{
		JSONObject obj=new JSONObject();
		try
		{
			
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
			obj.put("price",price);
			obj.put("date", date);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;

	}

	private Item(ItemBuilder builder) {
		
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
		this.price=builder.price;
		this.date=builder.date;
		
	}
	
	//=======class ItemBuilder============
	public static class ItemBuilder {
		
		private String itemId;
		private String name;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
	    private String price;
	    private String date;
	    
		public ItemBuilder setItemId(String itemId) {
			this.itemId = itemId;
			return this;
		}
		public ItemBuilder setName(String name) {
			this.name = name;
			return this;
		}
		
		public ItemBuilder setAddress(String address) {
			this.address = address;
			return this;
		}
		public ItemBuilder setCategories(Set<String> categories) {
			this.categories = categories;
			return this;
		}
		public ItemBuilder setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}
		public ItemBuilder setUrl(String url) {
			this.url = url;
			return this;
		}
		public ItemBuilder setDistance(double distance) {
			this.distance = distance;
			return this;
		}
		
		
		public ItemBuilder setPrice(String price)
		{
			this.price=price;
			return this;
		}
		
		public ItemBuilder setDate(String date)
		{
			this.date=date;
			return this;
		}
		//build function
		public Item build()
		{
			return new Item(this);
		}
		
		
	}
   //==========end of ItemBuilder=======================
}
