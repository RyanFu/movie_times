package com.jumplife.movieinfo.entity;

public class Time {
	
	String time;
	String SessionId;
	
	public Time (String time, String SessionId) {
		this.time = time;
		this.SessionId = SessionId;
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSessionId() {
		return SessionId;
	}
	public void setSessionId(String SessionId) {
		this.SessionId = SessionId;
	}
	
}
