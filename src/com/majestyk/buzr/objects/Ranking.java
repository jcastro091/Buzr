package com.majestyk.buzr.objects;

public class Ranking {
	private String user_id;
	private String username;
	private String count;
	private String image;
	private long hash;
	
	public Ranking (String S1, String S2, String S3, String S4) {
		this.user_id = S1;
		this.username = S2;
		this.count = S3;
		this.image = S4;
		setHash(this.image.hashCode());
	}

	public String getUserId() {
		return user_id;
	}

	public String getUsername() {
		return username;
	}

	public String getCount() {
		return count;
	}

	public String getImage() {
		return image;
	}

	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}
}
