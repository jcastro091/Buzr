package com.foursquare.listeners;

import java.util.ArrayList;

import com.foursquare.models.User;

public interface FriendsListener extends ErrorListener {

	public void onGotFriends(ArrayList<User> list);
	
}
