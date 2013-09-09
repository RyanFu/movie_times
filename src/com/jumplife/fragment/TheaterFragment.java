package com.jumplife.fragment;

import java.util.ArrayList;

import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.TheaterListActivity;
import com.jumplife.movieinfo.entity.Area;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TheaterFragment extends Fragment {
	
	private View 			 fragmentView;    
    private FragmentActivity mFragmentActivity;
	
	private AsyncTask<Integer, Integer, String> loadDataTask;
	ArrayList<Area> areaList;
	int[] area_buttons ={
			R.id.bt_a1,
			R.id.bt_a2,
			R.id.bt_a3,
			R.id.bt_a4,
			R.id.bt_a5,
			R.id.bt_a6,
			R.id.bt_a7,
			R.id.bt_a8,
			R.id.bt_a9,
			R.id.bt_a10,
			R.id.bt_a11,
			R.id.bt_a13,
			R.id.bt_a14,
			R.id.bt_a15,
			R.id.bt_a16,
			R.id.bt_a19,
			R.id.bt_a20,
			R.id.bt_a21,
			R.id.bt_a22,
			R.id.bt_a23,
			R.id.bt_a24,
			R.id.bt_a25,
			R.id.bt_a28
	};
	private String[] mStrings;
	private int displayWidth;
	private int position;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_areas, container, false);
		setViews();
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
	@SuppressWarnings("deprecation")
	private void setViews() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mFragmentActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		displayWidth = displayMetrics.widthPixels;
		displayWidth = displayWidth/3;
		int[] unused_area_buttons ={
				R.id.bt_a12,
				R.id.bt_a17,
				R.id.bt_a18,
				R.id.bt_a26,
				R.id.bt_a27,
				R.id.bt_a29,
				R.id.bt_a30
		};
		for(int i=0;i<unused_area_buttons.length;i++){
			((Button)fragmentView.findViewById(unused_area_buttons[i])).setWidth(displayWidth);
		}
	}
	
	private void setViewListListener(){
		
		for(int i=0;i<area_buttons.length;i++){
			((Button)fragmentView.findViewById(area_buttons[i])).setWidth(displayWidth);
		}
		
		for(int i=0;i<area_buttons.length;i++){
			((Button)fragmentView.findViewById(area_buttons[i])).setText(mStrings[i]);
		}
		for (position=0;position<area_buttons.length;position++){
			((Button)fragmentView.findViewById(area_buttons[position])).setOnClickListener(new OnClickListener(){

				public void onClick(View v) {
					Intent newAct = new Intent();
					for (position=0;position<area_buttons.length;position++){
						if(area_buttons[position]==v.getId())
							break;
					}
	    	    	newAct.putExtra("theaterType", 1);
	    	    	newAct.putExtra("area_id", areaList.get(position).getId());
	    	    	newAct.putExtra("area_name", areaList.get(position).getName());
	    	    	newAct.setClass( mFragmentActivity, TheaterListActivity.class );
	                startActivity( newAct );
					
				}
		      }
			);
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
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
        private ProgressDialog progressdialogInit;
        private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	LoadDataTask.this.cancel(true);
		    	mFragmentActivity.finish();	        	
		    }
    	};
    	
		@Override  
        protected void onPreExecute() {
			progressdialogInit= ProgressDialog.show(mFragmentActivity, "Load", "Loading…");
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
        		showReloadDialog(mFragmentActivity);
    		}else{
    			setViewListListener();
	            super.onPostExecute(result);
    		}
        }  
          
    }
	public void showReloadDialog(final Context context) {
		 AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
	
	       alt_bld.setMessage("是否重新載入資料？").setCancelable(true).setPositiveButton("確定", new DialogInterface.OnClickListener()
	       {
	           @SuppressLint("NewApi")
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
	
	

	
}
