package com.jumplife.ad;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlManager;
import com.adwhirl.AdWhirlTargeting;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.jumplife.movieinfo.R;

public class AdGenerator{
	private Activity activity;
	public static final String TAG = "AdGenerator";
	public boolean DEBUG = false;
	
	public AdGenerator(Activity activity) {
		this.activity = activity;
	}
	
	public void setAdmobAd() {
		if (DEBUG)
			Log.d(TAG, "Ad Start");
    	// Create the adView
    	Resources res = activity.getResources();
    	String admobKey = res.getString(R.string.admob_key);

        AdView adView = new AdView(activity, AdSize.BANNER, admobKey);

        // Lookup your LinearLayout assuming it's been given
        // the attribute android:id="@+id/mainLayout"
        RelativeLayout adLayout = (RelativeLayout)activity.findViewById(R.id.ad_layout);

        // Add the adView to it
        adLayout.addView(adView);

        // Initiate a generic request to load it with an ad
        adView.loadAd(new AdRequest());
        if (DEBUG)
        	Log.d(TAG, "Ad End");
	}
	
	public void setAdwhirlAd() {
		Resources res = activity.getResources();
    	String adwhirlKey = res.getString(R.string.adwhirl_key);
    	
    	RelativeLayout adLayout = (RelativeLayout)activity.findViewById(R.id.ad_layout);
    	
    	AdWhirlManager.setConfigExpireTimeout(1000 * 60); 
        //AdWhirlTargeting.setAge(23);
        //AdWhirlTargeting.setGender(AdWhirlTargeting.Gender.MALE);
        //AdWhirlTargeting.setKeywords("online games gaming");
        //AdWhirlTargeting.setPostalCode("94123");
        AdWhirlTargeting.setTestMode(false);
   		
        AdWhirlLayout adwhirlLayout = new AdWhirlLayout(activity, adwhirlKey);	
        
    	adwhirlLayout.setAdWhirlInterface((AdWhirlInterface)activity);
    	
    	adwhirlLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    	//adwhirlLayout.setLayoutParams();
    	
    	/*TextView ta  = (TextView) findViewById(R.layout.text_view);
       LayoutParams lp = new LayoutParams();
       lp.gravity= Gravity.CENTER_HORIZONTAL; 
       ta.setLayoutParams(lp);
    	 * 
    	 */

	 	
    	adLayout.addView(adwhirlLayout);
	}
}
