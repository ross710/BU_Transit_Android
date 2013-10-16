package com.rosstard.BUTransit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

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

//	private HashMap<Integer, Vehicle> vehicles;
//	private HashMap<Integer, ArrivalEstimate> estimates;
	private CellAdapter listAdapter;  
	private ArrayList<ListViewObject> list;
	private Context context;
	private Location loc;
	
	private LocationManager mlocManager;
	LocationListener mlocListener;

	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";

	public ListViewFragment(Context context) {
		this.context = context;
	}


	@Override
	public void onResume() {
        handler.post(r);
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, mlocListener);
		super.onResume();
	}
 

 
	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
 

    
    @Override
	public void onPause() {
        handler.removeCallbacks(r);
        mlocManager.removeUpdates(mlocListener);
        super.onPause();
       
        
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_main_list,
				container, false);
		
		mlocListener = new MyLocationListener();
		mlocManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mlocListener);


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
					loadArrivalEstimates();
//					getLocation();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

        
        
        final ListView listView = (ListView) v.findViewById(R.id.listView);

        list = new ArrayList<ListViewObject>(); 

   
        // Create ArrayAdapter using the planet list.  
//        listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.cell, list);  
        listAdapter = new CellAdapter(getActivity(), list);  


        
        listView.setAdapter(listAdapter);

        
        
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
	
	

//	private LatLng getLocation()
//	{
//		// Get the location manager
//		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//		Criteria criteria = new Criteria();
//		String bestProvider = locationManager.getBestProvider(criteria, false);
//		Location location = locationManager.getLastKnownLocation(bestProvider);
//		Double lat,lon;
//		try {
//			lat = location.getLatitude ();
//			lon = location.getLongitude ();
//			Log.v("LOCATION", lat.toString());
//			return new LatLng(lat, lon);
//		}
//		catch (NullPointerException e){
//			e.printStackTrace();
//			return null;
//		}
//	}
//

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
				
				
				
//		        list.clear();
//				for (Map.Entry entry : stops.entrySet()) { 
//					Stop stop = (Stop) entry.getValue();
//					list.add(stop);
//
//				}
				
				
				
				
				
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
	
	private void loadArrivalEstimates() throws JSONException {
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(URL_ARRIVAL_ESTIMATES, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(String response) {

				try {
					//					estimates = new HashMap<Integer, ArrivalEstimate>();
					JSONObject jsonObj = new JSONObject(response);
					JSONArray jsonData = jsonObj.getJSONArray("data");
					//					Log.v("ARRAY", jsonData.toString());


			        list.clear();
			        ArrayList<ListViewObject> tempList = new ArrayList<ListViewObject>();


					for (int i = 0; i < jsonData.length(); i++) {
						JSONObject jsonEstimate = jsonData.getJSONObject(i);
//						Log.v("TEST", jsonEstimate.toString());
						//						String name = jsonStop.getString("name");
						int stop_id = 0;
						int vehicle_id = 0;
						Date arrival_at = null;
						stop_id = jsonEstimate.getInt("stop_id");


						SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

						JSONArray arrivals = jsonEstimate.getJSONArray("arrivals");
						if (arrivals.length() > 0) {
							JSONObject arrival = arrivals.getJSONObject(0);
							vehicle_id = arrival.getInt("vehicle_id");
							String date = arrival.getString("arrival_at");
							try {
								arrival_at = parserSDF.parse(date);
//								Log.v("DATE", arrival_at.toString());

							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
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
						
						Stop stop = stops.get(stop_id);
						String name = stop.getName();
						boolean isInboundToStuvi = stop.isInboundToStuvii();
//						Log.v("HEL", String.valueOf(isInboundToStuvi));

						Date d2 = new Date();
						int mins = (int) getDateDiff(d2,arrival_at, TimeUnit.MINUTES);
						
						ListViewObject lvo = new ListViewObject(name, isInboundToStuvi, type, mins, stop_id);
						
						tempList.add(lvo);
//						Log.v("STR", lvo.toString());
					}

					if (tempList != null && tempList.size() > 0) {
						ArrayList<ListViewObject> newList = getClosestStops(tempList);
						list.clear();
						for (int i = 0; i < newList.size(); i++) {
							list.add(newList.get(i));
//						System.arraycopy(tempList2, 0, list, 0, tempList2.size());
//						list = getClosestStops(tempList);
						}

						listAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//                Log.v("stops", vehicles.toString());


			}
		});

        
    }
	

	
	//Taken from http://stackoverflow.com/questions/1555262/calculating-the-difference-between-two-java-date-instances
	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	/* Class My Location Listener */

	public class MyLocationListener implements LocationListener

	{

		@Override

		public void onLocationChanged(Location loc)

		{
			ListViewFragment.this.updateLocation(loc);


//			String text = "My current location is: " +
//
//					"Latitud = " + loc.getLatitude() +
//
//					"Longitud = " + loc.getLongitude();
//
//			Log.v("LOCATION",text);
//			
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}


	}/* End of Class MyLocationListener */
	
	protected void updateLocation(Location loc) {
		this.loc = loc;
	}
	
	private int getClosestStop(Location loc, List<ListViewObject> newList) {
		int closestIndex = 0;
		double cD = 0;
		for (int i = 0; i < newList.size(); i++) {
			Stop stop = stops.get(newList.get(i).getStop_id());
			double lat = stop.getLat();
			double lng = stop.getLng();
			
			double d = (loc.getLatitude() - lat)*(loc.getLatitude() - lat) + (loc.getLongitude() - lng)*(loc.getLongitude() - lng);
			
			if (cD > d || cD == 0) {
				cD = d;
				closestIndex = i;
			}
			
		}
		
		return closestIndex;
		
	}
	
	protected ArrayList<ListViewObject> getClosestStops (ArrayList<ListViewObject> list) {
//		List<ListViewObject> newList = new ArrayList<ListViewObject>(list);
		if (list != null && list.size() > 0 && loc != null) {

			ArrayList<ListViewObject> finalList = new ArrayList<ListViewObject>();

			int closest = 0;

			//first
			closest = getClosestStop(loc, list);
			finalList.add(list.get(closest));
			list.remove(closest);

			//second
			closest = getClosestStop(loc, list);
			finalList.add(list.get(closest));
			list.remove(closest);

			//third
			closest = getClosestStop(loc, list);
			finalList.add(list.get(closest));
			list.remove(closest);

			//fourth
			closest = getClosestStop(loc, list);
			finalList.add(list.get(closest));
			list.remove(closest);

//			list.clear();
//			list = finalList;
			return finalList;
		}
		return list;
	}
}