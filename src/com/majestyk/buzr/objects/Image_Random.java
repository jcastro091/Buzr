package com.majestyk.buzr.objects;

public class Image_Random {
	public String user_id;
	public String upload_id;
	public String username;
	public String description;
	public String location;						
	public String timestamp;						
	public String buzr_total;
	public String comment_total;
	public String image;
	public String profile_image;
	public Integer suggested;

	public Image_Random(String s1, String s2, String s3, String s4, String s5, 
			String s6, String s7, String s8, String s9, String s10, Integer i) {
		user_id = s1;
		upload_id = s2;
		username = s3;
		description = s4;
		location = s5;
		timestamp = s6;
		buzr_total = s7;
		comment_total = s8;
		image = s9;
		profile_image = s10;
		suggested = i;
	}
}
