package com.jumplife.fragment;


import java.util.ArrayList;


import com.jumplife.adapter.MovieListAdapter;
import com.jumplife.movieinfo.MovieInfoActivity;
import com.jumplife.movieinfo.MovieInfoAppliccation;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;



import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;

public class MovieTimeListFragment extends Fragment {
	
	private View fragmentView;
	//private ImageButton	ibRefresh;
	
	private ListView lvMovie; 
	private MovieListAdapter adapter;
	private ArrayList<Movie> movieList;
	private FragmentActivity mFragmentActivity;    
	private LoadDataTask loadtask;
	private int typeID;
	
	public static MovieTimeListFragment NewInstance(int typeId) {
		MovieTimeListFragment fragment = new MovieTimeListFragment();
	    Bundle args = new Bundle();
	    args.putInt("typeId", typeId);
	    fragment.setArguments(args);
	    MovieInfoAppliccation.shIO.edit().putInt("typeId", typeId).commit();
	    return fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		typeID = MovieInfoAppliccation.shIO.getInt("typeId", 0);
		fragmentView = inflater.inflate(R.layout.fragment_movietime_list, container, false);
		
		initView();
		
        loadtask = new LoadDataTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadtask.execute();
        else
        	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	    
		return fragmentView;
	}
	@Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	
	private void initView() {
		
		lvMovie = (ListView)fragmentView.findViewById(R.id.frag_listview_movie);
		
       /* ibRefresh = (ImageButton)fragmentView.findViewById(R.id.ib_movie_list_refresh);
		ibRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
            	loadtask = new LoadDataTask();
                if(Build.VERSION.SDK_INT < 11)
                	loadtask.execute();
                else
                	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
            }
        });
        */
	}
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
		/*	ivLoadingIcon.setVisibility(View.VISIBLE);
			ivLoadingCircle.setVisibility(View.VISIBLE);
			ivLoadingCircle.startAnimation(animation);*/
			
			//ibRefresh.setVisibility(View.GONE);
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
        	if(movieList != null && movieList.size() > 0){
        		setListAdatper();
        		setListener();
            	lvMovie.setVisibility(View.VISIBLE);
            	//ibRefresh.setVisibility(View.GONE);		
    		} else {
    			lvMovie.setVisibility(View.GONE);
    			//ibRefresh.setVisibility(View.VISIBLE);
    		}
        	
        	/*animation.cancel();
        	ivLoadingCircle.clearAnimation();
        	ivLoadingIcon.setVisibility(View.GONE);
        	ivLoadingCircle.setVisibility(View.GONE);*/

	        super.onPostExecute(result);  
        }
    }
	private void setListAdatper() {
		adapter	= new MovieListAdapter(mFragmentActivity, movieList, typeID);
		lvMovie.setAdapter(adapter);
	}
	private void setListener() {
        lvMovie.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent newAct = new Intent();
                newAct.putExtra("movie_id", movieList.get(position).getId());
                newAct.putExtra("movie_name", movieList.get(position).getChineseName());
				newAct.setClass(mFragmentActivity, MovieInfoActivity.class );
				mFragmentActivity.startActivity(newAct);				
			}
		});
        lvMovie.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true));
	}
	
	private void fetchData() {
		movieList = new ArrayList<Movie>();
		if(getArguments().containsKey("typeId")) {
			try {	
					SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(mFragmentActivity);
					SQLiteDatabase db = instance.getReadableDatabase();
					db.beginTransaction();
					if (getArguments().getInt("typeId", 0) == 1){
						ArrayList<Movie> tmpList = instance.getMovieList(db,SQLiteMovieInfoHelper.FILTER_FIRST_ROUND);
				    	
				    	String hotString = MovieInfoAppliccation.shIO.getString("hot_movie", null);
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
				    	movieList.addAll(tmpList);
					}
			        else if (getArguments().getInt("typeId", 0) == 2)
			        	movieList = instance.getMovieList(db,SQLiteMovieInfoHelper.FILTER_THIS_WEEK);
			        else if (getArguments().getInt("typeId", 0) == 3)
						movieList = instance.getMovieList(db,SQLiteMovieInfoHelper.FILTER_RECENT);
					else if (getArguments().getInt("typeId", 0) == 4)
						movieList = instance.getMovieList(db,SQLiteMovieInfoHelper.FILTER_SECOND_ROUND);
					else{/*
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
							movieList = new ArrayList<Movie>();*/
					}
					db.setTransactionSuccessful();
			        db.endTransaction();
			        db.close();
			        instance.closeHelper();
		    } catch (Exception e) {
				
			}
		
		}
	}
	
	
}
