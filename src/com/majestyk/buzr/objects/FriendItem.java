package com.majestyk.buzr.objects;

public class FriendItem extends Friend {

	private String SocialID;
	private String Name;
	private String Image;
	private String UserID;
	private String Username;
	private String Privacy;
	private Boolean Following;
	private Boolean Registered;

	public FriendItem(String s1, String s2, String s3, String s4, String s5, String s6, Boolean f1, Boolean f2) {
		this.setSocialID(s1);
		this.setName(s2);
		this.setImage(s3);
		this.setUserID(s4);
		this.setUsername(s5);
		this.setPrivacy(s6);
		this.setFollowing(f1);
		this.setRegistered(f2);
	}

	public String getSocialID() {
		return SocialID;
	}

	public void setSocialID(String socialID) {
		SocialID = socialID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getImage() {
		return Image;
	}

	public void setImage(String image) {
		Image = image;
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

	public String getPrivacy() {
		return Privacy;
	}

	public void setPrivacy(String privacy) {
		Privacy = privacy;
	}

	public Boolean getFollowing() {
		return Following;
	}

	public void setFollowing(Boolean following) {
		Following = following;
	}

	public Boolean getRegistered() {
		return Registered;
	}

	public void setRegistered(Boolean registered) {
		Registered = registered;
	}

}
