package com.jumplife.movieinfo;

import java.util.ArrayList;

import java.util.Date;
import java.util.GregorianCalendar;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.ad.AdGenerator;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.sectionlistview.TvScheduleListAdapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TvScheduleActivity extends TrackedActivity implements AdWhirlInterface{
	
	private int id;
	private static int currentDateIdx = 0;
	private ImageButton buttonNext;
	private ImageButton buttonPre;
	private TextView date;
	private ListView tvListView;
	private LoadDataTask loadDataTask;
	private String[] tvSchedule = new String[totalDays*2];
	private ArrayList<ArrayList<tvSchedule>> tvScheduleDays;

	public static int totalDays = 3;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvschedule_list);        
        findViews();
        loadDataTask = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	loadDataTask.execute();
        else
        	loadDataTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
        
        AdTask adTask = new AdTask();
        adTask.execute();
    }
	
	private void fetchData() {
		MovieAPI movieAPI = new MovieAPI();
		tvSchedule = movieAPI.getTvSchedule(id);
	}

	private void setListAdatper() {
		if(currentDateIdx < tvScheduleDays.size())
			tvListView.setAdapter(new TvScheduleListAdapter(TvScheduleActivity.this, tvScheduleDays.get(currentDateIdx)));
	}

	private void findViews() {
		TextView topbar_text = (TextView)findViewById(R.id.topbar_text);		
		Bundle extras = getIntent().getExtras();
		topbar_text.setText(extras.getString("tv_name"));
		
		tvListView = (ListView)findViewById(R.id.listview_theater);
		buttonNext = (ImageButton)findViewById(R.id.btn_next);
		buttonPre = (ImageButton)findViewById(R.id.btn_pre);
		date = (TextView)findViewById(R.id.textview_date);
		id = extras.getInt("tv_id");
	}
	
	private void setView(){
		date.setText(tvSchedule[currentDateIdx*2+1]);
		if(currentDateIdx == 0) {
			//date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.schedule_cycle, 0, 0, 0);
			date.setTextColor(getResources().getColor(R.color.main_color_yellow));
			date.setTypeface(null, Typeface.BOLD);
		} else {
			//date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.schedule_cycle_null, 0, 0, 0);
			date.setTextColor(getResources().getColor(R.color.tab_normal_text_color));
			date.setTypeface(null, Typeface.NORMAL);
		}
	}
	
	private void setListener(){
		buttonNext.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				currentDateIdx +=1;
				if(currentDateIdx > tvScheduleDays.size()-1)
					currentDateIdx = 0;
				Log.d("", "current Date idx : " + currentDateIdx);
				setView();
				setListAdatper();
			}			
		});
		buttonPre.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				currentDateIdx -=1;
				if(currentDateIdx < 0) {
					if(tvScheduleDays.size() > 0)
						currentDateIdx = tvScheduleDays.size()-1;
					else
						currentDateIdx = 0;
				}
				Log.d("", "current Date idx : " + currentDateIdx);
				setView();
				setListAdatper();
			}			
		});
		tvListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int month = 0;
				int day = 0;
				String[] tmp = tvSchedule[currentDateIdx*2+1].split("月");
				if(tmp[0] != null) {
					if(tmp[0].length() < 3)
						month = Integer.parseInt(tmp[0].substring(0, 1));
					else
						month = Integer.parseInt(tmp[0].substring(0, 2));
				}
				
				tmp = tmp[1].split("日");
				if(tmp[0] != null) {
					if(tmp[0].length() < 3)
						day = Integer.parseInt(tmp[0].substring(1, 2).replace(" ", ""));
					else
						day = Integer.parseInt(tmp[0].substring(1, 3).replace(" ", ""));				 
				}
				
				if(month != 0 && day != 0) {
					AlertDialog alertdialog = CalendarRemindDialog("增加提醒", "開啟Google 日曆"
						, tvScheduleDays.get(currentDateIdx).get(arg2), month, day);
					alertdialog.show();
				}
			}					
		});
	}
	
	private AlertDialog CalendarRemindDialog(String title,String message, final tvSchedule schedule, final int month, final int day){
        //產生一個Builder物件
    	Builder builder = new AlertDialog.Builder(TvScheduleActivity.this);
	    builder.setTitle(title);
	    builder.setMessage(message);
	    builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
	    	@SuppressWarnings("deprecation")
			public void onClick(DialogInterface dialog, int which) {
	            Intent intent = new Intent(Intent.ACTION_EDIT);  
	    		intent.setType("vnd.android.cursor.item/event");
	    		intent.putExtra("title", "[電影時刻表提醒] 收看《" + schedule.name + "》");
	    		intent.putExtra("description", "現正播映 : " + schedule.name);
	    		Date date = new Date();	            
	    		String[] time = schedule.time.split(":");
	    		Log.d(null, date.getYear() + "年" + month + "月" + day + "日" + time[0] + ":" + time[1]);
	    		GregorianCalendar calDate = new GregorianCalendar(date.getYear()+1900, month-1, day,
	    				Integer.parseInt(time[0]), Integer.parseInt(time[1]), 0);
	    		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
	    		  calDate.getTimeInMillis());
	    		calDate.set(date.getYear()+1900, month, day,
	    				Integer.parseInt(time[0]), Integer.parseInt(time[1]) + 10, 0);
	    		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
	    		  calDate.getTimeInMillis());

	    		startActivity(intent);
	        }
	    });
	    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {}
	    });
	    return builder.create();
   }
	
	private void SetSecheduleList(){
		tvScheduleDays = new ArrayList<ArrayList<tvSchedule>>(totalDays);
		for(int j=0; j<totalDays; j+=1) {
			ArrayList<tvSchedule> tvScheduleDay = new ArrayList<tvSchedule>(10);
			String[] scheduleday = tvSchedule[j*2].split("\\|\\|\\|");
			for(int i=0; i<scheduleday.length; i++){
				String[] eachSchedule = scheduleday[i].split("\\:\\:\\:");
				if(eachSchedule.length > 1) {
					tvSchedule tmp = new tvSchedule(eachSchedule[0], eachSchedule[1]);
					tvScheduleDay.add(tmp);
				}
			}
			if(!tvScheduleDay.isEmpty())
				tvScheduleDays.add(tvScheduleDay);
		}
	}
	
	public class tvSchedule {
		public String time;
		public String name;
		public tvSchedule(String time, String name) {
			this.time = time;
			this.name = name;
		}
	}
	
	class AdTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			
			return null;
		}
		
		 @Override  
	     protected void onPostExecute(String result) {
			 //setAd();
			 AdGenerator adGenerator = new AdGenerator(TvScheduleActivity.this);
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
			progressdialogInit= ProgressDialog.show(TvScheduleActivity.this, "Load", "Loading…");
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
        	if(tvSchedule == null){
        		showReloadDialog(TvScheduleActivity.this);
    		}else{
    			setView();
    			setListener();
    			SetSecheduleList();
    			setListAdatper();
	            super.onPostExecute(result);
    		}
        }  
          
    }
	
	@Override
    protected void onStart()
    {
	    super.onStart();        
    }
    @Override
    protected void onStop()
    {
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
	            	loadDataTask = new LoadDataTask();
	            	if(Build.VERSION.SDK_INT < 11)
	                	loadDataTask.execute();
	                else
	                	loadDataTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	            	dialog.dismiss();
	            }
	        });
	        
	        AlertDialog alert = alt_bld.create();
	        alert.setTitle("讀取錯誤");
	        alert.show();
		
	}
    
    @Override
    protected void onDestroy(){
    	if (loadDataTask != null && loadDataTask.getStatus() != AsyncTask.Status.FINISHED)
    		loadDataTask.cancel(true);
    	
    	super.onDestroy();
    }

	@Override
	public void adWhirlGeneric()
	{
		// TODO Auto-generated method stub
		
	}
}
