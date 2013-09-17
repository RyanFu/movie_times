package com.jumplife.movieinfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.movieinfo.MovieInfoActivity.LoadDataTask;
import com.jumplife.movieinfo.MovieInfoActivity.UpdateMovieTask;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.promote.PromoteAPP;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MovieIntroActivity extends SherlockActivity {
	private int movie_id;
	private int theater_id;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageView ivLargePoster;
	private ImageView ivSmallPoster;
	private TextView tvChineseName;
	private TextView tvEnglishName;
	private TextView tvTimeLong;
	private TextView tvDate;
	private ImageView ivPlay;
	private TextView tvPlay;
	private TextView tvDirector;
	private TextView tvActor;
	private TextView tvIntroduction;
	
	
	private LinearLayout llPlay;
	private LinearLayout llBtns;
	private LinearLayout llTimetableBooking;
	private LinearLayout llShortTalk;
	private RelativeLayout rlNameMv;
	private RelativeLayout rlAll;
	private LoadDataTask taskLoad;
	private Movie movie;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);
        getSupportActionBar().setIcon(R.drawable.movietime);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		getSupportActionBar().setTitle("電影介紹");
		
        Bundle extras = getIntent().getExtras();
        movie_id = extras.getInt("movie_id");
        theater_id = extras.getInt("theater_id");
        findViews();
        
        taskLoad = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	taskLoad.execute();
        else
        	taskLoad.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
    }
	private void findViews() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = displayMetrics.heightPixels;
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub)
		.showImageForEmptyUri(R.drawable.stub)
		.showImageOnFail(R.drawable.stub)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.cacheOnDisc()
		.cacheInMemory()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		ivLargePoster = (ImageView)findViewById(R.id.iv_large_poster);
		ivSmallPoster = (ImageView)findViewById(R.id.iv_small_poster);
		tvChineseName = (TextView)findViewById(R.id.tv_chinese_name);
		tvEnglishName = (TextView)findViewById(R.id.tv_english_name);
		tvTimeLong = (TextView)findViewById(R.id.tv_timelong);
		tvDate = (TextView)findViewById(R.id.tv_date); 
		ivPlay = (ImageView)findViewById(R.id.iv_play);
		tvPlay = (TextView)findViewById(R.id.tv_play);
		tvDirector = (TextView)findViewById(R.id.tv_director);
		tvActor = (TextView)findViewById(R.id.tv_actor);
		tvIntroduction = (TextView)findViewById(R.id.tv_introduction);
		llPlay = (LinearLayout)findViewById(R.id.ll_play);
		llTimetableBooking = (LinearLayout)findViewById(R.id.ll_timetable_booking);
		llShortTalk = (LinearLayout)findViewById(R.id.ll_shorttalk);
		llBtns = (LinearLayout)findViewById(R.id.ll_btns);
 		rlAll = (RelativeLayout)findViewById(R.id.rl_poster_name_mv);
		rlNameMv = (RelativeLayout)findViewById(R.id.rl_name_mv);
		
		ivLargePoster.getLayoutParams().width = screenWidth;
		ivLargePoster.getLayoutParams().height = screenHeight;
		ivLargePoster.setScaleType(ScaleType.CENTER_CROP);
		
		ivSmallPoster.getLayoutParams().width = screenWidth * 1 / 3;  
		ivSmallPoster.getLayoutParams().height = screenWidth  * 1 / 2; 
		ivSmallPoster.setScaleType(ScaleType.CENTER_CROP);
		
		rlAll.setPadding(screenWidth * 1 / 15, screenHeight * 3 / 128, screenWidth * 1 / 15, 0);
		rlNameMv.getLayoutParams().width = screenWidth * 3 / 5;
		rlNameMv.getLayoutParams().height = screenWidth  * 1 / 2;
		rlNameMv.setPadding(screenHeight * 3 / 128, 0, 0, 0);
		
		ivPlay.getLayoutParams().height = screenWidth *1/12;
		ivPlay.getLayoutParams().width = screenWidth * 1/18;
		
		llBtns.getLayoutParams().height = screenHeight  * 7 / 64;
			
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
        	progressdialogInit= new ProgressDialog(MovieIntroActivity.this);
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
        		showReloadDialog(MovieIntroActivity.this);
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
	private void setListener() {
		llPlay.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		if(movie.getYoutubeId() != null && !movie.getYoutubeId().equals("null"))
	    			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getYoutubeVideoUrl())));
			}
		});
		llShortTalk.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intentContent = new Intent().setClass( MovieIntroActivity.this, MovieEvaluateActivity.class );
		        intentContent.putExtra("movie_id", movie_id);
		        startActivity( intentContent );
			}
		});
		llTimetableBooking.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent intentSchedule = new Intent().setClass( MovieIntroActivity.this, MovieScheduleActivity.class );
				intentSchedule.putExtra("movie_id", movie_id);
		        intentSchedule.putExtra("movie_id", movie_id);
		        intentSchedule.putExtra("theater_id", theater_id);
		        startActivity( intentSchedule );
			}
		});
	}
	private void setViews() {
        //topbar_text.setText("電影資訊");
		imageLoader.displayImage(movie.getPosterUrl(), ivSmallPoster, options);
		imageLoader.displayImage(movie.getPosterUrl(), ivLargePoster, options);
		tvChineseName.setText(movie.getChineseName());
		tvEnglishName.setText(movie.getEnglishName());
		
		if(movie.getRunningTime() > 0)
			tvTimeLong.setText("片長 : " + movie.getRunningTime() + "分");
		else
			tvTimeLong.setText("片長 : 尚未提供");
		
		DateFormat createFormatter = new SimpleDateFormat("yyyy-MM-dd");
		if(movie.getReleaseDate() != null)
			tvDate.setText("上映日期 : "+createFormatter.format(movie.getReleaseDate()));
		else
			tvDate.setText("未提供上映日期");
		if(movie.getYoutubeId() == null || movie.getYoutubeId().equals("null")) {
			tvPlay.setText(" 尚未提供");
			llPlay.setClickable(false);
		} else {
			tvPlay.setText(" 預告片");
			llPlay.setClickable(true);
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
		
		
		tvDirector.setText("導演 : "+directors);
		tvActor.setText("演員 : "+actors);
		tvIntroduction.setText("劇情簡介 : " + movie.getInttroduction());
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
			
			SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(MovieIntroActivity.this);
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
        		showReloadDialog(MovieIntroActivity.this);
        	}else{
        		movie = updatedMovie;
	        	setViews();
	            setListener();
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
 	       
 	        alert.setTitle("讀取錯誤");
 	        
 	        alert.show();		
 	}
    

}
