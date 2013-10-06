package com.rosstard.BUTransit;

import com.google.android.gms.maps.model.LatLng;

public class Stop {
	private String name;
	private String []routes;
	private int stop_id;
	private boolean isInboundToStuVi;
	private LatLng location;
	
	
	public Stop(String name, int stop_id, String[]routes, boolean isInboundToStuVi) {
		this.name = name;
		this.stop_id = stop_id;
		this.routes = routes;
		this.isInboundToStuVi = isInboundToStuVi;
	}

}