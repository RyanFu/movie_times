package com.jumplife.fragment;


import java.util.ArrayList;

import com.jumplife.adapter.TheaterListAdapter;
import com.jumplife.movieinfo.MovieInfoAppliccation;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.TheaterMovieList;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MyFavoriteFragment extends Fragment {
	private View 			 fragmentView;    
    private FragmentActivity mFragmentActivity;
    private AsyncTask<Integer, Integer, String> loadDataTask;
    private FrameLayout flTheaterList;
    private FrameLayout flNoTheaterList;
    private ListView lvTheaterList; 
    private ArrayList<Theater> theaters;
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_like, container, false);
		initView();
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
	
	private void initView() {
		flTheaterList = (FrameLayout)fragmentView.findViewById(R.id.fl_theater_list);
	    lvTheaterList = (ListView)fragmentView.findViewById(R.id.lv_theater_list); 
	    flNoTheaterList= (FrameLayout)fragmentView.findViewById(R.id.fl_no_theater_list);
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
        	
        	setListAdatper();
            setListListener();
            super.onPostExecute(result);
    	}  
    }
	private void fetchData() {
		
		SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(mFragmentActivity);
		SQLiteDatabase db = instance.getReadableDatabase();
		theaters = instance.getTheaterListById(db, MovieInfoAppliccation.shIO.getString("like_theater", ""));
		db.close();
        instance.closeHelper();        
	}
	private void setListAdatper() {
		
		if(theaters.size() > 0)
			lvTheaterList.setAdapter(new TheaterListAdapter(mFragmentActivity, theaters));
		else {
			flTheaterList.setVisibility(View.GONE);
			flNoTheaterList.setVisibility(View.VISIBLE);
		}
			
		
	}
	private void setListListener() {
		lvTheaterList.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) { 
	    	    Intent newAct = new Intent();
    			newAct.putExtra("movielist", 4);
    			newAct.putExtra("theater_id", theaters.get(position).getId());
    	    	newAct.putExtra("theater_name", theaters.get(position).getName());
    	    	newAct.putExtra("theater_buylink", theaters.get(position).getBuyLink());
    	    	newAct.putExtra("theater_phone", theaters.get(position).getPhone());
    	    	newAct.putExtra("theater_location", theaters.get(position).getAddress());
    	    	newAct.setClass( mFragmentActivity, TheaterMovieList.class );
    	    	startActivity( newAct );
		    }  
		});
	}

}
