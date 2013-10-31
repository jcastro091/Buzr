package com.majestyk.buzr.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.foursquare.EasyFoursquare;
import com.foursquare.criterias.VenuesCriteria;
import com.foursquare.models.Venue;
import com.majestyk.buzr.GlobalValues;
import com.majestyk.buzr.R;
import com.majestyk.buzr.adapter.LocationAdapter;
import com.majestyk.buzr.objects.Location;

public class SearchLocationsActivity extends Activity implements OnClickListener, OnItemClickListener {

	EasyFoursquare fs;
	VenuesCriteria criteria;
	ArrayList<Location> list;
	LocationAdapter adapter;
	private EditText search;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_locations);

		fs = new EasyFoursquare(this);
		criteria = new VenuesCriteria();

		list = new ArrayList<Location>();
		adapter = new LocationAdapter (this, list);
		listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(this);

		search = (EditText)findViewById(R.id.search_bar);
		search.setOnClickListener(this);

		getFoursquareVenues("");

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(SearchLocationsActivity.this, CreateOrFindLocations.class);
		startActivityForResult(intent, GlobalValues.REQUEST_CODE_SEARCH_LOCATION + 1);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent res = new Intent().putExtra("GPS", list.get(arg2).getLocationName());
		setResult(GlobalValues.REQUEST_CODE_SEARCH_LOCATION, res);
		finish();				
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode != RESULT_CANCELED) {
			switch(requestCode) {
			case (GlobalValues.REQUEST_CODE_SEARCH_LOCATION + 1):
				if (data.getStringExtra("OPTION").equals("1")) {
					Intent res = new Intent().putExtra("GPS", data.getStringExtra("TEXT"));
					setResult(GlobalValues.REQUEST_CODE_SEARCH_LOCATION, res);
					finish();
				} else if (data.getStringExtra("OPTION").equals("2")) {
					getFoursquareVenues(data.getStringExtra("TEXT"));
					search.setText(data.getStringExtra("TEXT"));
				}
				break;
			}
		}
	}

	private void getFoursquareVenues(String str) {
		list.clear();

		criteria.setQuery(str);
		ArrayList<Venue> venues = fs.getUserlessVenuesNearby(criteria);
		for (Venue v: venues) {
			list.add(new Location(v.getName(), v.getLocation().getAddress()));
		}

		adapter.notifyDataSetChanged();
	}

}