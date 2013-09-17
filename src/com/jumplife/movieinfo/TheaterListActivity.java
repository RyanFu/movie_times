package com.jumplife.movieinfo;


import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.google.analytics.tracking.android.EasyTracker;

import com.jumplife.ad.AdGenerator;
import com.jumplife.adapter.AreaListAdapter;
import com.jumplife.adapter.TheaterListAdapter;
import com.jumplife.movieinfo.entity.Area;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.movieinfo.promote.PromoteAPP;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TheaterListActivity extends SherlockActivity implements AdWhirlInterface{
	
	private ListView theaterListView;
	private ArrayList<Area> areaList;
	private ArrayList<Theater> theaters;
		
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
       
        Bundle extras = getIntent().getExtras();
        area = new Area(extras.getInt("area_id"),extras.getString("area_name"));
		type_dim = TYPE.THEATER;
        
		findViews();
        
        AdTask task = new AdTask();
        task.execute();
    }
	
	private void fetchData() {
		
		SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(this);
		SQLiteDatabase db = instance.getReadableDatabase();
		theaters = instance.getTheaterList(db, area.getId());
		db.close();
        instance.closeHelper();
		
	}

	private void setListListener() {
		theaterListView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) { 
		    	Intent newAct = new Intent();
    			newAct.putExtra("movielist", 4);
    			newAct.putExtra("theater_id", theaters.get(position).getId());
    	    	newAct.putExtra("theater_name", theaters.get(position).getName());
    	    	newAct.putExtra("theater_buylink", theaters.get(position).getBuyLink());
    	    	newAct.putExtra("theater_phone", theaters.get(position).getPhone());
    	    	newAct.putExtra("theater_location", theaters.get(position).getAddress());
    	    	newAct.setClass( TheaterListActivity.this, TheaterMovieList.class );       
	    	    startActivity( newAct );
		    }  
		});
		
	}

	private void setListAdatper() {
			
		if(theaters.size() > 0)
			theaterListView.setAdapter(new TheaterListAdapter(TheaterListActivity.this, theaters));
		else 
			theaterListView.setVisibility(View.GONE);
		
		theaterListView.setTextFilterEnabled(true);
	}

	private void findViews() {
		getSupportActionBar().setIcon(R.drawable.movietime);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
	
		getSupportActionBar().setTitle(area.getName());
		
		
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
		 Bundle extras = getIntent().getExtras();
         area = new Area(extras.getInt("area_id"),extras.getString("area_name"));
		 type_dim = TYPE.THEATER;
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
    
    

	public void adWhirlGeneric()
	{
		// TODO Auto-generated method stub
		
	}
	
}
