package com.jumplife.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.adapter.TvListAdapter;
import com.jumplife.fragment.SettingFragment.LoadDataTask;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.TvListActivity;
import com.jumplife.movieinfo.TvScheduleActivity;
import com.jumplife.movieinfo.entity.Channel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class TvtimeFragment extends Fragment {
	private View 			 fragmentView;    
    private FragmentActivity mFragmentActivity;
	
    private ListView tvList;
	private ArrayList<Channel> channels;
	private LoadDataTask loadDataTask;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.activity_theater_list,container, false);        
        findViews();
        loadDataTask = new LoadDataTask();
	        if(Build.VERSION.SDK_INT < 11)
	        	loadDataTask.execute();
	        else
	        	loadDataTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
		
		return fragmentView;
	}
    @Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
    
    private void findViews() {
		tvList = (ListView)fragmentView.findViewById(R.id.listview_theater);		
	}
    private void fetchData() {
		channels = new ArrayList<Channel>(10);
		channels = getChannels();
	}

	private void setListListener() {
		tvList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String channelName = channels.get(position).getName();
				
				EasyTracker.getTracker().trackEvent("頻道名稱", channelName, "", (long)0);
		
				Intent newAct = new Intent();
				newAct.putExtra("tv_id", channels.get(position).getId());
    	    	newAct.putExtra("tv_name", channels.get(position).getName());
    	    	newAct.setClass( mFragmentActivity, TvScheduleActivity.class );
    	    	startActivity( newAct );
			}
		});		
	}

	private void setListAdatper() {
		tvList.setAdapter(new TvListAdapter(mFragmentActivity, channels));
		tvList.setTextFilterEnabled(true);
	}
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
        private ProgressDialog progressdialogInit;

		@Override  
        protected void onPreExecute() {
        	progressdialogInit= ProgressDialog.show(mFragmentActivity, "Load", "Loading…");
            super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            fetchData();
            return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	progressdialogInit.dismiss();
        	if(channels == null){
        		showReloadDialog(mFragmentActivity);
    		}else{
    			setListListener();
    	        setListAdatper();
	            super.onPostExecute(result);
    		}
        }  
          
    }
	public void showReloadDialog(final Context context) {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

		alt_bld.setMessage("是否重新載入資料？").setCancelable(true).setPositiveButton("確定", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				loadDataTask = new LoadDataTask();
				if(Build.VERSION.SDK_INT < 11)
					loadDataTask.execute();
				else
					loadDataTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
				dialog.dismiss();
          }
		});
      
		AlertDialog alert = alt_bld.create();
		alert.setTitle("讀取錯誤");
		alert.show();
	}
	private ArrayList<Channel> getChannels () {
		ArrayList<Channel> channelList = new ArrayList<Channel> (10);
		
		String message = "[{\"channel\":{\"id\":1,\"name\":\"\u885b\u8996\u96fb\u5f71\u53f0\",\"channel_num\":61}}," +
						"{\"channel\":{\"id\":2,\"name\":\"\u6771\u68ee\u96fb\u5f71\u53f0\",\"channel_num\":62}}," +
						"{\"channel\":{\"id\":3,\"name\":\"\u7def\u4f86\u96fb\u5f71\u53f0\",\"channel_num\":63}}," +
						"{\"channel\":{\"id\":4,\"name\":\"\u9f8d\u7965\u6642\u4ee3\u96fb\u5f71\u53f0\",\"channel_num\":64}}," +
						"{\"channel\":{\"id\":5,\"name\":\"HBO\",\"channel_num\":65}}," +
						"{\"channel\":{\"id\":6,\"name\":\"\u6771\u68ee\u6d0b\u7247\u53f0\",\"channel_num\":66}}," +
						"{\"channel\":{\"id\":7,\"name\":\"AXN\",\"channel_num\":67}}," +
						"{\"channel\":{\"id\":8,\"name\":\"\u597d\u840a\u5862\u96fb\u5f71\u53f0\",\"channel_num\":68}}," +
						"{\"channel\":{\"id\":9,\"name\":\"STAR MOVIES\",\"channel_num\":69}}," +
						"{\"channel\":{\"id\":10,\"name\":\"\u7def\u4f86\u80b2\u6a02\u53f0\",\"channel_num\":70}}," +
						"{\"channel\":{\"id\":11,\"name\":\"Cinemax\",\"channel_num\":71}}," +
						"{\"channel\":{\"id\":12,\"name\":\"\u58f9\u96fb\u8996\u96fb\u5f71\u53f0\",\"channel_num\":null}}]";

		
		try {
			JSONArray recordArray = new JSONArray(message);
			for(int i = 0; i < recordArray.length(); i++) {
				JSONObject channelsJson  = recordArray.getJSONObject(i).getJSONObject("channel");
				int num;				
				if(!channelsJson.isNull("channel_num"))
					num = channelsJson.getInt("channel_num");
				else
					num = 0;				
				Channel channel = new Channel(channelsJson.getInt("id"), 
						num, 
						channelsJson.getString("name"));
				channelList.add(channel);				
			}
		} 
		catch (JSONException e){
			e.printStackTrace();
			return null;
		} 
		
		return channelList;
	}
    
}
