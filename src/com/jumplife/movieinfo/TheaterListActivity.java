package com.jumplife.movieinfo;


import java.util.ArrayList;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedActivity;

import com.jumplife.ad.AdGenerator;
import com.jumplife.movieinfo.entity.Area;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.sectionlistview.AreaListAdapter;
import com.jumplife.sectionlistview.TheaterListAdapter;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import com.jumplife.sqlite.SQLiteMovieDiary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TheaterListActivity extends TrackedActivity implements AdWhirlInterface{
	
	private ListView theaterListView;
	private ArrayList<Area> areaList;
	private ArrayList<Theater> theaters;
	private static String[] mStrings;
	
	private AdView adView;
	private SharePreferenceIO shIO;
	
	public enum TYPE
    {
        AREA,THEATER
    };
    
    TYPE type_dim = TYPE.AREA;
    
	private Area area;
	private LoadDataTask loadDataTask;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater_list);        
        getType();
        findViews();
        
        AdTask task = new AdTask();
        task.execute();
    }
	
	private void getType() {
		
		try{
        	Bundle extras = getIntent().getExtras();
        	int type = extras.getInt("theaterType");
        	if(type == 0)
        		type_dim = TYPE.AREA;
        	else{
        		area = new Area(extras.getInt("area_id"),extras.getString("area_name"));
        		type_dim = TYPE.THEATER;
        	}
        }catch(Exception e){
        	
        }  
		
	}

	private void fetchData() {
		if(type_dim == TYPE.AREA){
			areaList = Area.getAreaList();
			if(areaList!=null){
				mStrings = new String[areaList.size()];
				for(int i=0; i < mStrings.length ;i++)
					mStrings[i] = areaList.get(i).getName();
			}
		}else{
			SQLiteMovieDiary sqlMovieDiary = new SQLiteMovieDiary(this);
			if(area.getId() != -1)
				theaters = sqlMovieDiary.getTheaterList(area.getId());
			else {
				shIO = new SharePreferenceIO(TheaterListActivity.this);
				theaters = sqlMovieDiary.getTheaterListById(shIO.SharePreferenceO("like_theater", ""));
			}
		}
		
	}

	private void setListListener() {
		theaterListView.setOnItemClickListener(new OnItemClickListener() {  

		      public void onItemClick(AdapterView<?> parent, View view, int position,  
		        long id) { 
		    	    
		    	    if (type_dim == TYPE.AREA){
		    	    	Intent newAct = new Intent();
		    	    	newAct.putExtra("theaterType", 1);
		    	    	newAct.putExtra("area_id", areaList.get(position).getId());
		    	    	newAct.putExtra("area_name", areaList.get(position).getName());
		    	    	newAct.setClass( TheaterListActivity.this, TheaterListActivity.class );
		                
		    	    	EasyTracker.getTracker().trackEvent("戲院地區", areaList.get(position).getName(), "", (long)0);
		    	    	
		    	    	startActivity( newAct );
		    	    }
		    		else{
		    			Intent newAct = new Intent();
		    			newAct.putExtra("movielist", 4);
		    			newAct.putExtra("theater_id", theaters.get(position).getId());
		    	    	newAct.putExtra("theater_name", theaters.get(position).getName());
		    	    	newAct.putExtra("theater_buylink", theaters.get(position).getBuyLink());
		    	    	newAct.putExtra("theater_phone", theaters.get(position).getPhone());
		    	    	newAct.putExtra("theater_location", theaters.get(position).getAddress());
		    	    	newAct.setClass( TheaterListActivity.this, MovieList.class );
		                
		    	    	EasyTracker.getTracker().trackEvent("戲院名稱", theaters.get(position).getName(), "", (long)0);
	
		    	    	startActivity( newAct );
		    		}
		        }  
		       
		  });
		
	}

	private void setListAdatper() {
		if (type_dim == TYPE.AREA)
			theaterListView.setAdapter(new AreaListAdapter(TheaterListActivity.this, mStrings));
		else {			
			if(theaters.size() > 0)
				theaterListView.setAdapter(new TheaterListAdapter(TheaterListActivity.this, theaters));
			else 
				theaterListView.setVisibility(View.GONE);
		}
		
		theaterListView.setTextFilterEnabled(true);
	}

	private void findViews() {
		LinearLayout topbar = (LinearLayout)findViewById(R.id.topbar);
		TextView topbar_text = (TextView)findViewById(R.id.topbar_text);
		if (type_dim == TYPE.AREA)
			topbar.setVisibility(View.GONE);
		else {
			topbar.setVisibility(View.VISIBLE);
			topbar_text.setText(area.getName());
		}
		
		theaterListView = (ListView)findViewById(R.id.listview_theater);
		
	}
	
	class AdTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			
			return null;
		}
		
		 @Override  
	     protected void onPostExecute(String result) {
			 //setAd();
			 AdGenerator adGenerator = new AdGenerator(TheaterListActivity.this);
			//adGenerator.setAdmobAd();
			 adGenerator.setAdwhirlAd();
			 super.onPostExecute(result);
		 }
    }
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
        private ProgressDialog progressdialogInit;
        private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	LoadDataTask.this.cancel(true);
		    	finish();	        	
		    }
    	};
    	
		@Override  
        protected void onPreExecute() {
			progressdialogInit= ProgressDialog.show(TheaterListActivity.this, "Load", "Loading…");
            progressdialogInit.setTitle("Load");
        	progressdialogInit.setMessage("Loading…");
        	progressdialogInit.setOnCancelListener(cancelListener);
        	progressdialogInit.setCanceledOnTouchOutside(false);
        	progressdialogInit.setCancelable(true);
        	progressdialogInit.show();
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
        	if(type_dim == TYPE.AREA && areaList==null){
        		showReloadDialog(TheaterListActivity.this);
    		}else if(type_dim == TYPE.THEATER && theaters==null){
    			showReloadDialog(TheaterListActivity.this);
    		}else{
	        	setListAdatper();
	            setListListener();
	            super.onPostExecute(result);
    		}
        }  
          
    }
	
	@Override
    protected void onStart() {
	    super.onStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
	protected void onResume()
	{
        loadDataTask = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	loadDataTask.execute();
        else
        	loadDataTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
        
        super.onResume();
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
	        // Title for AlertDialog
	        alert.setTitle("讀取錯誤");
	        // Icon for AlertDialog
	        //alert.setIcon(R.drawable.gnome_logout);
	        alert.show();
		
	}
    
    @Override
    protected void onDestroy(){
    	if (loadDataTask != null && loadDataTask.getStatus() != AsyncTask.Status.FINISHED)
    		loadDataTask.cancel(true);
    	
    	super.onDestroy();
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN){
			
			if(type_dim == TYPE.AREA) {
				new AlertDialog.Builder(this)
					.setTitle("- 離開程式? -")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {
							TheaterListActivity.this.finish();
						}
					})
					.setNegativeButton("否", null)
					.show();
			}else
				finish();
						
			return true;
	    }		
		
		return super.onKeyDown(keyCode, event);
	}

	public void adWhirlGeneric()
	{
		// TODO Auto-generated method stub
		
	}
}
