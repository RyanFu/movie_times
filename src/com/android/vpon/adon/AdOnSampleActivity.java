package com.android.vpon.adon;


import com.jumplife.movieinfo.R;
import com.vpon.adon.android.AdListener;

import com.vpon.adon.android.AdOnPlatform;
import com.vpon.adon.android.AdView;
import com.vpon.adon.android.VponDestroy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

public class AdOnSampleActivity extends Activity {
	/** 
	 * 3.2.5
	 * 因為AdView接口有做改變，需要輸入廣告寬高。
	 * 請修改 AdView(Context)，改成 AdView (Context, adWhidth, adHeight)
	 * 這個 adWidth, adHeight就是您的廣告的寬高，有支援固定的廣告Size，請到wiki.vpon.com查詢。
	 * 
	 * 取消base map，setLicenseKey請改成setLicenseKey(adOnKey, AdOnPlatform.TW, autoRefreshAd);
	 * 
	 * 新增permission
	 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	 * 
	 * 新增 VponDestroy.remove(this)，請加進Activity的onDestroy裡面，此方法是為了清memory。
	 * 
	 * 修正已知的Memory Leak(感謝發現leak的各位大大。)
	 * 
	 * 請修改android:configChanges="orientation|keyboardHidden|navigation|keyboard"
	 * 進<activity android:name="com.vpon.adon.android.webClientHandler.QRActivity" 
	 * 和<activity android:name="com.vpon.adon.android.webClientHandler.ShootActivity"
	 * 只有新增"orientation"進configChanges裡面。
	 * 
	 * 移除Apon，更精準投放目前已經做到adOn裡面。
	 * 
	 * 3.2.4 
     * 不抓imei 改用 macAddress(MD5) 或 OpenUDID
	 * 
	 * 3.2.1 和 3.0 有一些改變
	 * 
	 * 除了BUG Fixed以外，還多了一些東西
	 * 
	 * 新增permission
	 * 	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	 * 
	 * 多了一個可以設定 "底圖" 的設定，
	 * 如果AdView Layout Height的地方是用 wrap_content or -2 而不是固定的高度，請把 BaseMap = false
	 * 固定高度 請考慮設 BaseMap = true
	 * 
	 * QRcode的地方，改名了！ 從原本的com.google.zxing 改成 com.googleing.zxinging 
	 * (為了讓google開啓QRcode的時候，不會開到有Vpon廣告的app)
	 * 一切和google QRcode有關的命名 全都做了修改 ex: capture.xml, AndroidManifest.xml 等等
	 * 
	 * 
     * 3.0和2.0有一些改變 請至 wiki.vpon.com
     * 
     * 此為 adOn 版本的 sample code
     * 
     * 需要加入一些 code
     * AndroidManifest.xml (一些Activity)
     * res->Layout 請複製內容後貼上 (capture.xml)
     * res->values 請複製內容後貼上 (attrs.xml, colors.xml, ids.xml, strings.xml)
     * 
     * 請把QRcode source code加入 
     * 再修改 裡面和import R.java有關係的檔案
     * 
     * Apon分析工具 現在有區分 CN, TW 
     * 
     * 請修改adOnKey, 
     * 
     * */

	
	//請把QRcode source code加入(com.google.zxing) 
    //再修改 裡面和import R.java有關係的檔案
	//此sample code則把R.java改成 import com.android.vpon.adon.R;
	//com.android.vpon.adon為此sample code 的 main activity
	//有四個地方要改 1. CaptureActivity.java, 2.CaptureActivityHandler.java 3.DecodeHandler.java 4.ViewfinderView.java  
	
	private AdView adView;
	private RelativeLayout adonContainerView;
	private String adOnKey = "ChangeMe"; //adOn license key

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        //adonContainerView = (RelativeLayout)findViewById(R.id.adonContainerView);
		
        /*adView = new AdView(this, Width, Height); //1*/
		//如果對於填寫adWidth和adHeight有任何疑問，請到wiki.vpon.com查詢。
        //If there is any question about adWidth and adHeight, please check wiki.vpon.com.
        
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams (-2, -2);
        rl.addRule(RelativeLayout.CENTER_IN_PARENT);
        
        adonContainerView.addView(adView, rl);//2
		
		boolean autoRefreshAd = true; //3			
		
		adView.setLicenseKey(adOnKey, AdOnPlatform.CN , autoRefreshAd); //3

		AdListener adListener = new AdListener() { //4
			public void onRecevieAd(AdView adView) {
				//廣告抓取成功
				Log.i("AdOn", "OnRecevieAd");
				/*
				 * 廣告抓取成功時,我們建議您可以在這函式中替AdView增加一些簡單的動畫, 動畫範例程式碼如下 
				 */
				rotationHoriztion(0, 360, adView);
			}
			
			public void onFailedToRecevieAd(AdView adView) {
				//廣告抓取失敗
				Log.i("AdOn", "OnFailesToRecevieAd");
			}
		};
		
		adView.setAdListener(adListener); //5
		
		Log.i("AdOn SDK Version", adView.getVersion());//查詢目前SDK的版本
		
    }
    
    public void rotationHoriztion(int beganDegree, int endDegree, AdView view) {
		final float centerX = 320 / 2.0f;
		final float centerY = 48 / 2.0f;
		final float zDepth = -0.50f * view.getHeight();
	
		Rotate3dAnimation rotation = new Rotate3dAnimation(beganDegree, endDegree, centerX, centerY, zDepth, true);
		rotation.setDuration(1000);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}
	
			public void onAnimationEnd(Animation animation) {
			}
	
			public void onAnimationRepeat(Animation animation) {
			}
		});
		view.startAnimation(rotation);
	}
    
    protected void onDestroy() {
    	VponDestroy.remove(this);
    	super.onDestroy();
    }

}