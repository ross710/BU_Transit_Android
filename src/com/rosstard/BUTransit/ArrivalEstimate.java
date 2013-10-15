package com.rosstard.BUTransit;

import java.util.Date;


public class ArrivalEstimate {
	private Date arrival_at;
	private int vehicle_id;
	private int stop_id;
	
	
	public ArrivalEstimate(Date arrival_at, int vehicle_id, int stop_id) {
		this.arrival_at = arrival_at;
		this.vehicle_id = vehicle_id;
		this.stop_id = stop_id;
	}
	
	public Date arrival_at() {
		return arrival_at;
	}
	
	public int vehicle_id() {
		return vehicle_id;
	}
	public int stop_id() {
		return stop_id;
	}
}
