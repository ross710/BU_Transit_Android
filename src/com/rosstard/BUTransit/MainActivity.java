package com.rosstard.BUTransit;

import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
	 
    // Google Map
    private GoogleMap map;
    private final String URL_STOPS = "http://api.transloc.com/1.2/stops.json?agencies=bu";
	private final String URL_VEHICLES = "http://api.transloc.com/1.2/vehicles.json?agencies=bu";
	private final String URL_ARRIVAL_ESTIMATES = "http://api.transloc.com/1.2/arrival-estimates.json?agencies=bu";
	private final String URL_ROUTES = "http://api.transloc.com/1.2/routes.json?agencies=bu";

	HashMap<Integer, Stop> stops;
	HashMap<Integer, Stop> vehicles;


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
            	
				
                Log.v("stops", stops.toString());
            }
        });
    }
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        try {
            // Loading map
            initilizeMap();
 
        } catch (Exception e) {
            e.printStackTrace();
        }

        LatLngBounds BU_Location = new LatLngBounds(new LatLng(42.34091, -71.09682), new LatLng(42.34091, -71.09682));

        // Set the camera to the greatest possible zoom level that includes the
        // bounds
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BU_Location.getCenter(), 13)); 
        PolylineOptions line = new PolylineOptions();
        line = loadRoute(line);
        line.width(3);
        line.color(Color.BLUE);
        
        
        map.addPolyline(line);
        
//        map.addMarker(new MarkerOptions()
//        .position(new LatLng(42.35155, -71.11856))
//        .title("Hello world")
//        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_bus_med)));
        
//        BackEndWrapper bew = new BackEndWrapper();
//
//        try {
//			bew.loadStops();
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        try {
			loadStops();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
        
//        Log.v("WHY", "WHY U NO WORK");
//        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        
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
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (map == null) {
            map = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
 
            // check if map is created successfully or not
            if (map == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }
}
 
//public class MainActivity extends FragmentActivity implements
//		ActionBar.TabListener {
//
//	/**
//	 * The {@link android.support.v4.view.PagerAdapter} that will provide
//	 * fragments for each of the sections. We use a
//	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
//	 * will keep every loaded fragment in memory. If this becomes too memory
//	 * intensive, it may be best to switch to a
//	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
//	 */
//	SectionsPagerAdapter mSectionsPagerAdapter;
//
//	/**
//	 * The {@link ViewPager} that will host the section contents.
//	 */
//	ViewPager mViewPager;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//
//	    // Zoom in, animating the camera.
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//
//		// Set up the action bar.
//		final ActionBar actionBar = getActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//
//		// Create the adapter that will return a fragment for each of the three
//		// primary sections of the app.
//		mSectionsPagerAdapter = new SectionsPagerAdapter(
//				getSupportFragmentManager());
//
//		// Set up the ViewPager with the sections adapter.
//		mViewPager = (ViewPager) findViewById(R.id.pager);
//		mViewPager.setAdapter(mSectionsPagerAdapter);
//
//		// When swiping between different sections, select the corresponding
//		// tab. We can also use ActionBar.Tab#select() to do this if we have
//		// a reference to the Tab.
//		mViewPager
//				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//					@Override
//					public void onPageSelected(int position) {
//						actionBar.setSelectedNavigationItem(position);
//					}
//				});
//
//		// For each of the sections in the app, add a tab to the action bar.
//		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//			// Create a tab with text corresponding to the page title defined by
//			// the adapter. Also specify this Activity object, which implements
//			// the TabListener interface, as the callback (listener) for when
//			// this tab is selected.
//			actionBar.addTab(actionBar.newTab()
//					.setText(mSectionsPagerAdapter.getPageTitle(i))
//					.setTabListener(this));
//		}
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public void onTabSelected(ActionBar.Tab tab,
//			FragmentTransaction fragmentTransaction) {
//		// When the given tab is selected, switch to the corresponding page in
//		// the ViewPager.
//		mViewPager.setCurrentItem(tab.getPosition());
//	}
//
//	@Override
//	public void onTabUnselected(ActionBar.Tab tab,
//			FragmentTransaction fragmentTransaction) {
//	}
//
//	@Override
//	public void onTabReselected(ActionBar.Tab tab,
//			FragmentTransaction fragmentTransaction) {
//	}
//
//	/**
//	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
//	 * one of the sections/tabs/pages.
//	 */
//	public class SectionsPagerAdapter extends FragmentPagerAdapter {
//
//		public SectionsPagerAdapter(FragmentManager fm) {
//			super(fm);
//		}
//
//		@Override
//		public Fragment getItem(int position) {
//			// getItem is called to instantiate the fragment for the given page.
//			// Return a DummySectionFragment (defined as a static inner class
//			// below) with the page number as its lone argument.
//			Fragment fragment = new DummySectionFragment();
//			Bundle args = new Bundle();
//			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
////			args.putString("Name", "Test");
//			fragment.setArguments(args);
//			return fragment;
//		}
//
//		@Override
//		public int getCount() {
//			// Show 3 total pages.
//			return 2;
//		}
//
//		@Override
//		public CharSequence getPageTitle(int position) {
//			Locale l = Locale.getDefault();
//			switch (position) {
//			case 0:
//				return "List View";
////				return getString(R.string.title_section1).toUpperCase(l);
//			case 1:
//				return "Map View";
////				return getString(R.string.title_section2).toUpperCase(l);
//			}
//			return null;
//		}
//	}
//
//	/**
//	 * A dummy fragment representing a section of the app, but that simply
//	 * displays dummy text.
//	 */
//	public static class DummySectionFragment extends Fragment {
//		/**
//		 * The fragment argument representing the section number for this
//		 * fragment.
//		 */
//		public static final String ARG_SECTION_NUMBER = "section_number";
//
//		public DummySectionFragment() {
//		}
//
//		@Override
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
//					container, false);
//			TextView dummyTextView = (TextView) rootView
//					.findViewById(R.id.section_label);
//			dummyTextView.setText(Integer.toString(getArguments().getInt(
//					ARG_SECTION_NUMBER)));
//			return rootView;
//		}
//	}
//
//}
