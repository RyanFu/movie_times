package com.jumplife.movieinfo;

import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.adapter.MovieGalleryAdapter;
import com.jumplife.movieinfo.MovieIntroContentActivity.LoadDataTask;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.promote.PromoteAPP;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;


@SuppressWarnings("deprecation")
public class MovieInfoTable extends TrackedActivity {
	private Gallery galleryTopMivie;
	private ImageView imageviewTopMovie;
	private ArrayList<Movie> movieList;
	private ProgressBar progressbarTopMovie;
	private LoadGalleryTask loadGalleryTask;
	public static String TAG = "MovieInfoTable";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	long startTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movieintro);
        findAndSetListener();
        loadGalleryTask = new LoadGalleryTask();
        if(Build.VERSION.SDK_INT < 11)
        	loadGalleryTask.execute();
        else
        	loadGalleryTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
        long endTime = System.currentTimeMillis();
        Log.e(TAG, "sample method took（movie infotable activity) %%%%%%%%%%%%%%%%%%%%%%%%%%%%"+(endTime-startTime)+"ms");
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN){
			
			PromoteAPP promoteAPP = new PromoteAPP(MovieInfoTable.this);
        	if(!promoteAPP.isPromote) {
				new AlertDialog.Builder(this)
					.setTitle("- 離開程式? -")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {
							MovieInfoTable.this.finish();
						}
					})
					.setNegativeButton("否", null)
					.show();
        	} else
		    	promoteAPP.promoteAPPExe();
						
			return true;
	    } else
	    	return super.onKeyDown(keyCode, event);
	}
    
    private void findAndSetListener() {
    	progressbarTopMovie = (ProgressBar)findViewById(R.id.progressbar_topmovie);
    	galleryTopMivie = (Gallery)findViewById(R.id.gallery_topmovie);
    	imageviewTopMovie = (ImageView)findViewById(R.id.imageview_topmovie);
    	Button buttonTheaterLike = (Button)findViewById(R.id.button_theaterlike);
    	Button buttonRecent = (Button)findViewById(R.id.button_recentmovie);
    	Button buttonFirstMovie = (Button)findViewById(R.id.button_firstmovie);
		Button buttonSecondMovie = (Button)findViewById(R.id.button_secondmovie);
		Button buttonWeekly = (Button)findViewById(R.id.button_weekly);
		buttonTheaterLike.setOnClickListener(ClickTheaterLike);
		buttonFirstMovie.setOnClickListener(ClickFirstMovie);
		buttonSecondMovie.setOnClickListener(ClicksecondMovie);
		buttonWeekly.setOnClickListener(ClickWeekly);	
		buttonRecent.setOnClickListener(ClickRecentMovie);
		imageviewTopMovie.setOnClickListener(ClickTopMovieRefresh);
	}
	
	private void initTopMovieGallery() {		
		galleryTopMivie.setAdapter(new MovieGalleryAdapter(MovieInfoTable.this, movieList, true));
        galleryTopMivie.setOnItemClickListener(clickGallery);
        galleryTopMivie.setSelection(1);
	}
	
	private OnItemClickListener clickGallery = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
			EasyTracker.getTracker().trackEvent("電影篩選條件", "種類", "票房排行", (long)0);
			
			Intent newAct = new Intent();
			newAct.putExtra("movie_id", movieList.get(position).getId());
		   	newAct.putExtra("movie_name", movieList.get(position).getChineseName());
			newAct.putExtra("theater_id", -1);
			newAct.setClass( MovieInfoTable.this, MovieInfoTabActivities.class );
            startActivity( newAct );
		}
	};
	
	private OnClickListener ClickTopMovieRefresh = new OnClickListener() {
    	public void onClick(View v) {
    		new LoadTopMovieTask().execute();
		}
    };
    
    private OnClickListener ClickTheaterLike = new OnClickListener() {
    	public void onClick(View v) {
    		EasyTracker.getTracker().trackEvent("電影篩選條件", "種類", "最愛影城", (long)0);
    		
    		Intent newAct = new Intent();
	    	newAct.putExtra("theaterType", 1);
	    	newAct.putExtra("area_id", -1);
	    	newAct.putExtra("area_name", "最愛影城");
	    	newAct.setClass( MovieInfoTable.this, TheaterListActivity.class );
            startActivity( newAct );
		}
    };
    
	private OnClickListener ClickRecentMovie = new OnClickListener() {
    	public void onClick(View v) {
    		EasyTracker.getTracker().trackEvent("電影篩選條件", "種類", "近期上映", (long)0);
    		
    		Intent newAct = new Intent();
    		newAct.putExtra("movielist", 0);
            newAct.setClass( MovieInfoTable.this, MovieSectionList.class );
            startActivity( newAct );
		}
    };
    
	private OnClickListener ClickFirstMovie = new OnClickListener() {
    	public void onClick(View v) {
    		EasyTracker.getTracker().trackEvent("電影篩選條件", "種類", "首輪電影", (long)0);
       
            Intent newAct = new Intent();
    		newAct.putExtra("movielist", 1);
            newAct.setClass( MovieInfoTable.this, MovieSectionList.class);
            startActivity( newAct );
		}
    };
    
    private OnClickListener ClicksecondMovie = new OnClickListener() {
    	public void onClick(View v) {
    		EasyTracker.getTracker().trackEvent("電影篩選條件", "種類", "二輪電影", (long)0);

            Intent newAct = new Intent();
    		newAct.putExtra("movielist", 2);
            newAct.setClass( MovieInfoTable.this, MovieSectionList.class );
            startActivity( newAct );   		
		}
    };
    
    private OnClickListener ClickWeekly = new OnClickListener() {
    	public void onClick(View v) {
    		EasyTracker.getTracker().trackEvent("電影篩選條件", "種類", "本周新片", (long)0);
    		
    		Intent newAct = new Intent();
    		newAct.putExtra("movielist", 3);
            newAct.setClass( MovieInfoTable.this, MovieList.class );
            startActivity( newAct );
		}
    };
    
    private void fetchData() {
    	SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(this);
		SQLiteDatabase db = instance.getWritableDatabase();
    	ArrayList<Movie> tmpList = instance.getMovieList(db, SQLiteMovieInfoHelper.FILTER_TOP_10);
    	db.close();
        instance.closeHelper();
    	
    	SharePreferenceIO spIO = new SharePreferenceIO(this);
    	String hotString = spIO.SharePreferenceO("hot_movie", null);
    	if(hotString != null) {
	        String[] hotSeq = hotString.split(",");
	        if(hotSeq != null) {
	        	for(int i=0; i<hotSeq.length; i++) {
	        		for(int j=0; j<tmpList.size(); j++) {
	        			if(Integer.parseInt(hotSeq[i]) == tmpList.get(j).getId()) {
	        				Movie tmp = tmpList.get(j);
	        				movieList.add(tmp);
	        				tmpList.remove(j);
	        			}
	        		}
	        	}
	        } 
    	}
	}
    
    class LoadGalleryTask extends AsyncTask<Integer, Integer, String>{  
          
        @Override  
        protected void onPreExecute() {
        	movieList = new ArrayList<Movie>(10);
        	progressbarTopMovie.setVisibility(View.VISIBLE);
        	imageviewTopMovie.setVisibility(View.GONE);
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
        	if(movieList == null || movieList.size() == 0){
        		imageviewTopMovie.setVisibility(View.VISIBLE);
	        	progressbarTopMovie.setVisibility(View.GONE);
	        	galleryTopMivie.setVisibility(View.GONE);
	         }else{
	        	progressbarTopMovie.setVisibility(View.GONE);
	         	imageviewTopMovie.setVisibility(View.GONE);
	 			galleryTopMivie.setVisibility(View.VISIBLE);	         	
	        	initTopMovieGallery();
	         }
	         super.onPostExecute(result);  
        }  
          
    }
    
    public class LoadTopMovieTask extends AsyncTask<Integer, Integer, String>{  
        
        private ProgressDialog progressdialogInit;

		@Override  
        protected void onPreExecute() {
        	progressdialogInit= ProgressDialog.show(MovieInfoTable.this, "Load", "Loading…");
            super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	MovieAPI movieAPI = new MovieAPI();
        	movieList = movieAPI.getMovieList(MovieAPI.FILTER_TOP_10);
            return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	progressdialogInit.dismiss();
        	if(movieList == null || movieList.size() == 0){
        		imageviewTopMovie.setVisibility(View.VISIBLE);
	        	progressbarTopMovie.setVisibility(View.GONE);
	        	galleryTopMivie.setVisibility(View.GONE);
	         }else{
	        	progressbarTopMovie.setVisibility(View.GONE);
	         	imageviewTopMovie.setVisibility(View.GONE);
	 			galleryTopMivie.setVisibility(View.VISIBLE);	         	
	        	initTopMovieGallery();
	         }
        	super.onPostExecute(result);
        }  
          
    }

    @Override
	protected void onDestroy(){
		super.onDestroy();
		if (loadGalleryTask!= null && loadGalleryTask.getStatus() != AsyncTask.Status.FINISHED)
			loadGalleryTask.cancel(true);
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
}
