package com.foursquare.listeners;

import com.foursquare.models.Venue;

public interface FoursquareVenueDetailsResquestListener extends ErrorListener {
	
	public void onVenueDetailFetched(Venue venues);
	
}
