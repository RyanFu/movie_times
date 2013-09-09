package com.jumplife.adapter;

import java.util.ArrayList;

import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Record;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecordListAdapter extends BaseAdapter{
	
	Context mContext;
    private ArrayList<Record> records;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public RecordListAdapter(Context mContext, ArrayList<Record> recordList){
		this.records = recordList;
		this.mContext = mContext;
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub)
		.showImageForEmptyUri(R.drawable.stub)
		.showImageOnFail(R.drawable.stub)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.cacheOnDisc()
		.cacheInMemory()
		.displayer(new SimpleBitmapDisplayer())
		.build();
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
		RelativeLayout rlItemEvaluate = (RelativeLayout)converView.findViewById(R.id.rl_item_evaluate);
		ImageView user_avatar = (ImageView)converView.findViewById(R.id.user_avatar);
		TextView name = (TextView)converView.findViewById(R.id.user_name);
		TextView score = (TextView)converView.findViewById(R.id.user_score);
		TextView user_comment = (TextView)converView.findViewById(R.id.user_comment);

		PackageManager pm = mContext.getPackageManager();
	    int versionCode;
		try {
			versionCode = pm.getPackageInfo("com.jumplife.moviediary", 0).versionCode;
		} catch (NameNotFoundException e) {
			versionCode = 0;
			e.printStackTrace();
		}
		if(versionCode > 30)
			rlItemEvaluate.setBackgroundResource(R.drawable.item_background_evaluate);
		 else
			 rlItemEvaluate.setBackgroundResource(R.drawable.item_background_normal);
		
		rlItemEvaluate.setPadding(10, 10, 10, 10);
		
		imageLoader.displayImage(records.get(position).getUser().getIconUrl(), user_avatar, options);		
		name.setText(records.get(position).getUser().getName());
		score.setText("評價 :" + records.get(position).getScoreString());
		user_comment.setText(records.get(position).getComment());
		Log.d("", "position : " + position);
		return converView;
	}
}