package com.jumplife.adapter;

import java.util.ArrayList;

import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Theater;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TheaterListAdapter extends BaseAdapter{
	
	Activity activity;
	ArrayList<Theater> theaters;
	private int mWidth;
	private int mHeight;
	private int padding;
	private int paddingTop;
	private int paddingBottom;
	private int dividerWidth;
	
	public TheaterListAdapter(Activity mActivity, ArrayList<Theater> theaters){
		this.theaters = theaters;
		this.activity = mActivity;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		mWidth = screenWidth;
		mHeight = screenWidth * 3 / 8;
		
		padding = mWidth * 1 / 24 ;
		paddingTop = padding / 3;
		paddingBottom = padding / 2;
		dividerWidth = mWidth * 11 / 12;
	}

	public int getCount() {
		
		return theaters.size();
	}

	public Object getItem(int position) {

		return theaters.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater myInflater = LayoutInflater.from(activity);
		View converView = myInflater.inflate(R.layout.listview_theater, null);
		TextView theater = (TextView)converView.findViewById(R.id.theater_name);
		TextView address = (TextView)converView.findViewById(R.id.theater_address);
		theater.setText(theaters.get(position).getName());
		
		theater.setPadding(padding , paddingTop, 0, 0);
		address.setPadding(padding , 0, 0, paddingBottom);
		View vTheaterDivider = (View)converView.findViewById(R.id.v_theater_divider);
		vTheaterDivider.getLayoutParams().width = dividerWidth;
		vTheaterDivider.setPadding(padding, 0, padding, 0);
		
		if(address != null)
			address.setText(theaters.get(position).getAddress());
		
		return converView;

	}

	

}
