package com.jumplife.adapter;

import java.util.ArrayList;

import com.jumplife.movieinfo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TvScheduleListAdapter extends BaseAdapter{
	
	 ArrayList<com.jumplife.movieinfo.TvScheduleActivity.tvSchedule> tvSchedule;
    Context mContext;
	
	public TvScheduleListAdapter(Context mContext,  ArrayList<com.jumplife.movieinfo.TvScheduleActivity.tvSchedule> tvSchedule){
		this.tvSchedule = tvSchedule;
		this.mContext = mContext;
	}

	public int getCount() {
		
		return tvSchedule.size();
	}

	public Object getItem(int position) {

		return tvSchedule.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		View converView = myInflater.inflate(R.layout.listview_tv_schedule, null);
		TextView tvName = (TextView)converView.findViewById(R.id.tv_name);
		TextView tvTime = (TextView)converView.findViewById(R.id.tv_time);
		tvName.setText(tvSchedule.get(position).name);
		tvTime.setText(tvSchedule.get(position).time);
		
		return converView;

	}

	

}
