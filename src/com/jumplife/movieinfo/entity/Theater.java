package com.jumplife.movieinfo.entity;

import java.util.ArrayList;

public class Theater{
	private int id;
	private String name;
	private ArrayList<Movie> movies;
	private ArrayList<Time> timeTable;
	private String hallType;
	private String buyLink;
	private int area;
	private String phone;
	private String address;
	private String hall;
	private String ezCheckId;
	private String ezMovieId;
	private String date;
	
	public Theater (){
		this(-1, "", new ArrayList<Movie> (10),new ArrayList<Time>(1), "", "", -1, "", "", "", "", "", "");
	}
	
	public Theater (int id, String name) {
		this(id, name, new ArrayList<Movie> (10),new ArrayList<Time>(1), "", "", -1, "", "", "", "", "", "");
	}
	
	public Theater (int id, String name, ArrayList<Time> timeTable, String hallType, String buyLink, int area, String phone, String address, String hall ,String ezCheckId ,String ezMovieId ,String date) {
		this(id, name, new ArrayList<Movie> (1),new ArrayList<Time>(1), hallType, buyLink, area, phone, address, hall, ezCheckId , ezMovieId , date);
	}
	
	public Theater (int id, String name, ArrayList<Movie> movies, ArrayList<Time> timeTable, String hallType, String buyLink,
			int area, String phone, String address, String hall ,String ezCheckId ,String ezMovieId ,String date) {
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
		this.ezCheckId = ezCheckId;
		this.ezMovieId = ezMovieId;
		this.date = date;
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
	
	public ArrayList<Time> getTimeTable(){
		return timeTable;
	}
	public void setTimeTable(ArrayList<Time> timeTable){
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
	
	public String getezCheckId(){
		return ezCheckId;
	}
	public void setezChekId(String ezCheckId){
		this.ezCheckId = ezCheckId;
	}
	public String getezMovieId(){
		return ezMovieId;
	}
	public void setezMovieId(String ezMovieId){
		this.ezMovieId = ezMovieId;
	}
	public String getDate(){
		return date;
	}
	public void setDate(String date){
		this.date = date;
	}
	
}


