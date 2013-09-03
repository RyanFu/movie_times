package com.jumplife.movieinfo;

import java.util.ArrayList;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.ad.AdGenerator;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.sectionlistview.MovieListAdapter;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import com.jumplife.sqlite.SQLiteMovieDiary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class MovieList extends TrackedActivity implements AdWhirlInterface{
	
	private SharePreferenceIO shIO;
	private boolean likeTheater = false;
	String[] likeTheaters;
	public String[] ListType =
	{
	    "近期上映", "首輪電影", "二輪電影", "本周新片"
	};
	
	public enum TYPE
    {
        RECENT, FIRSTROUND, SECONDROUND, WEEKLY, THEATER
    };
    TYPE listType = TYPE.RECENT;
    
	private ArrayList<Movie> movieList = new ArrayList<Movie>();	
	private ListView movieListView;

	private View viewHeader;
	private ImageView imageview_like;
	private LinearLayout theaterCheck;
    private LinearLayout theaterLocation;
    private LinearLayout theaterPhone;
    private LinearLayout theaterLike;
    private AlertDialog dialogTheaterInfo;
    
	private Theater theater;
	private LoadDataTask tast;
	//private static String TAG = "MovieList";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        getType();
        findViews(); 
        tast = new LoadDataTask();
    	if(Build.VERSION.SDK_INT < 11)
    		tast.execute();
        else
        	tast.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
        
        AdTask adTask = new AdTask();
    	adTask.execute();
    }
	
	private void getType() {
		Bundle extras = getIntent().getExtras();
        try{
        	int type = extras.getInt("movielist");
        	switch(type){
        	case 0:
        		listType = TYPE.RECENT;
        		break;
        	case 1:
        		listType = TYPE.FIRSTROUND;
        		break;
        	case 2:
        		listType = TYPE.SECONDROUND;
        		break;
        	case 3:
        		listType = TYPE.WEEKLY;
        		break;	
        	case 4:
        		theater = new Theater(extras.getInt("theater_id"), extras.getString("theater_name"),
        				new ArrayList<Movie> (10), "", "", extras.getString("theater_buylink"), -1, 
        				extras.getString("theater_phone"), extras.getString("theater_location"), "");
        		listType = TYPE.THEATER;
        	}
        }catch(Exception e){
        	
        }
		
	}

	private void setListListener() {
		movieListView.setOnItemClickListener(new OnItemClickListener() {  
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(listType == TYPE.THEATER)
					position = position-1;
				if(position > -1) { 
		    	  	Movie movie = movieList.get(position);
		    	   	Intent newAct = new Intent();
		    	   	newAct.putExtra("movie_id", movie.getId());
		    	   	newAct.putExtra("movie_name", movie.getChineseName());
		    	    if(listType == TYPE.THEATER)
		    	    	newAct.putExtra("theater_id", theater.getId());
		    	    else
		    	    	newAct.putExtra("theater_id", -1);
		            newAct.setClass( MovieList.this, MovieInfoTabActivities.class );
		            startActivity( newAct );
				}
			}
		});
		theaterCheck.setOnClickListener(new OnClickListener() {  
			public void onClick(View arg0) {
				if(theater.getBuyLink().contains("movietimes_jl")) {
					EasyTracker.getTracker().trackEvent("電影院資訊", "訂票", "EZ訂", (long)0);
					Intent newAct = new Intent();
	                newAct.setClass(MovieList.this, EzCheckActivity.class);
	                startActivity(newAct);
				} else {
					EasyTracker.getTracker().trackEvent("電影院資訊", "訂票", "非EZ訂", (long)0);
					Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(theater.getBuyLink()));
					startActivity(intent);
				}
			}
		});
		theaterPhone.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EasyTracker.getTracker().trackEvent("電影院資訊", "電話", "", (long)0);
				showTheaterInfoDialog(1, "影城電話 : ", theater.getPhone());
			}			
		});
		theaterLocation.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				EasyTracker.getTracker().trackEvent("電影院資訊", "地圖", "", (long)0);
				showTheaterInfoDialog(2, "影城地址 : ", theater.getAddress());
			}			
		});
		theaterLike.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				shIO = new SharePreferenceIO(MovieList.this);
				String tmpLikeTheaters = "";
				if(likeTheater) {
					for(int i=0; i<likeTheaters.length; i++)
						if(!likeTheaters[i].equals(String.valueOf(theater.getId())))
							tmpLikeTheaters = tmpLikeTheaters + likeTheaters[i] + ",";
					shIO.SharePreferenceI("like_theater", tmpLikeTheaters);
					likeTheater = false;
					EasyTracker.getTracker().trackEvent("最愛影城", "退出", "", (long)0);
				} else {
					for(int i=0; i<likeTheaters.length; i++)
						tmpLikeTheaters = tmpLikeTheaters + likeTheaters[i] + ",";
					tmpLikeTheaters = tmpLikeTheaters + theater.getId();
					shIO.SharePreferenceI("like_theater", tmpLikeTheaters);
					likeTheater = true;
					EasyTracker.getTracker().trackEvent("最愛影城", "加入", "", (long)0);
					Toast.makeText(MovieList.this, "已加入至《最愛影城》", Toast.LENGTH_LONG).show();
				}
				setLike();
			}			
		});
	}

	private void setView() {
		if (listType == TYPE.THEATER) {
			shIO = new SharePreferenceIO(this);
			likeTheaters = shIO.SharePreferenceO("like_theater", "").split(",");
			for(int i=0; i<likeTheaters.length; i++)
				if(likeTheaters[i].equals(String.valueOf(theater.getId())))
					likeTheater = true;
					
			if(theater.getBuyLink() == null) {
				theaterCheck.setVisibility(View.INVISIBLE);
			} else {
				theaterCheck.setVisibility(View.VISIBLE);
			}
			if(theater.getAddress() != null)
				theaterPhone.setVisibility(View.VISIBLE);
			else
				theaterPhone.setVisibility(View.INVISIBLE);
			if(theater.getAddress() != null)
				theaterLocation.setVisibility(View.VISIBLE);
			else
				theaterLocation.setVisibility(View.INVISIBLE);
			setLike();
		}
	}
	
	private void setLike(){		
	    if(likeTheater) {
	    	imageview_like.setImageResource(R.drawable.theater_like);
	    } else {
	    	imageview_like.setImageResource(R.drawable.theater_unlike);
	    }
	}
	
	@SuppressWarnings("unchecked")
	private void fetchData() {
		MovieAPI movieAPI = new MovieAPI();
		SQLiteMovieDiary sqlMovieDiary = new SQLiteMovieDiary(this);
		if (listType == TYPE.RECENT)
			movieList = sqlMovieDiary.getMovieList(SQLiteMovieDiary.FILTER_RECENT);
		else if (listType == TYPE.FIRSTROUND)
			movieList = sqlMovieDiary.getMovieList(SQLiteMovieDiary.FILTER_FIRST_ROUND);
        else if (listType == TYPE.SECONDROUND)
			movieList = sqlMovieDiary.getMovieList(SQLiteMovieDiary.FILTER_SECOND_ROUND);
		else if (listType == TYPE.WEEKLY)
			movieList = sqlMovieDiary.getMovieList(SQLiteMovieDiary.FILTER_THIS_WEEK);
		else{
			ArrayList<Movie> movies = movieAPI.getMoviesIdandHallList(theater);
			if (movies != null && movies.size() > 0) {
				movieList = sqlMovieDiary.getMovieListWithHall(movies);
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
		}
    }

	private void setListAdatper() {
		if(listType == TYPE.THEATER) {
			movieListView.addHeaderView(viewHeader);
		}
		
		movieListView.setAdapter(new MovieListAdapter(MovieList.this, movieList));
		movieListView.setTextFilterEnabled(true);
	}

	private void findViews() {
		TextView topbar_text = (TextView)findViewById(R.id.topbar_text);
		viewHeader = LayoutInflater.from(MovieList.this).inflate(
				R.layout.listview_theaterinfo_header, null);
		theaterCheck = (LinearLayout) viewHeader.findViewById(R.id.ll_check);
        theaterLocation = (LinearLayout) viewHeader.findViewById(R.id.ll_address);
        theaterPhone = (LinearLayout) viewHeader.findViewById(R.id.ll_phone);
        theaterLike = (LinearLayout) viewHeader.findViewById(R.id.ll_like);
        imageview_like = (ImageView)viewHeader.findViewById(R.id.imageview_like);
		
		if(listType == TYPE.RECENT)
			topbar_text.setText(ListType[0]);
		else if (listType == TYPE.FIRSTROUND)
			topbar_text.setText(ListType[1]);
		else if (listType == TYPE.SECONDROUND)
			topbar_text.setText(ListType[2]);
		else if (listType == TYPE.WEEKLY)
			topbar_text.setText(ListType[3]);
		else
			topbar_text.setText(theater.getName());
		
		movieListView = (ListView)findViewById(R.id.listview_movie);		
	}
	
	class AdTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			
			return null;
		}
		
		 @Override  
	     protected void onPostExecute(String result) {
			 //setAd();
			 AdGenerator adGenerator = new AdGenerator(MovieList.this);
			//adGenerator.setAdmobAd();
			 adGenerator.setAdwhirlAd();
			 super.onPostExecute(result);

		 }
    	
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
			progressdialogInit= ProgressDialog.show(MovieList.this, "Load", "Loading…");
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
        		showReloadDialog(MovieList.this);
        	}else{
        		setView();
	        	setListAdatper();
	            setListListener();
        	}
        	 super.onPostExecute(result);
        }          
    }
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if (tast!= null && tast.getStatus() != AsyncTask.Status.FINISHED)
		  tast.cancel(true);
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
	
	private void showTheaterInfoDialog(final int type, String title, String description) {
		View viewTheaterInfo;
		LayoutInflater factory = LayoutInflater.from(MovieList.this);
        viewTheaterInfo = factory.inflate(R.layout.dialog_theater_info,null);
        dialogTheaterInfo = new AlertDialog.Builder(MovieList.this).create();
        dialogTheaterInfo.setView(viewTheaterInfo);
        ImageView imageView = (ImageView)viewTheaterInfo.findViewById(R.id.imageview_confirm);
        TextView textviewTitle = (TextView)viewTheaterInfo.findViewById(R.id.textView_title);
        TextView textviewDescription = (TextView)viewTheaterInfo.findViewById(R.id.textView_description);
        TextView textviewConfirm = (TextView)viewTheaterInfo.findViewById(R.id.textview_confirm);
        
        textviewTitle.setText(title);
        textviewDescription.setText(description);
        if(type == 1) {
        	textviewConfirm.setText("電話");
        	imageView.setImageResource(R.drawable.icon_phone);
        } else {
        	textviewConfirm.setText("地址");
        	imageView.setImageResource(R.drawable.icon_map);
        }
        	
        dialogTheaterInfo.setOnKeyListener(new OnKeyListener(){
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if(KeyEvent.KEYCODE_BACK==keyCode)
                	if(dialogTheaterInfo != null && dialogTheaterInfo.isShowing())
                		dialogTheaterInfo.cancel();
                return false;
            }
        });
        ((RelativeLayout)viewTheaterInfo.findViewById(R.id.ll_cancel))
        .setOnClickListener(
            new OnClickListener(){
                public void onClick(View v) {
                    //取得文字方塊中的關鍵字字串
                	if(dialogTheaterInfo != null && dialogTheaterInfo.isShowing())
                		dialogTheaterInfo.cancel();                	
                }
            }
        );
        ((RelativeLayout)viewTheaterInfo.findViewById(R.id.ll_confirm))
        .setOnClickListener(
            new OnClickListener(){
                public void onClick(View v) {
                    //取得文字方塊中的關鍵字字串
                	if(type == 1) {
                		Uri uri = Uri.parse("tel:" + theater.getPhone());
						Intent intent=new Intent(Intent.ACTION_DIAL, uri);
				    	startActivity(intent);
                	} else {
                		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
								Uri.parse("http://maps.google.com/maps?q=" + theater.getAddress()));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK 
						          & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);			  
						intent.setClassName("com.google.android.apps.maps", 
						          "com.google.android.maps.MapsActivity");
						startActivity(intent);
                	}
                }
            }
        );
        dialogTheaterInfo.setCanceledOnTouchOutside(false);
        dialogTheaterInfo.show();
	}

	public void adWhirlGeneric()
	{
		// TODO Auto-generated method stub
		
	}
}
