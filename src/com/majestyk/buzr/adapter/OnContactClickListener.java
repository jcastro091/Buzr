package com.majestyk.buzr.adapter;

import com.majestyk.buzr.objects.ContactItem;

import android.widget.ImageButton;

public interface OnContactClickListener {
	public void onAre_FollowClick(ContactItem friend, ImageButton button);
	public void onNot_FollowClick(ContactItem friend, ImageButton button);
	public void onInviteUserClick(String phone);
}