package com.jumplife.movieinfo.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Area {
	private int id;
	private String name;
	
	public Area() {
		this(-1, "");
	}
	
	public Area(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	//取得縣市列表
  	public static ArrayList<Area> getAreaList() {
  		ArrayList<Area> areaList = new ArrayList<Area> (20);
  		String message = "[{\"area\":{\"id\":56,\"name\":\"\u53f0\u5317\u6771\u5340\"}}," +
  						"{\"area\":{\"id\":57,\"name\":\"\u53f0\u5317\u897f\u5340\"}}," +
  						"{\"area\":{\"id\":58,\"name\":\"\u53f0\u5317\u5357\u5340\"}}," +
  						"{\"area\":{\"id\":59,\"name\":\"\u53f0\u5317\u5317\u5340\"}}," +
  						"{\"area\":{\"id\":60,\"name\":\"\u65b0\u5317\u5e02\"}}," +
  						"{\"area\":{\"id\":61,\"name\":\"\u53f0\u5317\u4e8c\u8f2a\"}}," +
  						"{\"area\":{\"id\":62,\"name\":\"\u57fa\u9686\"}}," +
  						"{\"area\":{\"id\":63,\"name\":\"\u6843\u5712\"}}," +
  						"{\"area\":{\"id\":64,\"name\":\"\u4e2d\u58e2\"}}," +
  						"{\"area\":{\"id\":65,\"name\":\"\u65b0\u7af9\"}}," +
  						"{\"area\":{\"id\":66,\"name\":\"\u82d7\u6817\"}}," +
  						"{\"area\":{\"id\":67,\"name\":\"\u53f0\u4e2d\"}}," +
  						"{\"area\":{\"id\":68,\"name\":\"\u5f70\u5316\"}}," +
  						"{\"area\":{\"id\":69,\"name\":\"\u96f2\u6797\"}}," +
  						"{\"area\":{\"id\":70,\"name\":\"\u5357\u6295\"}}," +
  						"{\"area\":{\"id\":71,\"name\":\"\u5609\u7fa9\"}}," +
  						"{\"area\":{\"id\":72,\"name\":\"\u53f0\u5357\"}}," +
  						"{\"area\":{\"id\":73,\"name\":\"\u9ad8\u96c4\"}}," +
  						"{\"area\":{\"id\":74,\"name\":\"\u5b9c\u862d\"}}," +
  						"{\"area\":{\"id\":75,\"name\":\"\u82b1\u84ee\"}}," +
  						"{\"area\":{\"id\":76,\"name\":\"\u5c4f\u6771\"}}," +
  						"{\"area\":{\"id\":77,\"name\":\"\u6f8e\u6e56\"}}]";
  		
  		JSONArray areaArray;
		try {
			areaArray = new JSONArray(message.toString());
			for(int i = 0; i < areaArray.length() ; i++) {
				JSONObject areaJson = areaArray.getJSONObject(i).getJSONObject("area");
				Area area = new Area(areaJson.getInt("id"), areaJson.getString("name"));
				areaList.add(area);
			}
		} 
		catch (JSONException e) {
			e.printStackTrace();
			areaList = null;
		}
		return areaList;	
  	}
}
