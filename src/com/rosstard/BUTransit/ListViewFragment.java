package com.rosstard.BUTransit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.google.android.gms.maps.SupportMapFragment;


import android.content.Context;
//Help from: https://gist.github.com/joshdholtz/4522551
@SuppressLint("ValidFragment")
public class ListViewFragment extends Fragment {

    private final String URL_STOPS = "http://api.transloc.com/1.2/stops.json?agencies=bu";
	private final String URL_VEHICLES = "http://api.transloc.com/1.2/vehicles.json?agencies=bu";
	private final String URL_ARRIVAL_ESTIMATES = "http://api.transloc.com/1.2/arrival-estimates.json?agencies=bu";
	private final String URL_ROUTES = "http://api.transloc.com/1.2/routes.json?agencies=bu";

    private final Handler handler = new Handler();
    private Runnable r;

    
	private HashMap<Integer, Stop> stops;

	private HashMap<Integer, Vehicle> vehicles;
	private HashMap<Integer, Marker> vehicleMarkers = new HashMap<Integer, Marker>();
	private ArrayAdapter<String> listAdapter;  
	private ArrayList<String> list;
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	public ListViewFragment() {
	}


	
	@Override
	public void onResume() {
        handler.post(r);

		super.onResume();
	}
 

 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
 

    
    @Override
	public void onPause() {
        handler.removeCallbacks(r);
        super.onPause();
       
        
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main_list,
				container, false);
        
        final ListView listView = (ListView) v.findViewById(R.id.listView);

