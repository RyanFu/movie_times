package com.jumplife.movieinfo;


import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.imageload.ImageLoader;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.Record;
import com.jumplife.sqlite.SQLiteMovieDiary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MovieInfoActivity extends TrackedActivity {
	
	//private TextView topbar_text;
	private ImageView poster;
	private TextView chinese_name;
	private TextView english_name;
	private ImageView level;
	private TextView runningtime;
	private Button play_btn;
	private AdView adView;
	//private Button schedule_btn;
	
	private TextView textView_time;
	//private TextView textView_drama;
	private TextView textView_director;
	private TextView textView_actor;
	private ImageLoader imageLoader;
	private int movie_id;
	private Movie movie;
	private LoadDataTask taskLoad;
	
	private static String TAG = "MovieInfoActivity";
	public static ArrayList<Record> records;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movieinfo);
        Bundle extras = getIntent().getExtras();
        movie_id = extras.getInt("movie_id");
        extras.getInt("theater_id");
        imageLoader = new ImageLoader(MovieInfoActivity.this);
        findViews();
        
        taskLoad = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	taskLoad.execute();
        else
        	taskLoad.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
    }

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
    }
	
	public void setAd() {
    	// Create the adView
    	Resources res = getResources();
    	String admobKey = res.getString(R.string.admob_key);

        adView = new AdView(this, AdSize.BANNER, admobKey);

        // Lookup your LinearLayout assuming it's been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout)findViewById(R.id.ad_linearlayout);

        // Add the adView to it
        layout.addView(adView);

        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
    }
	
	private void setViews() {
        //topbar_text.setText("電影資訊");
		imageLoader.DisplayImage(movie.getPosterUrl(), poster);
		chinese_name.setText(movie.getChineseName());
		english_name.setText(movie.getEnglishName());
		
		if(movie.getLevelUrl() == null || movie.getLevelUrl().equals("null"))
			level.setVisibility(View.INVISIBLE);
		else {
			imageLoader.DisplayImage(movie.getLevelUrl(), level);
			level.setVisibility(View.VISIBLE);
		}
			
		if(movie.getRunningTime() > 0)
			runningtime.setText("片長 : " + movie.getRunningTime() + "分");
		else
			runningtime.setText("片長 : 尚未提供");
		
		DateFormat createFormatter = new SimpleDateFormat("yyyy/MM/dd");
		if(movie.getReleaseDate() != null)
			textView_time.setText(createFormatter.format(movie.getReleaseDate()));
		else
			textView_time.setText("未提供上映日期");
		
		Log.d(TAG, "" + movie.getYoutubeVideoUrl());
		if(movie.getYoutubeId() == null || movie.getYoutubeId().equals("null")) {
			play_btn.setText(" 尚未提供");
			play_btn.setClickable(false);
		} else {
			play_btn.setText(" 預告片");
			play_btn.setClickable(true);
		}
        
		String directors = "";
		for (String s : movie.getDirectors())
		{
			if(s != movie.getDirectors().get(movie.getDirectors().size()-1))
				directors += s + ", ";
			else
				directors += s;
		}
		
		String actors = "";
		for (String s : movie.getActors())
		{
			if(s != movie.getActors().get(movie.getActors().size()-1))
				actors += s + ", ";
			else
				actors += s;
		}

		textView_director.setText(directors);
		textView_actor.setText(actors);
		
		records = movie.getRecordList();
	}

	private void setListener() {
		play_btn.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		EasyTracker.getTracker().trackEvent("電影簡介", "預告片", "", (long)0);
	    		if(movie.getYoutubeId() != null && !movie.getYoutubeId().equals("null"))
	    			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getYoutubeVideoUrl())));
			}
		});
	}
	
	private void findViews() {
		 //topbar_text = (TextView)findViewById(R.id.topbar_text);
		 poster = (ImageView)findViewById(R.id.imageview_movie_poster);
		 poster.setClickable(true);
		 poster.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影簡介", "海報", "", (long)0);
				
			}
		});
		 chinese_name = (TextView)findViewById(R.id.textView_chinese_name);
		 english_name = (TextView)findViewById(R.id.textView_english_name);
		 level = (ImageView)findViewById(R.id.imageView_level);
		 runningtime = (TextView)findViewById(R.id.textView_runningtime);
		 play_btn = (Button)findViewById(R.id.button_video);
		 //schedule_btn = (Button)findViewById(R.id.button_schedule);
		 textView_time = (TextView)findViewById(R.id.textView_time);
		 //textView_drama = (TextView)findViewById(R.id.textView_drama);
		 textView_director = (TextView)findViewById(R.id.textView_director);
		 textView_actor = (TextView)findViewById(R.id.textView_actor);	
	}
	
    private void fetchData() {
    	SQLiteMovieDiary sqlMovieDiary = new SQLiteMovieDiary(this);
		movie = sqlMovieDiary.getMovie(movie_id);
	}
    
    class UpdateMovieTask extends AsyncTask<Integer, Integer, String>{
    	
    	private Movie updatedMovie;

		@Override
		protected String doInBackground(Integer... params) {
			MovieAPI movieAPI = new MovieAPI();
			updatedMovie = movieAPI.getMovieUpdate(movie);
			
			return null;
		}
		
		@Override  
        protected void onPostExecute(String result) {
        	if(updatedMovie==null){
        		showReloadDialog(MovieInfoActivity.this);
        	}else{
        		movie = updatedMovie;
	        	setViews();
	            setListener();
        	}
            super.onPostExecute(result);  
        } 
    	
    }
    
    class AdTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			
			return null;
		}
		
		 @Override  
	     protected void onPostExecute(String result) {
			 setAd();
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
        	progressdialogInit= new ProgressDialog(MovieInfoActivity.this);
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
        		showReloadDialog(MovieInfoActivity.this);
        	}else{
	        	setViews();
	            setListener();
        	}
        	if(movie != null && (movie.getReleaseDate() == null || movie.getRunningTime() == 0 || movie.getYoutubeId() == null)) {                
                UpdateMovieTask updateTask = new UpdateMovieTask();
                updateTask.execute();
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
