package com.jumplife.movieinfo;

import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedTabActivity;
import com.jumplife.ad.AdGenerator;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MovieInfoTabActivities extends TrackedTabActivity implements OnTabChangeListener, AdWhirlInterface {
	
	private TabHost tabHost;
	private TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	private TextView topbar_text;
	private TextView textViewFeedback;
    private LinearLayout topbarLayout;
	private int movie_id;
	private int theater_id;
	private AdView adView;
	
	public static String TAG = "MovieInfoTabActivities";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_tab);
        
        Bundle extras = getIntent().getExtras();
        movie_id = extras.getInt("movie_id");
        theater_id = extras.getInt("theater_id");
        final String movie_name = extras.getString("movie_name");
        
        topbarLayout = (LinearLayout) findViewById(R.id.topbar);
        textViewFeedback = (TextView)findViewById(R.id.textview_feedback);
        textViewFeedback.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				Uri uri = Uri.parse("mailto:jumplives@gmail.com");  
				String[] ccs={"abooyaya@gmail.com, chunyuko85@gmail.com, raywu07@gmail.com, supermfb@gmail.com, form.follow.fish@gmail.com"};
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				it.putExtra(Intent.EXTRA_CC, ccs); 
				it.putExtra(Intent.EXTRA_SUBJECT, "[電影時刻表] 建議回饋(電影 : " + movie_name + ")");
				it.putExtra(Intent.EXTRA_TEXT, "請簡述你所遇到的問題 : ");
				startActivity(it);  
			}			
		});
        
        topbar_text = (TextView)findViewById(R.id.topbar_text);
        topbar_text.setText("電影資訊");
        
        tabHost = getTabHost();  // The activity TabHost
        tabHost.setup();
               
        tabInfoTable();
        tabInfoContent();
        tabEvaluate();
        tabInfoSchedule();
        
        setTabClickLog();
        
        Bundle bundle = this.getIntent().getExtras();
        if(bundle == null)
        	tabHost.setCurrentTab(0);
        
        topbarLayout.setVisibility(View.VISIBLE);		
		//tabHost.setOnTabChangedListener(this);
        new SharePreferenceIO(MovieInfoTabActivities.this);
    	
    	AdTask adTask = new AdTask();
        adTask.execute();
    }
    
    @Override
    public void onResume(){
		super.onResume();
		Bundle bundle = this.getIntent().getExtras();
        if(bundle != null)
        	tabHost.setCurrentTab(bundle.getInt("tabNo"));
	}
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	Log.d(TAG, "onActivityResult");
    }
     
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN){
			
			return true;
	    } else
	    	return super.onKeyDown(keyCode, event);
	}
    
    private void tabInfoTable() {
    	View TableTab = LayoutInflater.from(this).inflate(R.layout.info_tab, null);
    	TextView ActivitysTabLabel = (TextView) TableTab.findViewById(R.id.textview_tabicon);
		ActivitysTabLabel.setText("電影簡介");
		ImageView image = (ImageView) TableTab.findViewById(R.id.imageview_tabicon);
		image.setImageResource(R.drawable.tab_info_imageview_intro);
		
		// Create an Intent to launch an Activity for the tab (to be reused)
		// Initialize a TabSpec for each tab and add it to the TabHost
        Intent intentTable = new Intent().setClass(this, MovieInfoActivity.class);
        intentTable.putExtra("movie_id", movie_id);
        intentTable.putExtra("theater_id", theater_id);
        spec = tabHost.newTabSpec("tab1")
        				.setIndicator(TableTab)
        				.setContent(intentTable);
        tabHost.addTab(spec);
    }
    
    private void tabInfoContent() {
    	View ContentTab = LayoutInflater.from(this).inflate(R.layout.info_tab, null);
    	TextView MyListTabLabel = (TextView) ContentTab.findViewById(R.id.textview_tabicon);
        MyListTabLabel.setText("劇情介紹");
        ImageView image = (ImageView) ContentTab.findViewById(R.id.imageview_tabicon);
        image.setImageResource(R.drawable.tab_info_imageview_story);
		
        Intent intentContent = new Intent().setClass(this, MovieIntroContentActivity.class);
        intentContent.putExtra("movie_id", movie_id);
        //intentContent.putExtra("theater_id", theater_id);
        spec = tabHost.newTabSpec("tab2")
        				.setIndicator(ContentTab)
        				.setContent(intentContent);
        tabHost.addTab(spec);
    }
    
    private void tabEvaluate() {
    	View ContentTab = LayoutInflater.from(this).inflate(R.layout.info_tab, null);
    	TextView MyListTabLabel = (TextView) ContentTab.findViewById(R.id.textview_tabicon);
        MyListTabLabel.setText("電影短評");
        ImageView image = (ImageView) ContentTab.findViewById(R.id.imageview_tabicon);
        image.setImageResource(R.drawable.tab_info_imageview_comment);
		
        Intent intentContent = new Intent().setClass(this, MovieEvaluateActivity.class);
        intentContent.putExtra("movie_id", movie_id);
        spec = tabHost.newTabSpec("tab3")
        				.setIndicator(ContentTab)
        				.setContent(intentContent);
        tabHost.addTab(spec);
    }
    
    private void tabInfoSchedule() {
    	View ScheduleTab = LayoutInflater.from(this).inflate(R.layout.info_tab, null);
    	TextView CreateTabLabel = (TextView) ScheduleTab.findViewById(R.id.textview_tabicon);
        CreateTabLabel.setText("時刻表");
        ImageView image = (ImageView) ScheduleTab.findViewById(R.id.imageview_tabicon);
        image.setImageResource(R.drawable.tab_info_imageview_video);
		
        Intent intentSchedule = new Intent().setClass(this, MovieScheduleActivity.class);
        intentSchedule.putExtra("movie_id", movie_id);
        intentSchedule.putExtra("theater_id", theater_id);
        spec = tabHost.newTabSpec("tab3")
                		.setIndicator(ScheduleTab)
                		.setContent(intentSchedule);
        tabHost.addTab(spec);
    }
   
    public void onTabChanged(String tabId) {
		if(tabId == "tab1") {
			topbar_text.setText("電影資訊");
		} else if (tabId == "tab2") {
			topbar_text.setText("電影資訊");
		} else if (tabId == "tab3") { 
			topbar_text.setText("電影資訊");
		} else if (tabId == "tab4") { 
			topbar_text.setText("電影資訊");
		} 
	}
    
    public void setTabClickLog() {
    	tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if(tabId.equalsIgnoreCase("tab1")) {
					Log.d("Ben", "電影簡介");
					EasyTracker.getTracker().trackEvent("電影資訊tab", "點擊", "電影簡介", (long)0);
				}
				else if(tabId.equalsIgnoreCase("tab2")) {
					EasyTracker.getTracker().trackEvent("電影資訊tab", "點擊", "劇情介紹", (long)0);
					Log.d("Ben", "劇情介紹");
				}
				else if(tabId.equalsIgnoreCase("tab3")) {
					Log.d("Ben", "電影短評");
					EasyTracker.getTracker().trackEvent("電影資訊tab", "點擊", "電影短評", (long)0);
				}
				else if(tabId.equalsIgnoreCase("tab4")) {
					Log.d("Ben", "時刻表");
					EasyTracker.getTracker().trackEvent("電影資訊tab", "點擊", "時刻表", (long)0);
				}
			}
		});
    }
        
    class AdTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			
			return null;
		}
		
		 @Override  
	     protected void onPostExecute(String result) {
			 //setAd();
			 AdGenerator adGenerator = new AdGenerator(MovieInfoTabActivities.this);
			//adGenerator.setAdmobAd();
			 adGenerator.setAdwhirlAd();
			 super.onPostExecute(result);
		 }
    }

	@Override
	public void adWhirlGeneric()
	{
		// TODO Auto-generated method stub
		
	}
}