package com.jumplife.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.movieinfo.EzCheckActivity;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Theater;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter{
	ArrayList<Theater> theaters;
    Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public ScheduleAdapter(Context mContext, ArrayList<Theater> theaterList){
		this.theaters = theaterList;
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
		
		return theaters.size();
	}

	public Object getItem(int position) {

		return theaters.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		View converView = myInflater.inflate(R.layout.listview_schedule, null);
		
		if(theaters.size() != 0) {
			TextView theaterName = (TextView)converView.findViewById(R.id.textview_theatername);
			TextView hall = (TextView)converView.findViewById(R.id.textview_hall);
			Button buttonBooking = (Button)converView.findViewById(R.id.button_booking);
			LinearLayout llSchedule = (LinearLayout)converView.findViewById(R.id.linearlayout_schedule);
			ImageView imageviewTag1 = (ImageView)converView.findViewById(R.id.imageview_tag1);
			ImageView imageviewTag2 = (ImageView)converView.findViewById(R.id.imageview_tag2);
			
			final Theater theater = theaters.get(position);
			theaterName.setText(theater.getName());
			
			if(theater.getBuyLink() != null) {
				final String url = theater.getBuyLink();
				buttonBooking.setVisibility(View.VISIBLE);
				buttonBooking.setOnClickListener(new OnClickListener(){
					public void onClick(View arg0) {
						HashMap<String, String> parameters = new HashMap<String, String>();
						parameters.put("THEATER", theater.getName());
						parameters.put("LINK", url);
						
						if(theater.getBuyLink().contains("http://www.ezding.com.tw/jumplife")) {
							EasyTracker.getTracker().trackEvent("時刻表", "訂票", "EZ訂", (long)0);
							Intent newAct = new Intent();
			                newAct.setClass(mContext, EzCheckActivity.class);
			                mContext.startActivity(newAct);
						} else {
							EasyTracker.getTracker().trackEvent("時刻表", "訂票", url, (long)0);
							Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
							mContext.startActivity(intent);
						}
					}				
				});
			} else
				buttonBooking.setVisibility(View.GONE);
			
			if(theater.getHall()!= null && !theater.getHall().equals("")) {
				hall.setText(theater.getHall());
				hall.setVisibility(View.VISIBLE);
			} else
				hall.setVisibility(View.GONE);
			
			String[] timeTable=theater.getTimeTable().split("\\|") ;
			for(int i=0; i<timeTable.length; i+=3){
				TableRow Schedule_row = new TableRow(mContext);
				for(int j=0; j<3; j++){
					int index = i + j;				
					TextView tv = new TextView(mContext);
					if(index < timeTable.length) 
						tv.setText(timeTable[index]);
					
					tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					tv.setTextColor(mContext.getResources().getColor(R.color.textcolor_grey));
					tv.setGravity(Gravity.CENTER);
					TableRow.LayoutParams Params = new TableRow.LayoutParams
							(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 0.33f);
					Params.setMargins(3, 3, 3, 3);
					tv.setLayoutParams(Params);
					Schedule_row.addView(tv);
				}
				Schedule_row.setLayoutParams(new LayoutParams
						(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				llSchedule.addView(Schedule_row);
			}
			
			if(theater.getHallType() != null) {
				Log.d(null, "theater hall type : " + theater.getHallType());
				String[] tags = theater.getHallType().split("\\*\\*\\*") ;
				Log.d(null, "tags : " + tags[0]);
				if(tags.length == 1) {
					imageLoader.displayImage(tags[0], imageviewTag1, options);
					imageviewTag1.setVisibility(View.VISIBLE);
					imageviewTag2.setVisibility(View.GONE);
				} else if (tags.length == 2) {
					imageLoader.displayImage(tags[0], imageviewTag1, options);
					imageLoader.displayImage(tags[1], imageviewTag2, options);
					imageviewTag1.setVisibility(View.VISIBLE);
					imageviewTag2.setVisibility(View.VISIBLE);
				}					
			} else {
				imageviewTag1.setVisibility(View.GONE);
				imageviewTag2.setVisibility(View.GONE);
			}
			
		} else
			converView.setVisibility(View.GONE);


		return converView;
	}	
}
