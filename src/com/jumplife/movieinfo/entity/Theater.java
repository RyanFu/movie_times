package com.jumplife.movieinfo.entity;

import java.util.ArrayList;

public class Theater{
	private int id;
	private String name;
	private ArrayList<Movie> movies;
	private String timeTable;
	private String hallType;
	private String buyLink;
	private int area;
	private String phone;
	private String address;
	private String hall;
	
	public Theater (){
		this(-1, "", new ArrayList<Movie> (10), "", "", "", -1, "", "", "");
	}
	
	public Theater (int id, String name) {
		this(id, name, new ArrayList<Movie> (10), "", "", "", -1, "", "", "");
	}
	
	public Theater (int id, String name, String timeTable, String hallType, String buyLink, int area, String phone, String address, String hall) {
		this(id, name, new ArrayList<Movie> (1), timeTable, hallType, buyLink, area, phone, address, hall);
	}
	
	public Theater (int id, String name, ArrayList<Movie> movies, String timeTable, String hallType, String buyLink,
			int area, String phone, String address, String hall) {
		this.id = id;
		this.name = name;
		this.movies = movies;
		this.timeTable = timeTable;
		this.hallType = hallType;
		this.buyLink = buyLink;
		this.area = area;
		this.phone = phone;
		this.address = address;
		this.hall = hall;
	}
	
	public int getId(){
		return id;
	}
	public void setId(int id){
		this.id = id;
	}

	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public ArrayList<Movie> getMovies(){
		return movies;
	}
	public void setMovies(ArrayList<Movie> movies){
		this.movies = movies;
	}
	
	public String getTimeTable(){
		return timeTable;
	}
	public void setTimeTable(String timeTable){
		this.timeTable = timeTable;
	}

	public String getHallType(){
		return hallType;
	}
	public void setHallType(String hallType){
		this.hallType = hallType;
	}
	
	public String getBuyLink(){
		return buyLink;
	}
	public void setBuyLink(String buyLink){
		this.buyLink = buyLink;
	}
	
	public int getArea(){
		return area;
	}
	public void setArea(int area){
		this.area = area;
	}
	
	public String getPhone(){
		return phone;
	}
	public void setPhone(String phone){
		this.phone = phone;
	}
	
	public String getAddress(){
		return address;
	}
	public void setAddress(String address){
		this.address = address;
	}

	public void setHall(String hall){
		this.hall = hall;
	}
	public String getHall(){
		return hall;
	}
}


