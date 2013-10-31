package com.majestyk.buzr.objects;

import com.majestyk.buzr.Emojis;

public class NotificationItem extends Notification {

	private String Scenario;
	private String UserID;
	private String Username;
	private String Event;
	private String Time;
	private String Target;
	private String Emoji;
	private String UploadID;
	private String User;
	private long UserHash;
	private long TargetHash;
			
	public NotificationItem(String s1, String s2, String s3, String s4, String s5, String s6, String s7, String s8, String s9) {
		this.Scenario= s1;
		this.UserID = s2;
		this.Username = s3;
		this.Event = s4;
		this.Time = s5;
		this.Target = s6;
		this.Emoji = s7;
		this.UploadID = s8;
		this.User = s9;
		setUserHash(this.User.hashCode());
		setTargetHash(this.Target.hashCode());
	}
	
	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getTarget() {
		return Target;
	}

	public void setTarget(String target) {
		Target = target;
	}

	public String getUploadID() {
		return UploadID;
	}

	public void setUploadID(String uploadID) {
		UploadID = uploadID;
	}

	public String getUser() {
		return User;
	}

	public void setUser(String user) {
		User = user;
	}

	public String getScenario() {
		return Scenario;
	}

	public void setScenario(String scenario) {
		Scenario = scenario;
	}

	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

	public Integer getEmoji() {
		if(!Emoji.equals("")) {
			Integer[] pics = Emojis.getPics();
			return pics[(Integer.parseInt(Emoji)+11)%(pics.length)];
		}
		else return 0;
	}

	public void setEmoji(String emoji) {
		Emoji = emoji;
	}

	public long getUserHash() {
		return UserHash;
	}

	public void setUserHash(long userHash) {
		UserHash = userHash;
	}

	public long getTargetHash() {
		return TargetHash;
	}

	public void setTargetHash(long targetHash) {
		TargetHash = targetHash;
	}

}
