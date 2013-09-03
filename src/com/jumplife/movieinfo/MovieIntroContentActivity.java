package com.jumplife.movieinfo;

import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;

public class MovieIntroContentActivity extends TrackedActivity{
	
	private TextView movieName;
	private TextView content;
	private int movie_id;
	private LoadDataTask taskLoad;
	private Movie movie;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movieintro_content);
        
        movieName = (TextView) findViewById(R.id.textview_moviename);
        content = (TextView) findViewById(R.id.textview_content);
        
        Bundle extras = getIntent().getExtras();
        movie_id = extras.getInt("movie_id");
        
        taskLoad = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	taskLoad.execute();
        else
        	taskLoad.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
        
	}

	private void fetchData() {
		SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(this);
		SQLiteDatabase db = instance.getReadableDatabase();
    	movie = instance.getMovie(db, movie_id);
    	db.close();
    	instance.closeHelper();
	}
	
	private void setViews() {
		movieName.setText(movie.getChineseName());
		content.setText(movie.getInttroduction());
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
        	progressdialogInit= new ProgressDialog(MovieIntroContentActivity.this);
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
        	if(movie==null){
        		showReloadDialog(MovieIntroContentActivity.this);
        	}else{
	        	setViews();
	            
        	}
            super.onPostExecute(result);  
        }  
          
    }
   
   	@Override
	protected void onDestroy(){
		super.onDestroy();
		if (taskLoad!= null && taskLoad.getStatus() != AsyncTask.Status.FINISHED)
			taskLoad.cancel(true);
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
   protected void onResume() {
		super.onResume();
   }
   public void showReloadDialog(final Context context) {
		 AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

	        alt_bld.setMessage("是否重新載入資料？").setCancelable(true).setPositiveButton("確定", new DialogInterface.OnClickListener()
	        {
	            public void onClick(DialogInterface dialog, int id)
	            {
	            	LoadDataTask tast = new LoadDataTask();
	            	if(Build.VERSION.SDK_INT < 11)
	            		tast.execute();
	                else
	                	tast.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
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
