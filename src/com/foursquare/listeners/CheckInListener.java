package com.foursquare.listeners;

import com.foursquare.models.Checkin;



public interface CheckInListener extends ErrorListener {

	public void onCheckInDone(Checkin checkin);
	
}
