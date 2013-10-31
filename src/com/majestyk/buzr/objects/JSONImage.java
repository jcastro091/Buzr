package com.majestyk.buzr.objects;

public class JSONImage {

	private String ImageID;
	private String ImageURL;
	
	public JSONImage(String s1, String s2) {
		this.ImageID = s1;
		this.ImageURL = s2;
	}
	
	public String getUID () {
		return this.ImageID;
	}

	public String getURL () {
		return this.ImageURL;
	}
}