package com.jumplife.movieinfo;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;

import android.R.integer;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.adapter.RecordListAdapter;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.Record;
import com.jumplife.movieinfo.promote.PromoteAPP;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;

public class MovieEvaluateActivity extends SherlockActivity {

	private ListView listviewShow;
	private TextView tvNoEvaluate;
	
	private View viewHeader;
	private View viewFooter;
	private LinearLayout llMovieDiary;
	private LoadDataTask loadDataTask;
	private TextView tvIMDB;
	private TextView tvMovieDiary;
	
	private String IMDB;
	private String MovieDiary;
	private int movie_id;
	private RecordListAdapter recordListAdapter;
	private ArrayList<Record> recordList = new ArrayList<Record>();
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movieevaluate);
		getSupportActionBar().setIcon(R.drawable.movietime);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		getSupportActionBar().setTitle("電影短評");
		findViews();
		
		loadDataTask = new LoadDataTask();
		if(Build.VERSION.SDK_INT < 11)
			loadDataTask.execute();
        else
        	loadDataTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	}

	@Override
	protected void onDestroy() {
		if (loadDataTask != null
				&& loadDataTask.getStatus() != AsyncTask.Status.FINISHED)
			loadDataTask.cancel(true);
		super.onDestroy();
	}
	
	private void findViews() {
		listviewShow = (ListView) findViewById(R.id.listview_checks);
		viewHeader = LayoutInflater.from(MovieEvaluateActivity.this).inflate(
				R.layout.listview_movieevaluate_header, null);
		viewFooter = LayoutInflater.from(MovieEvaluateActivity.this).inflate(
				R.layout.listview_movieevaluate_footer, null);
		tvNoEvaluate = (TextView)viewFooter.findViewById(R.id.textview_noevaluate);
		
		tvMovieDiary = (TextView)viewHeader.findViewById(R.id.tv_score_moviediary);
		tvIMDB = (TextView)viewHeader.findViewById(R.id.tv_imdb);
		listviewShow.addHeaderView(viewHeader);
		listviewShow.addFooterView(viewFooter);
		llMovieDiary = (LinearLayout)findViewById(R.id.ll_moviediary);
		
	}

	private void fetchData() {
		Bundle extras = getIntent().getExtras();
		movie_id = extras.getInt("movie_id");
		MovieAPI movieAPI = new MovieAPI();
		String[] score = new String[2];
		movieAPI.getScoreRecord(movie_id, score, recordList );
		IMDB = score[0];
		MovieDiary = score[1];
	}

	private void setView() {
		
		
		float f = Float.parseFloat(MovieDiary.toString());
		f = f*100;
		
		MovieDiary = Integer.toString((int)Math.floor(f));
		
		tvIMDB.setText(IMDB);
		tvMovieDiary.setText(MovieDiary);
		
		llMovieDiary.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				PackageManager pm = MovieEvaluateActivity.this.getPackageManager();
			    Intent appStartIntent = pm.getLaunchIntentForPackage("com.jumplife.moviediary");
			    if(null == appStartIntent) {
			    	EasyTracker.getTracker().trackEvent("電影短評", "下載電影櫃", "", (long)0);
			    	startActivity(new Intent(Intent.ACTION_VIEW, 
				    		Uri.parse("market://details?id=com.jumplife.moviediary")));
			    }
			    
			    if(null != appStartIntent) {
			    	appStartIntent.addCategory("android.intent.category.LAUNCHER");
			    	appStartIntent.setComponent(new ComponentName("com.jumplife.moviediary",
				    		"com.jumplife.moviediary.MovieDiary"));
			    	appStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    MovieEvaluateActivity.this.startActivity(appStartIntent);
			    }
			}			
		});
		
		if (recordList == null) {
			showReloadDialog(MovieEvaluateActivity.this);
		} else {
			
		    if (recordList.size() == 0) 
				tvNoEvaluate.setVisibility(View.VISIBLE);
			else 
				tvNoEvaluate.setVisibility(View.GONE);			
		    sortRecordList();
			recordListAdapter = new RecordListAdapter(
					MovieEvaluateActivity.this, recordList);
			listviewShow.setAdapter(recordListAdapter);
		}
		
		
	}

	class LoadDataTask extends AsyncTask<Integer, Integer, String> {

		private ProgressDialog progressdialogInit;
		private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	Log.d("", "loadDataTask.getStatus() != AsyncTask.Status.FINISHED");
	    		LoadDataTask.this.cancel(true);
		    	finish();	        	
		    }
    	};
    	
		@Override
		protected void onPreExecute() {
			progressdialogInit= new ProgressDialog(MovieEvaluateActivity.this);
        	progressdialogInit.setTitle("Load");
        	progressdialogInit.setMessage("Loading…");
        	progressdialogInit.setOnCancelListener(cancelListener);
        	progressdialogInit.setCanceledOnTouchOutside(false);
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
			if (recordList == null) {
				showReloadDialog(MovieEvaluateActivity.this);
			} else {
				setView();
			}
			super.onPostExecute(result);
		}

	}

	public void showReloadDialog(final Context context) {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

		alt_bld.setMessage("是否重新載入資料？").setCancelable(true)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						loadDataTask = new LoadDataTask();
						if(Build.VERSION.SDK_INT < 11)
							loadDataTask.execute();
				        else
				        	loadDataTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
						dialog.dismiss();
					}
				});

		AlertDialog alert = alt_bld.create();
		// Title for AlertDialog
		alert.setTitle("讀取錯誤");
		// Icon for AlertDialog
		// alert.setIcon(R.drawable.gnome_logout);
		alert.show();
	}

	private void sortRecordList() {
		ArrayList<Record> haveContent = new ArrayList<Record>(8);
		ArrayList<Record> nonhaveContent = new ArrayList<Record>(8);
		
		Collections.sort(recordList, new Comparator<Record>() {
		    public int compare(Record record1, Record record2) {
				return record2.getLoveCount() - record1.getLoveCount();
		       
		    }
		});
		
		int size = recordList.size();
		for (int i = 0; i < size; i++) {
			if (recordList.get(i).getComment().equals(""))
				nonhaveContent.add(recordList.get(i));
			else
				haveContent.add(recordList.get(i));
		}
		recordList.clear();
		for (int i = 0; i < haveContent.size(); i++)
			recordList.add(haveContent.get(i));
		for (int i = 0; i < nonhaveContent.size(); i++)
			recordList.add(nonhaveContent.get(i));
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
