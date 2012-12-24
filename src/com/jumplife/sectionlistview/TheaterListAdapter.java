package com.jumplife.sectionlistview;

import java.util.ArrayList;

import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Theater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TheaterListAdapter extends BaseAdapter{
	
	ArrayList<Theater> theaters;
    Context mContext;
	
	public TheaterListAdapter(Context mContext, ArrayList<Theater> theaters){
		this.theaters = theaters;
		this.mContext = mContext;
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
		
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		View converView = myInflater.inflate(R.layout.listview_theater, null);
		TextView theater = (TextView)converView.findViewById(R.id.theater_name);
		TextView address = (TextView)converView.findViewById(R.id.theater_address);
		theater.setText(theaters.get(position).getName());
		if(address != null)
			address.setText(theaters.get(position).getAddress());
		
		return converView;

	}

	

}
