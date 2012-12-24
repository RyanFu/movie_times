package com.jumplife.sectionlistview;

import java.util.ArrayList;

import com.jumplife.imageload.ImageLoader;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Record;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordListAdapter extends BaseAdapter{
	
	Context mContext;
    private ArrayList<Record> records;
	private ImageLoader imageLoader;
	
	public RecordListAdapter(Context mContext, ArrayList<Record> recordList){
		this.records = recordList;
		this.mContext = mContext;
		imageLoader=new ImageLoader(mContext);
	}

	public int getCount() {
		
		return records.size();
	}

	public Object getItem(int position) {

		return records.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		View converView = myInflater.inflate(R.layout.listview_movieevaluate, null);
		ImageView user_avatar = (ImageView)converView.findViewById(R.id.user_avatar);
		TextView name = (TextView)converView.findViewById(R.id.user_name);
		TextView score = (TextView)converView.findViewById(R.id.user_score);
		TextView user_comment = (TextView)converView.findViewById(R.id.user_comment);

		imageLoader.DisplayImage(records.get(position).getUser().getIconUrl(), user_avatar);		
		name.setText(records.get(position).getUser().getName());
		score.setText("評價 :" + records.get(position).getScoreString());
		user_comment.setText(records.get(position).getComment());
		Log.d("", "position : " + position);
		return converView;
	}
}