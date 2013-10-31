package com.foursquare.listeners;

import java.util.ArrayList;

import com.foursquare.models.Venue;

public interface FoursquareVenuesResquestListener extends ErrorListener {
	
	public void onVenuesFetched(ArrayList<Venue> venues);
	
}
