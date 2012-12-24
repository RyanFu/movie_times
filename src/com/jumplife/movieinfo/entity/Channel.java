package com.jumplife.movieinfo.entity;

public class Channel {
	
	int id;
	int channel_num;
	String channel_name;
	
	public Channel (int id, int channel_num, String channel_name) {
		this.id = id;
		this.channel_num = channel_num;
		this.channel_name = channel_name;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNum() {
		return channel_num;
	}
	public void setNum(int channel_num) {
		this.channel_num = channel_num;
	}
	public String getName() {
		return channel_name;
	}
	public void setName(String channel_name) {
		this.channel_name = channel_name;
	}
}
