package com.majestyk.buzr.objects;

public class NotificationHeader extends Notification {

	private String Text;
			
	public NotificationHeader(String s) {
		this.Text = s;
	}
	
	public String getText() {
		return Text;
	}

	public void setText(String s) {
		this.Text = s;
	}

}
