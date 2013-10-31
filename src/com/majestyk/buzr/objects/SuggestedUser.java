package com.majestyk.buzr.objects;

import java.util.ArrayList;

public class SuggestedUser {

	public String Image;
	public String UserID;
	public String Username;
	public String Description;
	private Boolean isFollowing;
	
	private ArrayList<JSONImage> ImageList;

	public SuggestedUser(String s1, String s2, String s3, String s4, Boolean b) {
		this(s1, s2, s3, s4, b, null);
	}

	public SuggestedUser(String s1, String s2, String s3, String s4, Boolean b, ArrayList<JSONImage> list) {
		
		this.UserID = s1;
		this.Username = s2;
		this.Description = s3; 
		this.Image = s4;
		this.isFollowing = b;
		
		this.ImageList = list;
	}

	public ArrayList<JSONImage> getImages() {
		return ImageList;
	}

	public void setImages(ArrayList<JSONImage> list) {
		ImageList = list;
	}
	
	public Boolean getIsFollowing() {
		return this.isFollowing;
	}

	public void setIsFollowing(Boolean flag) {
		this.isFollowing = flag;
	}

}
