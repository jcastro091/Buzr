package com.majestyk.buzr.objects;

public class ContactItem extends Friend {

	private String Image;
	private String UserID;
	private String Username;
	private String Privacy;
	private Boolean Following;
	private Boolean Registered;
	
	private String email, phone;

	public ContactItem(String s1, String s2, String s3, String s4, String s5, String s6, Boolean f1, Boolean f2) {
		this.setUserID(s1);
		this.setUsername(s2);
		this.setEmail(s3);
		this.setImage(s4);
		this.setPrivacy(s5);
		this.setPhone(s6);
		this.setFollowing(f1);
		this.setRegistered(f2);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
