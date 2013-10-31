package com.majestyk.buzr.adapter;

import android.widget.ImageButton;

public interface OnFriendClickListener {
	public void onAre_FollowClick(int pos, ImageButton button);
	public void onNot_FollowClick(int pos, ImageButton button);
	public void onInviteUserClick(String id);
}