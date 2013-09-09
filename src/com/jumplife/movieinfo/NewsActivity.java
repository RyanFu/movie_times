package com.jumplife.movieinfo;

import java.util.ArrayList;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.google.analytics.tracking.android.TrackedActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jumplife.ad.AdGenerator;
import com.jumplife.adapter.NewsAdapter;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.News;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NewsActivity extends TrackedActivity implements AdWhirlInterface{
	private PullToRefreshListView newsListView;
	private ArrayList<News> newsList;
	private ImageButton imageButtonRefresh;
	private LinearLayout pullMore;
	private NewsAdapter newsAdapter;
	private int page = 1;
	public static final String TAG = "NewsActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_news);
		findViews();
	    LoadDataTask task = new LoadDataTask();
	    if(Build.VERSION.SDK_INT < 11)
			task.execute();
        else
        	task.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	    
	    AdTask adTask = new AdTask();
        adTask.execute();
	}
	
	private void findViews() {
		
       
        
		newsListView = (PullToRefreshListView)findViewById(R.id.listview_news);
		pullMore = (LinearLayout)findViewById(R.id.progressBar_pull_more);
		imageButtonRefresh = (ImageButton)findViewById(R.id.refresh);
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				LoadDataTask task = new LoadDataTask();
				if(Build.VERSION.SDK_INT < 11)
					task.execute();
		        else
		        	task.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
			}			
		});
	}
	
	private void setView() {
		if(newsList != null && newsList.size() > 0) {
			newsListView.setVisibility(View.VISIBLE);
			imageButtonRefresh.setVisibility(View.GONE);
		} else {
			newsListView.setVisibility(View.GONE);
			imageButtonRefresh.setVisibility(View.VISIBLE);
		}
	}
	
	private void FetchData() {
		MovieAPI movieAPI = new MovieAPI();
		newsList = movieAPI.getNewsList(page);
	}
	
	private void setListener() {
		newsListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				int pos = position - 1;
				News news = newsList.get(pos);
				
				if(news.getType() == News.TYPE_LINK) {
					
					Uri uri = Uri.parse(news.getLink());  
		    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
		    		startActivity(it);
		    		
				}
				else if(news.getType() == News.TYPE_PIC) {
					Intent intent = new Intent();
					intent.putExtra("picture_url", news.getPictureUrl());
					intent.putExtra("content", news.getContent());
					intent.setClass( NewsActivity.this, NewsPic.class );
			        startActivity( intent );
				}
			}
		});
		
		newsListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				newsListView.setLastUpdatedLabel(DateUtils.formatDateTime(getApplicationContext(),
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));

				// Do work to refresh the list here.
				new RefreshTask().execute();
			}
		});
		
		newsListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				new NextPageTask().execute();
			}			
		});
	}  

	private void setListAdatper() {
		newsAdapter = new NewsAdapter(NewsActivity.this, newsList);
		newsListView.setAdapter(newsAdapter);
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	private ProgressDialog progressdialogInit;
    	private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	LoadDataTask.this.cancel(true);
		    	newsListView.setVisibility(View.GONE);
				imageButtonRefresh.setVisibility(View.VISIBLE);
		    }
    	};

    	@Override  
        protected void onPreExecute() {
    		progressdialogInit= new ProgressDialog(NewsActivity.this);
        	progressdialogInit.setTitle("Load");
        	progressdialogInit.setMessage("Loading…");
        	progressdialogInit.setOnCancelListener(cancelListener);
        	progressdialogInit.setCanceledOnTouchOutside(false);
        	progressdialogInit.show();
        	newsListView.setVisibility(View.VISIBLE);
			imageButtonRefresh.setVisibility(View.GONE);
			super.onPreExecute();  
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            FetchData();
            return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	if(progressdialogInit != null && progressdialogInit.isShowing())
        		progressdialogInit.dismiss();
        	if(newsList != null && newsList.size() > 0){
        		Log.d(TAG, "List Size: " + newsList.size());
        		setListAdatper();
            	setListener();
            	page += 1;
        	}
        	super.onPostExecute(result);
        }
    }
	
	class RefreshTask  extends AsyncTask<Integer, Integer, String>{

		@Override  
        protected void onPreExecute() {
			page = 1;
        	super.onPreExecute();  
        }  
        @Override
		protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            FetchData();
			return "progress end";
		}
		@Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        } 
		protected void onPostExecute(String result) {
			setView();		
			if(newsList != null && newsList.size() > 0){
        		setListAdatper();
            	setListener();
            	page += 1;
        	}
			newsListView.onRefreshComplete();
        	super.onPostExecute(result);
        }
	}
	
	class NextPageTask  extends AsyncTask<Integer, Integer, String>{

		private ArrayList<News> tmpList;
		
		@Override  
        protected void onPreExecute() {
			pullMore.setVisibility(View.VISIBLE);
			super.onPreExecute();  
        }  
        @Override
		protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            MovieAPI movieAPI = new MovieAPI();
        	tmpList = movieAPI.getNewsList(page);
        	return "progress end";
		}
		@Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        } 
		protected void onPostExecute(String result) {
			pullMore.setVisibility(View.GONE);
			if(newsList == null)
				newsList = new ArrayList<News>(10);
			
			if(tmpList != null && tmpList.size() > 0){
				newsList.addAll(tmpList);
				newsAdapter.notifyDataSetChanged();
				page += 1;
    			// Call onRefreshComplete when the list has been refreshed.
        	}
			super.onPostExecute(result);
        }
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
	protected void onResume(){
        super.onResume();
   }
	
	class AdTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			
			return null;
		}
		
		 @Override  
	     protected void onPostExecute(String result) {
			 //setAd();
			 AdGenerator adGenerator = new AdGenerator(NewsActivity.this);
			//adGenerator.setAdmobAd();
			 adGenerator.setAdwhirlAd();
			 super.onPostExecute(result);
		 }
    }

	public void adWhirlGeneric()
	{
		// TODO Auto-generated method stub
		
	}

}
