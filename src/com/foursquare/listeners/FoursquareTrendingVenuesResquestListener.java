package com.foursquare.listeners;

import java.util.ArrayList;

import com.foursquare.models.Venue;

public interface FoursquareTrendingVenuesResquestListener extends ErrorListener {
	
	public void onTrendedVenuesFetched(ArrayList<Venue> venues);
	
}
