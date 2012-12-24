package com.jumplife.movieinfo;

import java.util.HashMap;


import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedTabActivity;
import com.jumplife.imageload.ImageLoader;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MovieTabActivities extends TrackedTabActivity implements OnTabChangeListener {
	
	private TabHost tabHost;
	private TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	private TextView topbar_text;
	private SharePreferenceIO sharepre;	
	private LinearLayout topbarLayout;
	private int openCount;
	private int version;
	
	private AdView adView;
	
	public static String TAG = "MovieTabActivities";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        long startTime = System.currentTimeMillis();
        topbarLayout = (LinearLayout) findViewById(R.id.topbar);
        
        topbar_text = (TextView)findViewById(R.id.topbar_text);
        topbar_text.setText("電影資訊");
        
        tabHost = getTabHost();  // The activity TabHost
        tabHost.setup();
               
        tabInfoTable();
        tabTheater();
        tabCheck();
        tabTV();
        tabNews();
        
        setTabClickLog();
        
        Bundle bundle = this.getIntent().getExtras();
        if(bundle == null)
        	tabHost.setCurrentTab(0);
        
        topbarLayout.setVisibility(View.VISIBLE);		
		tabHost.setOnTabChangedListener(this);
        sharepre = new SharePreferenceIO(MovieTabActivities.this);
        openCount = sharepre.SharePreferenceO("opencount", 0);
        version = sharepre.SharePreferenceO("version", 0);
        if(openCount > 5) {
        	LoadPromoteTask loadPromoteTask = new LoadPromoteTask();
        	loadPromoteTask.execute();
        	openCount = 0;
        }
        openCount += 1;
    	sharepre.SharePreferenceI("opencount", openCount);
    	long endTime = System.currentTimeMillis();
    	Log.e(TAG, "sample method took %%%%%%%%%%%%%%%%%%%%%%%%%%%%"+(endTime-startTime)+"ms");
    	
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
    	if (adView != null) {
    	  adView.destroy();
    	}
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
    
    public void setAd() {
    	Log.d("Ben", "Ad Start");
    	// Create the adView
    	Resources res = getResources();
    	String admobKey = res.getString(R.string.admob_key);

        adView = new AdView(this, AdSize.BANNER, admobKey);

        // Lookup your LinearLayout assuming it's been given
        // the attribute android:id="@+id/mainLayout"
        LinearLayout layout = (LinearLayout)findViewById(R.id.ad_linearlayout);

        // Add the adView to it
        layout.addView(adView);

        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
        Log.d("Ben", "Ad End");
    }
    
    private void tabInfoTable() {
    	View ActivitysTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
    	TextView ActivitysTabLabel = (TextView) ActivitysTab.findViewById(R.id.textview_tabicon);
		ActivitysTabLabel.setText("電影資訊");
		
		// Create an Intent to launch an Activity for the tab (to be reused)
		// Initialize a TabSpec for each tab and add it to the TabHost
        Intent intentFirstRound = new Intent().setClass(this, MovieInfoTable.class);
        intentFirstRound.putExtra("movie_flag", 1);
        spec = tabHost.newTabSpec("tab1")
        				.setIndicator(ActivitysTab)
        				.setContent(intentFirstRound);
        tabHost.addTab(spec);
    }
    
    private void tabTheater() {
    	View MyListTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
    	TextView MyListTabLabel = (TextView) MyListTab.findViewById(R.id.textview_tabicon);
        MyListTabLabel.setText("電影院");
        
        Intent intentTheater = new Intent().setClass(this, AreasActivity.class);
        intentTheater.putExtra("theaterType", 0);
        spec = tabHost.newTabSpec("tab2")
        				.setIndicator(MyListTab)
        				.setContent(intentTheater);
        tabHost.addTab(spec);
    }
    
    private void tabCheck() {
    	View CreateTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
    	TextView CreateTabLabel = (TextView) CreateTab.findViewById(R.id.textview_tabicon);
        CreateTabLabel.setText("電影訂票");
        
        Intent intentNewRound = new Intent().setClass(this, CheckActivity.class);
        spec = tabHost.newTabSpec("tab3")
                		.setIndicator(CreateTab)
                		.setContent(intentNewRound);
        tabHost.addTab(spec);
    }

    private void tabTV() {
    	View ActivitysTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
    	TextView ActivitysTabLabel = (TextView) ActivitysTab.findViewById(R.id.textview_tabicon);
		ActivitysTabLabel.setText("電視時刻");
    	        
        Intent intentList = new Intent().setClass(this, TvListActivity.class);
		spec = tabHost.newTabSpec("tab4")
        				.setIndicator(ActivitysTab)
        				.setContent(intentList);
        tabHost.addTab(spec);
    }
    
    private void tabNews(){
    	View ActivitysTab = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
    	TextView ActivitysTabLabel = (TextView) ActivitysTab.findViewById(R.id.textview_tabicon);
		ActivitysTabLabel.setText("關於我們");
    	        
		Intent intentList = new Intent().setClass(this, AboutUsActivity.class);
        spec = tabHost.newTabSpec("tab5")
        				.setIndicator(ActivitysTab)
        				.setContent(intentList);
        tabHost.addTab(spec);
    }
    
    public void onTabChanged(String tabId) {
		if(tabId == "tab1") {
			topbar_text.setText("電影資訊");
		} else if (tabId == "tab2") {
			topbar_text.setText("電影院");
		} else if (tabId == "tab3") { 
			topbar_text.setText("電影訂票");
		} else if (tabId == "tab4") {
			topbar_text.setText("電視時刻");
		} else if(tabId == "tab5") {
			topbar_text.setText("關於我們");
		}
	}
    
    public void setTabClickLog() {
    	tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				if(tabId.equalsIgnoreCase("tab1")) {
					EasyTracker.getTracker().trackEvent("主選單", "點擊", "電影資訊", (long)0);
				}
				else if(tabId.equalsIgnoreCase("tab2")) {
					EasyTracker.getTracker().trackEvent("主選單", "點擊", "電影院", (long)0);
				}
				else if(tabId.equalsIgnoreCase("tab3")) {
					EasyTracker.getTracker().trackEvent("主選單", "點擊", "電影訂票", (long)0);
				}
				else if(tabId.equalsIgnoreCase("tab4")) {
					EasyTracker.getTracker().trackEvent("主選單", "點擊", "電視時刻", (long)0);
				}
				else if(tabId.equalsIgnoreCase("tab5")) {
					EasyTracker.getTracker().trackEvent("主選單", "點擊", "關於我們", (long)0);
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
			 setAd();
			 super.onPostExecute(result);

		 }
    	
    }
	
	class LoadPromoteTask extends AsyncTask<Integer, Integer, String>{  
        
		private String[] promotion = new String[5];
        private ProgressDialog progressdialogInit;
        private AlertDialog dialogPromotion;
        
        private OnCancelListener cancelListener = new OnCancelListener(){
		    public void onCancel(DialogInterface arg0){
		    	LoadPromoteTask.this.cancel(true);
		    }
    	};

    	@Override  
        protected void onPreExecute() {
    		progressdialogInit= new ProgressDialog(MovieTabActivities.this);
        	progressdialogInit.setTitle("Load");
        	progressdialogInit.setMessage("Loading…");
        	progressdialogInit.setOnCancelListener(cancelListener);
        	progressdialogInit.setCanceledOnTouchOutside(false);
        	if(progressdialogInit != null && !progressdialogInit.isShowing())
        		progressdialogInit.show();
			super.onPreExecute();  
        }  
    	
		@Override  
        protected String doInBackground(Integer... params) {
			MovieAPI movieAPI = new MovieAPI();
			promotion = movieAPI.getPromotion();
			return "progress end";  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	progressdialogInit.dismiss();
        	if(promotion != null && !promotion[1].equals("null") && Integer.valueOf(promotion[4]) > version) {
	        	View viewPromotion;
	            LayoutInflater factory = LayoutInflater.from(MovieTabActivities.this);
	            viewPromotion = factory.inflate(R.layout.dialog_promotion,null);
	            dialogPromotion = new AlertDialog.Builder(MovieTabActivities.this).create();
	            dialogPromotion.setView(viewPromotion);
	            ImageView imageView = (ImageView)viewPromotion.findViewById(R.id.imageView1);
	            TextView textviewTitle = (TextView)viewPromotion.findViewById(R.id.textView1);
	            TextView textviewDescription = (TextView)viewPromotion.findViewById(R.id.textView2);
	            ImageLoader imageLoader = new ImageLoader(MovieTabActivities.this);			
				if(!promotion[0].equals("null"))
					imageLoader.DisplayImage(promotion[0], imageView);
				else
					imageView.setVisibility(View.GONE);
				if(!promotion[2].equals("null"))
					textviewTitle.setText(promotion[2]);
				else
					textviewTitle.setVisibility(View.GONE);
				if(!promotion[3].equals("null"))
					textviewDescription.setText(promotion[3]);
				else
					textviewDescription.setVisibility(View.GONE);
	            dialogPromotion.setOnKeyListener(new OnKeyListener(){
	                public boolean onKey(DialogInterface dialog, int keyCode,
	                        KeyEvent event) {
	                	sharepre.SharePreferenceI("version", Integer.valueOf(promotion[4]));
	                    if(KeyEvent.KEYCODE_BACK==keyCode)
	                    	if(dialogPromotion != null && dialogPromotion.isShowing())
	                    		dialogPromotion.cancel();
	                    return false;
	                }
	            });
	            ((Button)viewPromotion.findViewById(R.id.button2))
	            .setOnClickListener(
	                new OnClickListener(){
	                    public void onClick(View v) {
	                        //取得文字方塊中的關鍵字字串
	                    	sharepre.SharePreferenceI("version", Integer.valueOf(promotion[4]));
	                    	if(dialogPromotion != null && dialogPromotion.isShowing())
	                    		dialogPromotion.cancel();
	                    	
	                    	HashMap<String, String> parameters = new HashMap<String, String>();
	                    	parameters.put("LINK", promotion[1]);
	    					
	                    	Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(promotion[1]));
	                    	MovieTabActivities.this.startActivity(intent);
	                    }
	                }
	            );
	            dialogPromotion.setCanceledOnTouchOutside(false);
	            dialogPromotion.show();
        	}
	       	super.onPostExecute(result);  
        }  
          
    }
}