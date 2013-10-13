package com.rosstard.BUTransit;
import java.net.*;
import java.util.HashMap;
import java.io.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.*;

class BackEndWrapper {
	private final String URL_STOPS = "http://api.transloc.com/1.2/stops.json?agencies=bu";
	private final String URL_VEHICLES = "http://api.transloc.com/1.2/vehicles.json?agencies=bu";
	private final String URL_ARRIVAL_ESTIMATES = "http://api.transloc.com/1.2/arrival-estimates.json?agencies=bu";
	private final String URL_ROUTES = "http://api.transloc.com/1.2/routes.json?agencies=bu";

	HashMap<Integer, Stop> stops;
	HashMap<Integer, Vehicle> vehicles;


	public void loadStops() throws JSONException {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(URL_STOPS, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				try {
					stops = new HashMap<Integer, Stop>();
					JSONObject jsonObj = new JSONObject(response);
					JSONArray jsonStopsArray = jsonObj.getJSONArray("data");
					for (int i = 0; i < jsonStopsArray.length(); i++) {
						JSONObject jsonStop = jsonStopsArray.getJSONObject(i);
						String name = jsonStop.getString("name");

						JSONObject locationObj = jsonStop.getJSONObject("location");
						LatLng location = new LatLng(locationObj.getDouble("lat"),locationObj.getDouble("lng"));

						//ROUTES HERE

						int stop_id = jsonStop.getInt("stop_id");

						boolean isInboundToStuVii;
						switch (stop_id) {
						case 4068466: //ST. Mary's
							isInboundToStuVii = false;
							break;
						case 4068470: //Blanford
							isInboundToStuVii = false;
							break;
						case 4068478: //Huntington EastBound
							isInboundToStuVii = false;
							break;
						case 4068482: //710 Albany
							isInboundToStuVii = true;
							break;
						case 4068502: //Myles Standish
							isInboundToStuVii = true;
							break;
						case 4068514: //Marsh Plaza
							isInboundToStuVii = true;
							break;
						case 4108734: //518 Park Dr (South Campus)
							isInboundToStuVii = false;
							break;
						case 4108738: //Granby St
							isInboundToStuVii = false;
							break;
						case 4108742: //GSU
							isInboundToStuVii = true;
							break;
						case 4110206: //Kenmore
							isInboundToStuVii = false;
							break;
						case 4110214: //CFA
							isInboundToStuVii = true;
							break;
						case 4114006: //Agganis Way
							isInboundToStuVii = true;
							break;
						case 4114010: //Danielsen Hall
							isInboundToStuVii = true;
							break;
						case 4114014: //Silber Way
							isInboundToStuVii = true;
							break;
						case 4117694: //815 Albany
							isInboundToStuVii = true;
							break;
						case 4117698: //Amory St
							isInboundToStuVii = false;
							break;
						case 4117702: //Huntington Westbound
							isInboundToStuVii = true;
							break;
						case 4117706: //StuVii (10 Buick St)
							isInboundToStuVii = false;
							break;
						case 4117710: //StuVii2
							isInboundToStuVii = false;
							break;
						default:
							isInboundToStuVii = false;
							break;
						}
						
						
						stops.put(stop_id, new Stop(name, location, stop_id, isInboundToStuVii));

					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
                Log.v("stops", stops.toString());
            }
        });
    }
    
    public void loadVehicles() throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL_VEHICLES, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
            	try {
					vehicles = new HashMap<Integer, Vehicle>();
					JSONObject jsonObj = new JSONObject(response);
					JSONObject jsonData = jsonObj.getJSONObject("data");
					JSONArray oneThirtyTwo = jsonObj.getJSONArray("132");

					for (int i = 0; i < oneThirtyTwo.length(); i++) {
						JSONObject jsonStop = oneThirtyTwo.getJSONObject(i);
//						String name = jsonStop.getString("name");

						JSONObject locationObj = jsonStop.getJSONObject("location");
						LatLng location = null;
						if (locationObj != null) {
							location = new LatLng(locationObj.getDouble("lat"),locationObj.getDouble("lng"));
						}

						//arrival EsTIMATES

						int vehicle_id = jsonStop.getInt("vehicle_id");
						String type;
						switch (vehicle_id) {
		                case 4007492:
		                {
		                    type = "Small bus";
		                    break;
		                }
		                case 4007496:
		                {
		                    type = "Small bus";
		                    break;
		                }
		                case 4007500:
		                {
		                    type = "Small bus";
		                    break;
		                }
		                case 4007504:
		                {
		                    type = "Small bus";
		                    break;
		                }
		                case 4007508:
		                {
		                    type = "Small bus";
		                    break;
		                }
		                case 4007512:
		                {
		                    type = "Big bus";
		                    break;
		                }
		                case 4008320:
		                {
		                    type = "Big bus";
		                    break;
		                }
		                case 4009127:
		                {
		                    type = "Big bus";
		                    break;
		                }
		                default:
		                {
		                    type = "Unknown size of bus";
		                    break;
		                }
		            }

						
						
					vehicles.put(vehicle_id, new Vehicle(location, vehicle_id, type));
					
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
                Log.v("stops", stops.toString());
            
            }
        });
    }
    
    public void loadArrivalEstimates() throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL_ARRIVAL_ESTIMATES, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.v("stops", response);
            }
        });
    }
    public void loadRoutes() throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL_ROUTES, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.v("stops", response);
            }
        });
    }
}

