package com.jumplife.adapter;

import java.util.ArrayList;

import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Channel;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TvListAdapter extends BaseAdapter{
	
	private ArrayList<Channel> channels;
	Activity activity;
    private int mWidth;
	private int mHeight;
	private int padding;
	private int paddingTop;
	private int dividerWidth;
	
	public TvListAdapter(Activity mActivity, ArrayList<Channel> channels){
		this.channels = channels;
		this.activity = mActivity;
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		mWidth = screenWidth;
		mHeight = screenWidth * 3 / 8;
		
		
		padding = mWidth * 1 / 24 ;
		paddingTop = padding / 2;
		dividerWidth = mWidth * 11 / 12;
	}

	public int getCount() {		
		return channels.size();
	}

	public Object getItem(int position) {
		return channels.get(position);
	}

	public long getItemId(int position) {	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater myInflater = LayoutInflater.from(activity);
		View converView = myInflater.inflate(R.layout.listview_tv, null);
		TextView theater = (TextView)converView.findViewById(R.id.theater_name);
		theater.setText(channels.get(position).getName());
		theater.setPadding(padding ,paddingTop, 0, paddingTop);
		
		View vTvDivider = (View)converView.findViewById(R.id.v_tv_divider);
		vTvDivider.getLayoutParams().width = dividerWidth;
		vTvDivider.setPadding(padding, 0, padding, 0);
		return converView;

	}
}
