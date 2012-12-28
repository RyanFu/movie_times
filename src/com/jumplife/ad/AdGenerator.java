package com.jumplife.ad;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.jumplife.movieinfo.R;

public class AdGenerator {
	private Activity activity;
	public static final String TAG = "ÄdGenerator";
	public boolean DEBUG = false;
	
	public AdGenerator(Activity activity) {
		this.activity = activity;
	}
	
	public void setAd() {
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
}
