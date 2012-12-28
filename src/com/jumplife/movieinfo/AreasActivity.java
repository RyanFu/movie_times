package com.jumplife.movieinfo;

import java.util.ArrayList;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.movieinfo.entity.Area;
import com.jumplife.movieinfo.promote.PromoteAPP;

public class AreasActivity extends TrackedActivity{
	
	private AsyncTask<Integer, Integer, String> loadDataTask;
	ArrayList<Area> areaList;
	int[] area_buttons ={
			R.id.button1,
			R.id.button2,
			R.id.button3,
			R.id.button4,
			R.id.button5,
			R.id.button6,
			R.id.button7,
			R.id.button8,
			R.id.button9,
			R.id.button10,
			R.id.button11,
			R.id.button13,
			R.id.button14,
			R.id.button15,
			R.id.button16,
			R.id.button19,
			R.id.button20,
			R.id.button21,
			R.id.button22,
			R.id.button23,
			R.id.button24,
			R.id.button25
	};
	private String[] mStrings;
	private int displayWidth;
	private int position;
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas);        
        setViews();
        loadDataTask = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
			loadDataTask.execute();
        else
        	loadDataTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
    }

	@SuppressWarnings("deprecation")
	private void setViews() {
		WindowManager mWinMgr = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		displayWidth = mWinMgr.getDefaultDisplay().getWidth();
		displayWidth = displayWidth/3;
		int[] unused_area_buttons ={
				R.id.button12,
				R.id.button17,
				R.id.button18,
				R.id.button26,
				R.id.button27,
				R.id.button28,
				R.id.button29,
				R.id.button30
		};
		for(int i=0;i<unused_area_buttons.length;i++){
			((Button)findViewById(unused_area_buttons[i])).setWidth(displayWidth);
		}
	}
	
	private void fetchData(){
		areaList = Area.getAreaList();
		if(areaList!=null){
			mStrings = new String[areaList.size()];
			for(int i=0; i < mStrings.length ;i++)
				mStrings[i] = areaList.get(i).getName();
		}
	}
	
	private void setViewListListener(){
		
		for(int i=0;i<area_buttons.length;i++){
			((Button)findViewById(area_buttons[i])).setWidth(displayWidth);
		}
		
		for(int i=0;i<area_buttons.length;i++){
			((Button)findViewById(area_buttons[i])).setText(mStrings[i]);
		}
		for (position=0;position<area_buttons.length;position++){
			((Button)findViewById(area_buttons[position])).setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					Intent newAct = new Intent();
					for (position=0;position<area_buttons.length;position++){
						if(area_buttons[position]==v.getId())
							break;
					}
	    	    	newAct.putExtra("theaterType", 1);
	    	    	newAct.putExtra("area_id", areaList.get(position).getId());
	    	    	newAct.putExtra("area_name", areaList.get(position).getName());
	    	    	newAct.setClass( AreasActivity.this, TheaterListActivity.class );
	                startActivity( newAct );
					
				}
		      }
			);
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
			progressdialogInit= ProgressDialog.show(AreasActivity.this, "Load", "Loading…");
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
        	if(areaList==null){
        		showReloadDialog(AreasActivity.this);
    		}else{
    			setViewListListener();
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
			
			PromoteAPP promoteAPP = new PromoteAPP(AreasActivity.this);
        	if(!promoteAPP.isPromote) {
				new AlertDialog.Builder(this)
					.setTitle("- 離開程式? -")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {
							AreasActivity.this.finish();
						}
					})
					.setNegativeButton("否", null)
					.show();
        	} else
		    	promoteAPP.promoteAPPExe();
			
						
			return true;
	    }		
		
		return super.onKeyDown(keyCode, event);
	}

}
