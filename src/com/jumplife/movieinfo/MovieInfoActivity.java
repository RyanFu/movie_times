package com.jumplife.movieinfo;


import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.ad.AdGenerator;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.Record;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class MovieInfoActivity extends TrackedActivity implements AdWhirlInterface{
	
	//private TextView topbar_text;
	private ImageView poster;
	private TextView chinese_name;
	private TextView english_name;
	private ImageView level;
	private TextView runningtime;
	private Button play_btn;
	private Button ezcheck_btn;
	private TextView textView_time;
	//private TextView textView_drama;
	private TextView textView_director;
	private TextView textView_actor;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
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
		
	private void setViews() {
        //topbar_text.setText("電影資訊");
		imageLoader.displayImage(movie.getPosterUrl(), poster, options);
		chinese_name.setText(movie.getChineseName());
		english_name.setText(movie.getEnglishName());
		
		if(movie.getLevelUrl() == null || movie.getLevelUrl().equals("null"))
			level.setVisibility(View.INVISIBLE);
		else {
			imageLoader.displayImage(movie.getLevelUrl(), level, options);
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
		
		if(movie.get_is_ezding()) {
			ezcheck_btn.setClickable(true);
			Drawable ezding = getResources().getDrawable(R.drawable.btn_ezcheck);
			ezcheck_btn.setTextColor(getResources().getColor(R.color.textcolor_grey));
			ezcheck_btn.setCompoundDrawablesWithIntrinsicBounds(ezding, null, null, null);
			ezcheck_btn.setOnClickListener(new OnClickListener() {
		    	public void onClick(View v) {
		    		EasyTracker.getTracker().trackEvent("電影簡介", "EZ訂訂票", "", (long)0);
		    		Intent newAct = new Intent();
	                newAct.setClass(MovieInfoActivity.this, EzCheckActivity.class);
	                startActivity(newAct);
				}
			});
		}
	}
	
	private void findViews() {
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub)
		.showImageForEmptyUri(R.drawable.stub)
		.showImageOnFail(R.drawable.stub)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.cacheOnDisc()
		.cacheInMemory()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
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
		 ezcheck_btn = (Button)findViewById(R.id.button_ezcheck);
		 textView_time = (TextView)findViewById(R.id.textView_time);
		 textView_director = (TextView)findViewById(R.id.textView_director);
		 textView_actor = (TextView)findViewById(R.id.textView_actor);	
	}
	
    private void fetchData() {
    	SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(this);
		SQLiteDatabase db = instance.getReadableDatabase();
		
		movie = instance.getMovie(db, movie_id);
    	
    	db.close();
    	instance.closeHelper();
	}
    
    class UpdateMovieTask extends AsyncTask<Integer, Integer, String>{
    	
    	private Movie updatedMovie;

		@Override
		protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			
			SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(MovieInfoActivity.this);
			SQLiteDatabase db = instance.getWritableDatabase();
			
			MovieAPI movieAPI = new MovieAPI();
			updatedMovie = movieAPI.getMovieInfo(instance, db, movie);
			
			db.close();
			instance.closeHelper();
			
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
			 AdGenerator adGenerator = new AdGenerator(MovieInfoActivity.this);
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
        	if(movie == null) {
        		showReloadDialog(MovieInfoActivity.this);
        	} else {
	        	setViews();
	            setListener();
        	}
        	
            UpdateMovieTask updateTask = new UpdateMovieTask();
            if(Build.VERSION.SDK_INT < 11)
            	updateTask.execute();
            else
            	updateTask.executeOnExecutor(UpdateMovieTask.THREAD_POOL_EXECUTOR, 0);
        	
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

	public void adWhirlGeneric()
	{
		// TODO Auto-generated method stub
		
	}
}
