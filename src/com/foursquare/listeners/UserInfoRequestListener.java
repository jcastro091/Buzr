package com.foursquare.listeners;

import com.foursquare.models.User;

public interface UserInfoRequestListener extends ErrorListener {

	public void onUserInfoFetched(User user);
}
