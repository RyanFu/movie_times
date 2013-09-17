package com.jumplife.movieinfo;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.jumplife.adapter.MovieListAdapter;
import com.jumplife.movieinfo.MovieList.AdTask;
import com.jumplife.movieinfo.MovieList.LoadDataTask;
import com.jumplife.movieinfo.MovieList.TYPE;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.movieinfo.promote.PromoteAPP;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TheaterMovieList extends SherlockActivity {
	
	private Theater theater;
	private ImageButton ibLike;
	private ImageButton ibTheaterInfo;
	
	private ListView lvMovieList;
	
	private int tlMargin = 0;
	private int itemMargin = 0;
	
	private ArrayList<Movie> movieList = new ArrayList<Movie>();
	private LoadDataTask tast;
	private boolean likeTheater = false;
	String[] likeTheaters;
	private MovieListAdapter adapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater_movie_list);
        
        getSupportActionBar().setIcon(R.drawable.movietime);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		
        Bundle extras = getIntent().getExtras();
        
		theater = new Theater(extras.getInt("theater_id"), extras.getString("theater_name"),
				new ArrayList<Movie> (10), "", "", extras.getString("theater_buylink"), -1, 
				extras.getString("theater_phone"), extras.getString("theater_location"), "");
        
		getSupportActionBar().setTitle(theater.getName());
        
		initView(); 
        tast = new LoadDataTask();
    	if(Build.VERSION.SDK_INT < 11)
    		tast.execute();
        else
        	tast.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
        
    }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.action_bar_search, menu);
        
        MenuItem itemLike = menu.findItem(R.id.menu_item_like_theater);
        MenuItem itemInfo= menu.findItem(R.id.menu_item_info_theater);
         		 
        
        View actionViewLike = itemLike.getActionView();
        View actionViewInfo = itemInfo.getActionView();
        
        ibLike = (ImageButton) actionViewLike.findViewById(R.id.ib_like);
        ibTheaterInfo = (ImageButton) actionViewInfo.findViewById(R.id.ib_theater_info);
        
        if (ibLike != null) {
	        ibLike.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					String tmpLikeTheaters = "";
					if(likeTheater) {
						for(int i=0; i<likeTheaters.length; i++)
							if(!likeTheaters[i].equals(String.valueOf(theater.getId())))
								tmpLikeTheaters = tmpLikeTheaters + likeTheaters[i] + ",";
						MovieInfoAppliccation.shIO.edit().putString("like_theater", tmpLikeTheaters).commit();
						likeTheater = false;
						
						Toast.makeText(TheaterMovieList.this, "已移除於《最愛戲院》", Toast.LENGTH_LONG).show();
						
					} else {
						for(int i=0; i<likeTheaters.length; i++)
							tmpLikeTheaters = tmpLikeTheaters + likeTheaters[i] + ",";
						tmpLikeTheaters = tmpLikeTheaters + theater.getId();
						MovieInfoAppliccation.shIO.edit().putString("like_theater", tmpLikeTheaters).commit();
						likeTheater = true;
						
						Toast.makeText(TheaterMovieList.this, "已加入至《最愛戲院》", Toast.LENGTH_LONG).show();
						
					}
					setLike();
				}
				
			});
        }
        if (ibTheaterInfo != null) {
	        ibTheaterInfo.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
				
					setDialogTheaterInfo();
				}
				
			});
        }
        return true;
    }

	
	private void initView() {
	
		
		lvMovieList = (ListView)findViewById(R.id.lv_movie_list);

		
	}
	@SuppressWarnings("unchecked")
	private void fetchData() {
		MovieAPI movieAPI = new MovieAPI();
		SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(this);
		SQLiteDatabase db = instance.getReadableDatabase();
		db.beginTransaction();
		
		ArrayList<Movie> movies = movieAPI.getMoviesIdandHallList(theater);
		if (movies != null && movies.size() > 0) {
			movieList = instance.getMovieListWithHall(db, movies);
			Log.d(null, "movieList size :" + movieList.size());
			for(int i=0; i<movieList.size(); i++) {
				for(int j=0; j<movies.size(); j++) {
					if(movieList.get(i).getId() == movies.get(j).getId()) 
						movieList.get(i).setHall(movies.get(j).getHall());
				}
			}
			ArrayList<Movie> tmpList = (ArrayList<Movie>) movieList.clone();
			movieList.clear();
			for (int i = tmpList.size()-1; i>-1; i--)
				movieList.add(tmpList.get(i));
		} else
			movieList = new ArrayList<Movie>();
		
		db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        instance.closeHelper();
    }
	public class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
        private ProgressDialog progressdialogInit;
        private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	LoadDataTask.this.cancel(true);
		    	finish();	        	
		    }
    	};
    	
		@Override  
        protected void onPreExecute() {
			progressdialogInit= ProgressDialog.show(TheaterMovieList.this, "Load", "Loading…");
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
        	if(movieList==null || movieList.size()==0){
        		showReloadDialog(TheaterMovieList.this);
        	}else{
        		setView();
	        	setListAdatper();
	        	setListener();
        	}
        	 super.onPostExecute(result);
        }          
    }
	private void setListAdatper() {
		adapter	= new MovieListAdapter(TheaterMovieList.this, movieList, -1);
		lvMovieList.setAdapter(adapter);
	}
	private void setListener() {
		
		
		lvMovieList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent newAct = new Intent();
                newAct.putExtra("movie_id", movieList.get(position).getId());
                newAct.putExtra("movie_name", movieList.get(position).getChineseName());
                newAct.putExtra("theater_id", theater.getId());//
				newAct.setClass(TheaterMovieList.this, MovieIntroActivity.class );
				TheaterMovieList.this.startActivity(newAct);				
			}
		});
		lvMovieList.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
		
		
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
	private void setView() {
		
		likeTheaters = MovieInfoAppliccation.shIO.getString("like_theater", "").split(",");
		for(int i=0; i<likeTheaters.length; i++)
			if(likeTheaters[i].equals(String.valueOf(theater.getId())))
				likeTheater = true;
		
		
		setLike();
	}
	private void setLike(){		
	    if(likeTheater) {
	    	ibLike.setImageResource(R.drawable.addlike);
	    } else {
	    	ibLike.setImageResource(R.drawable.addlike_normal);
	    }
	}
	private void setDialogTheaterInfo() {

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels * 9 / 10;
		
		
		Dialog dialogInfo = new Dialog(this, R.style.dialogBasic);
		dialogInfo.setContentView(R.layout.dialog_theater_info);
		dialogInfo.getWindow().setLayout(width, LayoutParams.WRAP_CONTENT);
		
		TextView tvPhone = (TextView)dialogInfo.findViewById(R.id.tv_phone);
		TextView tvAddress = (TextView)dialogInfo.findViewById(R.id.tv_address);
		RelativeLayout rlCall = (RelativeLayout)dialogInfo.findViewById(R.id.rl_call);
		RelativeLayout rlMap = (RelativeLayout)dialogInfo.findViewById(R.id.rl_map);
		//TextView tvIntro = (TextView)dialogInfo.findViewById(R.id.tv_intro);				
		tvPhone.setText("電話 : " + theater.getPhone());
		tvAddress.setText("地址 : " + theater.getAddress());
		
		
		rlCall.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Uri uri = Uri.parse("tel:" + theater.getPhone());
				Intent intent=new Intent(Intent.ACTION_DIAL, uri);
		    	startActivity(intent);
				//showTheaterInfoDialog(1, "影城電話 : ", theater.getPhone());
			}			
		});
		rlMap.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
						Uri.parse("http://maps.google.com/maps?q=" + theater.getAddress()));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);			  
				//intent.setClassName("com.google.android.apps.maps" , "com.google.android.maps.MapsActivity");
				startActivity(intent);
			}			
		});
		dialogInfo.show();
		
		
		
		
		
	}
	
	
	
}
