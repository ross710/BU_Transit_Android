package com.rosstard.BUTransit;

public class ListViewObject {
	private String name;
	private boolean isInboundToStuvi;
	private String type;
	private int mins;
	private int stop_id;

	
	
	public ListViewObject(String name, boolean isInboundToStuvii, String type, int mins) {
		this.name = name;
		this.isInboundToStuvi = isInboundToStuvii;
		this.type = type;
		this.mins = mins;
	}
	public ListViewObject(String name, boolean isInboundToStuvii, String type, int mins, int stop_id) {
		this.name = name;
		this.isInboundToStuvi = isInboundToStuvii;
		this.type = type;
		this.mins = mins;
		this.stop_id = stop_id;
	}
	
	public int getStop_id() {
		return stop_id;
	}
	public String toString() {
		return name + isInboundToStuvi + type + mins;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean isInboundToStuvi() {
		return isInboundToStuvi;
	}
	public int getMins() {
		return mins;
	}
}
