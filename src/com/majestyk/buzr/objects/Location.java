package com.majestyk.buzr.objects;

public class Location {
	
	private String location_name;
	private String location_address;
	
	public Location(String s1, String s2) {
		location_name = s1;
		location_address = s2;
	}

	public String getLocationName() {
		return location_name;
	}

	public void setLocationName(String location_name) {
		this.location_name = location_name;
	}

	public String getLocationAddress() {
		return location_address;
	}

	public void setLocationAddress(String location_address) {
		this.location_address = location_address;
	}

}
