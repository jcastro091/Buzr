package com.foursquare.listeners;

import java.util.ArrayList;

import com.foursquare.models.Tip;

public interface TipsResquestListener extends ErrorListener {
	
	public void onTipsFetched(ArrayList<Tip> tips);
	
}
