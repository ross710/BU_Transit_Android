package com.rosstard.BUTransit;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CellAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
	private ArrayList<ListViewObject> stops;
	private Context context;
	private boolean noBus;
	
	private final class ViewHolder {
		TextView nameTextView;
		TextView sizeTextView;
		TextView directionTextView;
		
		TextView nextBusTextView;
		TextView minsTextView;
		TextView minutesAwayTextView;



	}

	private ViewHolder mHolder = null;

	public CellAdapter(Context context, ArrayList<ListViewObject> list) {
		this.context = context;
		this.stops = list;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		int size = stops.size();
		if (size == 0) {
			size = 1;
			noBus = true;
		} else {
			noBus = false;
		}
		return size;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			Log.v("CONVERT VIEW", "NULL");
			mHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.cell, null);           
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag(); 
		}

//		mHolder.nameTextView (TextView) convertView.findViewById(R.id.name_textView);
//		mHolder.nameTextView.setText(stops.get(position).getName());
//		mHolder.addressTextView = (TextView)convertView.findViewById(R.id.address_textView);
//		mHolder.addressTextView.setText(peopleList.get(position).getAddress());
		if (!noBus) {
			mHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textView);
			mHolder.nameTextView.setText(stops.get(position).getName());
			mHolder.directionTextView = (TextView)convertView.findViewById(R.id.direction_textView);
			//		
			if (stops.get(position).isInboundToStuvi()) {
				mHolder.directionTextView.setText("(to West Campus)");
				mHolder.nameTextView.setTextColor(Color.RED);
				mHolder.directionTextView.setTextColor(Color.RED);

			} else {
				mHolder.directionTextView.setText("(to East/Med Campus)");
				mHolder.nameTextView.setTextColor(Color.WHITE);
				mHolder.directionTextView.setTextColor(Color.WHITE);
			}

			mHolder.sizeTextView = (TextView)convertView.findViewById(R.id.size_textView);
			mHolder.sizeTextView.setText(stops.get(position).getType());

			mHolder.nextBusTextView = (TextView)convertView.findViewById(R.id.nextBus_textView);
			mHolder.nextBusTextView.setText("next bus is:");		
			mHolder.minsTextView = (TextView)convertView.findViewById(R.id.mins_textView);
			mHolder.minsTextView.setText(Integer.toString(stops.get(position).getMins()));
			mHolder.minutesAwayTextView = (TextView)convertView.findViewById(R.id.minutesAway_textView);
			mHolder.minutesAwayTextView.setText("minutes away");
		} else {
			mHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textView);
			mHolder.nameTextView.setText("Sorry Mate\n\nBuses don't appear to be running at this time but you can check the map to confirm.");
		}
		return convertView;
	}

//	@Override
//	public View getView(int arg0, View arg1, ViewGroup arg2) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}