package com.jumplife.movieinfo;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.TrackedActivity;
import com.jumplife.movieinfo.promote.PromoteAPP;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class CheckActivity extends TrackedActivity {
	private RelativeLayout viewshow, amb, miamar, in, sbc, lie, westin, ez, dingok;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check); 
        findView();
        setListListener();
    }
	
	private void findView(){
		viewshow = (RelativeLayout)findViewById(R.id.relative_viewshow); 
		amb = (RelativeLayout)findViewById(R.id.relative_amb); 
		miamar = (RelativeLayout)findViewById(R.id.relative_miramar); 
		in = (RelativeLayout)findViewById(R.id.relative_in); 
		sbc = (RelativeLayout)findViewById(R.id.relative_sbc);
		lie = (RelativeLayout)findViewById(R.id.relative_lie); 
		westin = (RelativeLayout)findViewById(R.id.relative_westin);
		ez = (RelativeLayout)findViewById(R.id.relative_ez);
		dingok  = (RelativeLayout)findViewById(R.id.relative_dingok);
	}
	
	private void setListListener() {
		viewshow.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "威秀", (long)0);

				Uri uri = Uri.parse("http://www.vscinemas.com.tw/Mobile/SelectSession.aspx");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
		amb.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "國濱", (long)0);

				Uri uri = Uri.parse("http://booking.ambassador.com.tw/");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
		miamar.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "美麗華", (long)0);
				
				Uri uri = Uri.parse("http://goo.gl/RmuRW");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
		in.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "豪華", (long)0);
				
				Uri uri = Uri.parse("http://www.in89.com.tw/booking_00.php");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
		sbc.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "星橋國際", (long)0);
			
				Uri uri = Uri.parse("http://www.sbc-cinemas.com.tw/");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
		lie.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "美奇萊", (long)0);
				
				Uri uri = Uri.parse("http://maichilai.ehosting.com.tw/login.aspx");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
		westin.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "大千", (long)0);
				
				Uri uri = Uri.parse("http://www.westin.com.tw/03_onl_movie.asp");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
		ez.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "EZ訂", (long)0);
				
				Uri uri = Uri.parse("http://www.ezding.com.tw/yahoo/mmb.do");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
		dingok.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				EasyTracker.getTracker().trackEvent("電影訂票", "戲院名稱", "一訂OK", (long)0);
				
				Uri uri = Uri.parse("http://m.dingok.com/");  
	    		Intent it = new Intent(Intent.ACTION_VIEW, uri);
	    		startActivity(it);
			}			
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN){
			
			PromoteAPP promoteAPP = new PromoteAPP(CheckActivity.this);
        	if(!promoteAPP.isPromote) {
				new AlertDialog.Builder(this)
					.setTitle("- 離開程式? -")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						// do something when the button is clicked
						public void onClick(DialogInterface arg0, int arg1) {
							CheckActivity.this.finish();
						}
					})
					.setNegativeButton("否", null)
					.show();
        	} else
		    	promoteAPP.promoteAPPExe();
						
			return true;
	    }		
		
		return super.onKeyDown(keyCode, event);
	}
	
    @Override
	protected void onStart() {
	    super.onStart();
	}
    
	@Override
	protected void onStop() {
	    super.onStop();
    }
}
