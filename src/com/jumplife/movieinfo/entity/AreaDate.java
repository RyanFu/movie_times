package com.jumplife.movieinfo.entity;

import java.util.ArrayList;

public class AreaDate {
	int areaId;
	ArrayList<String> date;
	
	public AreaDate (int areaId, ArrayList<String> date) {
		this.areaId = areaId;
		this.date = date;	
	}
	
	public int getAreaId() {
		return areaId;
	}
	public void setId(int areaId) {
		this.areaId = areaId;
	}
	public ArrayList<String> getDate() {
		return date;
	}
	public void setDate(ArrayList<String>date) {
		this.date = date;
	}
	
}