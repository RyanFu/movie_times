package com.jumplife.adapter;

import java.util.ArrayList;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.animation.ViewExpandAnimation;
import com.jumplife.movieinfo.entity.Record;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RecordListAdapter extends BaseAdapter{
	
	Context mContext;
    private ArrayList<Record> records;
    private ArrayList<Integer> mOpenItem;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private class ItemView {
		RelativeLayout rlNormal;
		TextView user_comment;
		TextView tvMore;
		ImageView user_avatar;
		TextView name;
		TextView score;
	}
	
	public RecordListAdapter(Context mContext, ArrayList<Record> recordList){
		this.records = recordList;
		this.mContext = mContext;
		this.mOpenItem = new ArrayList<Integer>(5);
		
		
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
		ItemView itemView = new ItemView();
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		convertView = myInflater.inflate(R.layout.listview_movieevaluate, null);
		
		RelativeLayout rlItemEvaluate = (RelativeLayout)convertView.findViewById(R.id.rl_item_evaluate);
		
		
		itemView.user_avatar = (ImageView)convertView.findViewById(R.id.user_avatar);
		
		itemView.rlNormal = (RelativeLayout)convertView.findViewById(R.id.rl_normal);
		itemView.name = (TextView)convertView.findViewById(R.id.user_name);
		itemView.score = (TextView)convertView.findViewById(R.id.user_score);
		itemView.user_comment = (TextView)convertView.findViewById(R.id.user_comment);
		itemView.tvMore = (TextView)convertView.findViewById(R.id.tv_more);
		
		
		imageLoader.displayImage(records.get(position).getUser().getIconUrl(), itemView.user_avatar, options);		
		
		itemView.name.setText(records.get(position).getUser().getName());
		itemView.score.setText("評價 :" + records.get(position).getScoreString());
		itemView.user_comment.setText(records.get(position).getComment());
		itemView.user_comment.post(new RunnableGetLines(itemView ,position));
		
		itemView.rlNormal.setOnClickListener((new itemMoreClick(itemView ,position)));
		
		
		
		return convertView;
		
	}
	private class RunnableGetLines implements Runnable{
		ItemView itemView;
		int lineCount;
		int pos;
		
		public RunnableGetLines(ItemView itemView , int position) {
			this.itemView = itemView;
			this.pos = position;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
		     lineCount = itemView.user_comment.getLineCount();
		   Log.d("pos:"+pos+"lines :",""+itemView.user_comment.getLineCount());
		   if(lineCount < 3){
			   itemView.tvMore.setVisibility(View.INVISIBLE);
			   itemView.rlNormal.setClickable(false);
		   }else{
			   itemView.tvMore.setVisibility(View.VISIBLE);				  
		   }
		   itemView.user_comment.setLines(2); 
		}
		
	} 
	private class itemMoreClick implements OnClickListener {
		ItemView itemView;
		int pos;
		
		public itemMoreClick(ItemView itemView,int pos) {
			this.itemView = itemView;
			this.pos = pos;
		}
		
		@Override
		public void onClick(View v) {
			itemStatusChanged(pos);
			if(getItemStatus(pos) == false){  
				itemView.tvMore.setVisibility(View.VISIBLE);	
				
				itemView.user_comment.setLines(2); 
		    }else{  
		    	itemView.tvMore.setVisibility(View.INVISIBLE);
		    	itemView.user_comment.setMaxLines(1000000);
		    }
			
		}		
	}
	private void itemStatusChanged(int pos){  
	    for(int i = 0; i < mOpenItem.size(); i ++){  
	        int ipos = mOpenItem.get(i);  
	        if(ipos == pos){  
	        	mOpenItem.remove(i);  
	            return;  
	        }  
	    }
	    Log.d(null, "pos : " + pos);  
	    mOpenItem.add(pos);  
	} 
	
	private boolean getItemStatus(int pos){  
	    for(int i = 0; i < mOpenItem.size(); i ++){  
	        int ipos = mOpenItem.get(i);  
	        if(ipos == pos){  
	            return true;  
	        }  
	    }  
	    return false;  
	}
	
	
}