package com.majestyk.buzr.objects;

public class FollowItem {

	private String FID;
	private String UID;
	private String USR;
	private String DES;
	private String FLG;
	private String URL;
			
	public FollowItem(String s1, String s2, String s3, String s4, String s5, String s6) {
		this.FID = s1;
		this.UID = s2;
		this.USR = s3;
		this.DES = s4;
		this.FLG = s5;
		this.URL = s6;
	}
	
	public String getFID () {
		return this.FID;
	}

	public String getUID () {
		return this.UID;
	}

	public String getUSR () {
		return this.USR;
	}

	public String getDES () {
		return this.DES;
	}

	public String getURL () {
		return this.URL;
	}

	public String getFLG() {
		return FLG;
	}

	public void setFLG(String fLG) {
		FLG = fLG;
	}

}
