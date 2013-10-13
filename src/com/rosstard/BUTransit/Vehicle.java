package com.rosstard.BUTransit;

import com.google.android.gms.maps.model.LatLng;

public class Vehicle {
	private int call_name;
	private int vehicle_id;
	private LatLng location;
	private String type;

	//ARRIVAL ESTIMATES
	
	
	public Vehicle(int vehicle_id) {
		this.vehicle_id = vehicle_id;
	}
	public Vehicle(LatLng location, int vehicle_id, String type) {
		this.location = location;
		this.vehicle_id = vehicle_id;

		this.type = type;
	}
	
	public void updateLocation(LatLng location) {
		this.location = location;
	}
}