        list = new ArrayList<String>(); 

   
        // Create ArrayAdapter using the planet list.  
        listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.cell, list);  
        // Add more planets. If you passed a String[] instead of a List<String>   
        // into the ArrayAdapter constructor, you must not add more items.   
        // Otherwise an exception will occur.  
        
        listView.setAdapter(listAdapter);
        try {
			loadStops();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        r = new Runnable() {
            @Override
            public void run() {
//                try {
//        			loadVehicles();
//        			listView.invalidateViews();
//        		} catch (JSONException e) {
//        			// TODO Auto-generated catch block
//        			e.printStackTrace();
//        		}
                handler.postDelayed(this, 5000);
            }
        };
        
        handler.post(r);

        
        
     // Create and populate a List of planet names.  
//        String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",  
//                                          "Jupiter", "Saturn", "Uranus", "Neptune"};    
//        ArrayList<String> planetList = new ArrayList<String>();  
//        planetList.addAll( Arrays.asList(planets) );  
          

//        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        
//        
//        
//        
//        LocationListener locationListener = new LocationListener() {
//			public void onLocationChanged(Location location) {
//				// Called when a new location is found by the network location provider.
//				String lat = Double.toString(location.getLatitude());
//				String lon = Double.toString(location.getLongitude());
//		        Log.v("LOCATION", lat + " " + lon);
//
////				TextView tv = (TextView) findViewById(R.id.txtLoc);
////				tv.setText("Your Location is:" + lat + "--" + lon);
//			}
// 
//			public void onStatusChanged(String provider, int status, Bundle extras) {}
//			public void onProviderEnabled(String provider) {}
//			public void onProviderDisabled(String provider) {}
//		};
//		// Register the listener with the Location Manager to receive location updates
//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//		
        //        locationManager.getLastKnownLocation(null);
//        LatLng location = 
//        Log.v("LOCATION", getLocation().toString());
		

		return v;
	}
	
	

	private LatLng getLocation()
    {
     // Get the location manager
     LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
     Criteria criteria = new Criteria();
     String bestProvider = locationManager.getBestProvider(criteria, false);
     Location location = locationManager.getLastKnownLocation(bestProvider);
     Double lat,lon;
     try {
       lat = location.getLatitude ();
       lon = location.getLongitude ();
       return new LatLng(lat, lon);
     }
     catch (NullPointerException e){
         e.printStackTrace();
       return null;
     }
    }


	private void loadStops() throws JSONException {
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
//						if (stop_id != 4108734 && stop_id != 4108738 && stop_id != 4108742 
//								&& stop_id != 4114006 && stop_id != 4117706) {
//							map.addMarker(new MarkerOptions()
//							.title(name)
//							.position(location)
//							.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus_yellow_small)));
//						
//						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
//				list = new ArrayList<String>(); 
		        list.clear();
				for (Map.Entry entry : stops.entrySet()) { 
					Stop stop = (Stop) entry.getValue();
					list.add(stop.toString());

				}
//				getActivity().runOnUiThread(new Runnable() {
//
//			        @Override
//			        public void run() {
////			        	listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.cell, list); 
//				        listAdapter.notifyDataSetChanged();
//			        }
//			    });


//                Log.v("stops", stops.toString());
            }
        });
    }
	
	private void loadVehicles() throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL_VEHICLES, new AsyncHttpResponseHandler() {
        
            @Override
            public void onSuccess(String response) {
            	try {
            		Log.v("TRY", "TRYING TO YPATE");
					vehicles = new HashMap<Integer, Vehicle>();
					JSONObject jsonObj = new JSONObject(response);
					JSONObject jsonData = jsonObj.getJSONObject("data");
//					Log.v("ARRAY", jsonData.toString());

					JSONArray oneThirtyTwo = jsonData.getJSONArray("132");
					
//					Log.v("ARRAY", oneThirtyTwo.toString());
					for (int i = 0; i < oneThirtyTwo.length(); i++) {
						JSONObject jsonVehicle = oneThirtyTwo.getJSONObject(i);
//						String name = jsonStop.getString("name");

						JSONObject locationObj = jsonVehicle.getJSONObject("location");
						LatLng location = null;
						if (locationObj != null) {
							location = new LatLng(locationObj.getDouble("lat"),locationObj.getDouble("lng"));
						}

						//arrival EsTIMATES
						JSONArray arrivalEstimates = jsonVehicle.getJSONArray("arrival_estimates");
						
						int stop_id = 0;
						if (arrivalEstimates != null && arrivalEstimates.length() > 0) {
							stop_id = arrivalEstimates.getJSONObject(0).getInt("stop_id");
						}
						int vehicle_id = jsonVehicle.getInt("vehicle_id");
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

					int resourceID = R.drawable.icon_bus_med;

					if (stop_id != 0) {
						Stop stop = stops.get(stop_id);
					
						if (stop.isInboundToStuvii()) {
							resourceID = R.drawable.icon_bus_west;
						} 
					}
						
//					if (vehicleMarkers.containsKey(vehicle_id)) {
//						Marker marker = vehicleMarkers.get(vehicle_id);
//						marker.setPosition(location);
//						marker.setIcon(BitmapDescriptorFactory.fromResource(resourceID));
//					} else {
////						vehicles.put(vehicle_id, new Vehicle(location, vehicle_id, type));
//						Marker marker = map.addMarker(new MarkerOptions()
//						.title(type)
//						.position(location)
//						.icon(BitmapDescriptorFactory.fromResource(resourceID)));
//
//						vehicleMarkers.put(vehicle_id, marker);
//					}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
//                Log.v("stops", vehicles.toString());
            
            }
        });
        
        
    }
	
    private PolylineOptions loadRoute(PolylineOptions line) {
    	line.add(new LatLng(42.35155,-71.11856), new LatLng(42.35369,-71.11809));
    	line.add(new LatLng(42.35247,-71.11549), new LatLng(42.35101,-71.11579));
    	line.add(new LatLng(42.34879,-71.09712), new LatLng(42.34881,-71.09285));
    	line.add(new LatLng(42.34781,-71.09242), new LatLng(42.34678,-71.09229));
    	line.add(new LatLng(42.34627,-71.09094), new LatLng(42.34548,-71.09064));
    	line.add(new LatLng(42.34434,-71.09083), new LatLng(42.34400,-71.09040));
    	line.add(new LatLng(42.34330,-71.08577), new LatLng(42.34043,-71.08167));
    	line.add(new LatLng(42.33912,-71.08036), new LatLng(42.33641,-71.07708));
    	line.add(new LatLng(42.33366,-71.08056), new LatLng(42.33287,-71.08118));
    	line.add(new LatLng(42.33119,-71.07708), new LatLng(42.33345,-71.07347));
    	line.add(new LatLng(42.33593,-71.07002), new LatLng(42.33882,-71.07352));
    	line.add(new LatLng(42.33648,-71.07687), new LatLng(42.33943,-71.08041));
    	line.add(new LatLng(42.34061,-71.08163), new LatLng(42.34286,-71.08504));
    	line.add(new LatLng(42.34345,-71.08579), new LatLng(42.35085,-71.08946));
    	line.add(new LatLng(42.34903,-71.09631), new LatLng(42.34901,-71.09731));
    	line.add(new LatLng(42.35155,-71.11856), new LatLng(42.35155,-71.11856));

    	return line;
    }
}