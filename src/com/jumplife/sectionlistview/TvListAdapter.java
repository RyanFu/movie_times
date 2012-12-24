package com.jumplife.sectionlistview;

import java.util.ArrayList;

import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Channel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TvListAdapter extends BaseAdapter{
	
	private ArrayList<Channel> channels;
    Context mContext;
	
	public TvListAdapter(Context mContext, ArrayList<Channel> channels){
		this.channels = channels;
		this.mContext = mContext;
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
		
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		View converView = myInflater.inflate(R.layout.listview_tv, null);
		TextView theater = (TextView)converView.findViewById(R.id.theater_name);
		theater.setText(channels.get(position).getName());
		
		return converView;

	}
}
