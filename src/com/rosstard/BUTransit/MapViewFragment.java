package com.rosstard.BUTransit;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Interpolator;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */

//Help from: https://gist.github.com/joshdholtz/4522551
public class MapViewFragment extends Fragment {
	// Google Map
    private GoogleMap map;
    private MapView mapView;
    private final String URL_STOPS = "http://api.transloc.com/1.2/stops.json?agencies=bu";
	private final String URL_VEHICLES = "http://api.transloc.com/1.2/vehicles.json?agencies=bu";
	private final String URL_ARRIVAL_ESTIMATES = "http://api.transloc.com/1.2/arrival-estimates.json?agencies=bu";
	private final String URL_ROUTES = "http://api.transloc.com/1.2/routes.json?agencies=bu";

    private final Handler handler = new Handler();
    private Runnable r;
    
	HashMap<Integer, Stop> stops;
	HashMap<Integer, Vehicle> vehicles;
	HashMap<Integer, Marker> vehicleMarkers = new HashMap<Integer, Marker>();
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	public MapViewFragment() {
	}

	
	@Override
	public void onResume() {
		mapView.onResume();
        handler.post(r);

		super.onResume();
	}
 
	@Override
	public void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}
	
 

    
    @Override
	public void onPause() {
        handler.removeCallbacks(r);
        mapView.onPause();
        super.onPause();
       
        
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main_map,
				container, false);
		
		mapView = (MapView) v.findViewById(R.id.mapview);
		mapView.onCreate(savedInstanceState);

		// Gets to GoogleMap from the MapView and does initialization stuff
		map = mapView.getMap();
		map.getUiSettings().setMyLocationButtonEnabled(false);
		map.setMyLocationEnabled(true);

		// Needs to call MapsInitializer before doing any CameraUpdateFactory calls
		try {
			MapsInitializer.initialize(this.getActivity());
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}

        resetView();
//        LatLngBounds BU_Location = new LatLngBounds(new LatLng(42.34091, -71.09682), new LatLng(42.34091, -71.09682));
//
//        // Set the camera to the greatest possible zoom level that includes the
//        // bounds
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BU_Location.getCenter(), 13)); 
        
        PolylineOptions line = new PolylineOptions();
        line = loadRoute(line);
        line.width(3);
        line.color(Color.BLUE);
        map.addPolyline(line);
        
        
        try {
			loadStops();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        r = new Runnable() {
            @Override
            public void run() {
                try {
        			loadVehicles();
        		} catch (JSONException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
                handler.postDelayed(this, 5000);
            }
        };
        
        handler.post(r);
		
		return v;
	}
	
	


	public void resetView() {
        LatLngBounds BU_Location = new LatLngBounds(new LatLng(42.34091, -71.09682), new LatLng(42.34091, -71.09682));

        // Set the camera to the greatest possible zoom level that includes the
        // bounds
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BU_Location.getCenter(), 13)); 
	}

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
						if (stop_id != 4108734 && stop_id != 4108738 && stop_id != 4108742 
								&& stop_id != 4114006 && stop_id != 4117706) {
							map.addMarker(new MarkerOptions()
							.title(name)
							.position(location)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus_yellow_small)));
						
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
				
//                Log.v("stops", stops.toString());
            }
        });
    }
	
	public void loadVehicles() throws JSONException {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL_VEHICLES, new AsyncHttpResponseHandler() {
        
            @Override
            public void onSuccess(String response) {
            	try {
            		Log.v("Update", "Trying To Update");
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
						
					if (vehicleMarkers.containsKey(vehicle_id)) {
						Marker marker = vehicleMarkers.get(vehicle_id);
						marker.setIcon(BitmapDescriptorFactory.fromResource(resourceID));

						animateMarker(marker, location, false);
//						marker.setPosition(location);
					} else {
//						vehicles.put(vehicle_id, new Vehicle(location, vehicle_id, type));
						Marker marker = map.addMarker(new MarkerOptions()
						.title(type)
						.position(location)
						.icon(BitmapDescriptorFactory.fromResource(resourceID)));

						vehicleMarkers.put(vehicle_id, marker);
					}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
//                Log.v("stops", vehicles.toString());
            
            }
        });
        
        
    }
	
	//taken from http://stackoverflow.com/questions/13728041/move-markers-in-google-map-v2-android
    public void animateMarker(final Marker marker, final LatLng toPosition,
            final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
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