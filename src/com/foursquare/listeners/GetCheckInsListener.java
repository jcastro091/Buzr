package com.foursquare.listeners;

import java.util.ArrayList;

import com.foursquare.models.Checkin;



public interface GetCheckInsListener extends ErrorListener {

	public void onGotCheckIns(ArrayList<Checkin> list);
	
}
