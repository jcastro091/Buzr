package com.majestyk.buzr.objects;

public class Request {

	private String FollowID;
	private String UserID;
	private String Username;
	private String Description;
			
	public Request(String s1, String s2, String s3, String s4) {
		this.FollowID = s1;
		this.UserID = s2;
		this.Username = s3;
		this.Description = s4;
	}
	
	public String getFollowID() {
		return FollowID;
	}

	public void setFollowID(String followID) {
		FollowID = followID;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

}
