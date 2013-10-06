package com.rosstard.BUTransit;

import com.google.android.gms.maps.model.LatLng;

public class Vehicle {
	private int call_name;
	private int vehicle_id;
	private LatLng location;
	//ARRIVAL ESTIMATES
	
	
	public Vehicle(int vehicle_id) {
		this.vehicle_id = vehicle_id;
	}
	
	public void updateLocation(LatLng location) {
		this.location = location;
	}
}
