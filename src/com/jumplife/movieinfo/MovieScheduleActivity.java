package com.jumplife.movieinfo;

import java.util.ArrayList;

import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.adapter.ScheduleAdapter;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Theater;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MovieScheduleActivity extends TrackedActivity {
	private ImageButton imageButtonRefresh;
	private ArrayList<Theater> theaterList;
	private ListView listviewSchedule;
	private Spinner spinnerLoc;
	private int movieId;
	private int theaterId;
	private ArrayAdapter<String> adapterLoc;
	private String[] location = {"台北東區", "台北西區", "台北南區", "台北北區",
								"新北市", "台北二輪", "基隆", "桃園", "中壢", "新竹", 
								"苗栗", "台中", "彰化", "雲林", "南投", "嘉義", "台南", 
								"高雄", "宜蘭", "花蓮", "台東", "屏東", "澎湖"}; 
	private int[] locationId = {56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 78, 76, 77};
	private String[] locationAct;
	private int[] locationIdAct;
	private ArrayList<String> locationLst = new ArrayList<String>(); 
	private ArrayList<Integer> locationIdLst = new ArrayList<Integer>();
	private ArrayList<Integer> locationIdLstOriginal = new ArrayList<Integer>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movieschedule);
        
        findViews();
        LoadDataTask loadTask = new LoadDataTask();
    	if(Build.VERSION.SDK_INT < 11)
        	loadTask.execute();
        else
        	loadTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
    }
	
	private void findViews(){
		listviewSchedule = (ListView)findViewById(R.id.listview_schedule);
		imageButtonRefresh = (ImageButton)findViewById(R.id.refresh);
		spinnerLoc = (Spinner)findViewById(R.id.spinner_location);
		Bundle extras = getIntent().getExtras();
		movieId = extras.getInt("movie_id");
		theaterId = extras.getInt("theater_id");
		
		for(int i=0; i<location.length; i++) 
			locationIdLstOriginal.add(locationId[i]);
		
		setListener();
	}
	
	private void FetchData(){
		MovieAPI movieAPI = new MovieAPI();
		if(theaterId != -1) 
			theaterList = movieAPI.getMovieTheaterTimeTableListWithHall(movieId, theaterId);
		else
			theaterList = movieAPI.getMovieTimeTableListWithHall(movieId);
	}
	
	private void setView(){
		if(theaterList != null) {
			listviewSchedule.setVisibility(View.VISIBLE);
			imageButtonRefresh.setVisibility(View.GONE);
		} else {
			listviewSchedule.setVisibility(View.GONE);
			imageButtonRefresh.setVisibility(View.VISIBLE);
		}
	}
	
	private void setListener() {
		imageButtonRefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Log.d("", "Click imageButtonRefresh");
				LoadDataTask loadTask = new LoadDataTask();
		    	if(Build.VERSION.SDK_INT < 11)
		        	loadTask.execute();
		        else
		        	loadTask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
			}			
		});
		spinnerLoc.setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
				setScheduleAdapter(position);
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(MovieScheduleActivity.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
			}
		});
	}  

	private void setListAdatper() {
		setSpinnerContent();
		adapterLoc = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationAct);
		adapterLoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		setScheduleAdapter(0);
		if(theaterId == -1) 
			spinnerLoc.setAdapter(adapterLoc);
		else
			spinnerLoc.setVisibility(View.GONE);
	}
	
	private void setScheduleAdapter(int position) {
		ArrayList<Theater> tmpList = new ArrayList<Theater>(10);
		for(int i=0; i<theaterList.size(); i++) {
			if(theaterId != -1) {
				tmpList = theaterList;
			} else {
				if(theaterList.get(i).getArea() == locationIdAct[position])				
					tmpList.add(theaterList.get(i));
			}
		}
		listviewSchedule.setAdapter(new ScheduleAdapter(MovieScheduleActivity.this, tmpList));
	}
	
	private void setSpinnerContent(){
		if(theaterId == -1) {
			if(theaterList.size() > 0) {
				for(int i=0; i<theaterList.size(); i++) {
					int index = locationIdLstOriginal.indexOf(theaterList.get(i).getArea());
					if(!locationIdLst.contains(locationId[index])) {
						locationLst.add(location[index]);
						locationIdLst.add(locationId[index]);
					}
				}
				locationAct = new String[locationLst.size()];
				locationIdAct = new int[locationIdLst.size()];
				for(int i=0; i<locationLst.size(); i++) {
					locationAct[i] = locationLst.get(i);
					locationIdAct[i] = locationIdLst.get(i);
				}
			} else {
				locationAct = new String[1];
				locationAct[0] = "本電影未提供時刻表";
			}
		} else {
			locationAct = location;
			locationIdAct = locationId;
		}
	}
	
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	private ProgressDialog progressdialogInit;
    	private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	LoadDataTask.this.cancel(true);
	    		listviewSchedule.setVisibility(View.GONE);
				imageButtonRefresh.setVisibility(View.VISIBLE);
		    }
    	};

    	@Override  
        protected void onPreExecute() {
    		progressdialogInit= new ProgressDialog(MovieScheduleActivity.this);
        	progressdialogInit.setTitle("Load");
        	progressdialogInit.setMessage("Loading…");
        	progressdialogInit.setOnCancelListener(cancelListener);
        	progressdialogInit.setCanceledOnTouchOutside(false);
        	progressdialogInit.show();
        	listviewSchedule.setVisibility(View.GONE);
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
        	setView();
        	if(progressdialogInit != null && progressdialogInit.isShowing())
        		progressdialogInit.dismiss();
        	if(theaterList != null){
        		setListAdatper();
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
}
