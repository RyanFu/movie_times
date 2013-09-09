package com.jumplife.movieinfo;

import java.util.ArrayList;

import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.AppProject;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieTime extends TrackedActivity {

	private LoadDataTask taskLoad;
	//private ProgressBar progressbar;
	private ImageView imageviewPBar;
	private TextView loading;
	private AnimationDrawable animationDrawable;
	public static String TAG = "MovieTimeActivities";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movietime);
        loading = (TextView)findViewById(R.id.textview_loading);
        loading.setText("更新資料中...");
        imageviewPBar = (ImageView)findViewById(R.id.imageview_progressbar);
        
        //progressbar = (ProgressBar)findViewById(R.id.progressBar1);
        //progressbar.setVisibility(View.VISIBLE);
        taskLoad = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	taskLoad.execute();
        else
        	taskLoad.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
    }
	
	@Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
        super.onWindowFocusChanged(hasFocus);  
        imageviewPBar.setBackgroundResource(R.anim.progressbar_video);
        animationDrawable = (AnimationDrawable) imageviewPBar.getBackground();
        animationDrawable.start();
    } 
	
	private String fetchData() {
		long startTime = System.currentTimeMillis();
		MovieAPI api = new MovieAPI();
		
        ArrayList<Integer>[] a = api.getUpdateMoviesId();
        if(a != null) {
    		
    		SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(this);
    		instance.createDataBase();
    		SQLiteDatabase db = instance.getWritableDatabase();
    		db.beginTransaction();
    		
	        ArrayList<Integer> moviesId = new ArrayList<Integer>();
	        moviesId = instance.findMoviesIdNotInDB(db, a[5]);
	        if (moviesId.size()>0){
		        String idLst = "";
		        for(int i=0; i<moviesId.size(); i++)
		           idLst = moviesId.get(i) + "," +idLst;
		        api.AddMoviesFromInfo(instance, db, idLst);
	        }
	        instance.updateMovieIs(db, a);
	        long endTime = System.currentTimeMillis();
	       
	        String hotSeq = "";
	        for(int i=0; i<a[3].size(); i++)
	        	hotSeq = hotSeq + a[3].get(i) + ",";
	        MovieInfoAppliccation.shIO.edit().putString("hot_movie", hotSeq);
	    	Log.e(TAG, "sample method took（movie time activity) %%%%%%%%%%%%%%%%%%%%%%%%%%%%"+(endTime-startTime)+"ms");
			
	    	ArrayList<AppProject> appProject = api.getAppProjectList(MovieTime.this);
	    	if(appProject != null) {
	    		instance.updateProject(db, appProject);
	    	}
	        
			db.setTransactionSuccessful();
	        db.endTransaction();
	        db.close();
	        instance.closeHelper();        
	    	
			return "progress end";
        } else {
        	return "progress fail";
        }
	}
	
	private void setData(){
		
		MovieInfoAppliccation.shIO.edit().putInt("typeId", 1);
		
		Intent newAct = new Intent();
		newAct.setClass( MovieTime.this, MainMenuActivity.class );
		startActivity(newAct);
    	finish();
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
        @Override  
        protected void onPreExecute() {
        	super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            return fetchData();
        }  
 

		@Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	if(result.equals("progress fail")) {
	        	//progressbar.setVisibility(View.INVISIBLE);
        		imageviewPBar.setVisibility(View.INVISIBLE);
	        	loading.setText(Html.fromHtml("網路連結不穩<br>更新資料失敗"));
	        	try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	setData();
	        super.onPostExecute(result);  
        }  
          
    }
	
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	if(animationDrawable != null && animationDrawable.isRunning())
      	  animationDrawable.stop();
    }
}
