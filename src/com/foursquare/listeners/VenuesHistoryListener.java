package com.foursquare.listeners;

import java.util.ArrayList;

import com.foursquare.models.Venues;

public interface VenuesHistoryListener extends ErrorListener {

	public void onGotVenuesHistory(ArrayList<Venues> list);

}
