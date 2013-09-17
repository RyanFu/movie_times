package com.jumplife.fragment;

import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;

import com.jumplife.movieinfo.NewsActivity;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.AppProject;
import com.jumplife.sqlite.SQLiteMovieInfoHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.ComponentName;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class SettingFragment extends Fragment {
	private View 			 fragmentView;    
    private FragmentActivity mFragmentActivity;
    
    private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	private LoadDataTask loadtask;
	private ArrayList<AppProject> appProject;
	
	private LinearLayout llAboutUs;
	private LinearLayout  llItem;
	private TextView tvItem ;
	private ImageView ivItem ;
	private ProgressBar pbInit;
	
	
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_setting, container, false);
        
		initView();
        loadtask = new LoadDataTask();
	    if(Build.VERSION.SDK_INT < 11)
	    	loadtask.execute();
        else
        	loadtask.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);
	    
		return fragmentView;
	}
    @Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
    private void initView() {
    	options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub)
		.showImageForEmptyUri(R.drawable.stub)
		.showImageOnFail(R.drawable.stub)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.cacheOnDisc()
		.cacheInMemory()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		pbInit = (ProgressBar)fragmentView.findViewById(R.id.pb_about_us);
		llAboutUs = (LinearLayout)fragmentView.findViewById(R.id.ll_aboutus);
		
		initBasicView();
	
    }
    @SuppressLint("InlinedApi")
	private void initBasicView() {
    	
    	
    	LinearLayout llFeed = new LinearLayout(mFragmentActivity);
    	LinearLayout llClear = new LinearLayout(mFragmentActivity);
    	LinearLayout llNews = new LinearLayout(mFragmentActivity);
    	LinearLayout llFacebook = new LinearLayout(mFragmentActivity);
    	
    	
    	llFeed = createItem(mFragmentActivity.getResources().getString(R.string.advice_and_feedback),R.drawable.feedback,"",0);
    	llClear = createItem(mFragmentActivity.getResources().getString(R.string.clear),R.drawable.delete,"",0);
    	llNews = createItem(mFragmentActivity.getResources().getString(R.string.entertainment_news),R.drawable.news,"",0);
    	llFacebook = createItem(mFragmentActivity.getResources().getString(R.string.facebook),R.drawable.facebook,"",0);
    	
    	llAboutUs.addView(llFeed);
    	llAboutUs.addView(createLine());
    	llAboutUs.addView(llClear);
    	llAboutUs.addView(createLine());
    	llAboutUs.addView(llNews);
    	llAboutUs.addView(createLine());
    	llAboutUs.addView(llFacebook);
    	llAboutUs.addView(createLine());
		
    	llFeed.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				
				Uri uri = Uri.parse("mailto:jumplives@gmail.com");  
				String[] ccs={"abooyaya@gmail.com, raywu07@gmail.com, supermfb@gmail.com, form.follow.fish@gmail.com"};
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				it.putExtra(Intent.EXTRA_CC, ccs); 
				it.putExtra(Intent.EXTRA_SUBJECT, "[電影時刻表] 建議回饋"); 
				startActivity(it);  
			}			
		});
		llNews.setOnClickListener(new OnClickListener(){
        	public void onClick(View arg0) {
        		
				Intent newAct = new Intent();
				newAct.setClass( mFragmentActivity, NewsActivity.class );
                startActivity( newAct );
			}				
		});
        llFacebook.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				
				Uri uri = Uri.parse("http://www.facebook.com/movietalked");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(it);
			}			
		});
		
		llClear.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				imageLoader.clearMemoryCache();
				imageLoader.clearDiscCache();
				Toast toast = Toast.makeText(mFragmentActivity, 
	            		mFragmentActivity.getResources().getString(R.string.clear_finish), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
			}			
		});
	}
    private View createLine() {
    	View vLine = new View(mFragmentActivity);
    	
    	vLine.setLayoutParams(
                new LinearLayout.LayoutParams(
                		LayoutParams.MATCH_PARENT, 
                		mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.setting_fragment_seperate_height)));
    	vLine.setBackgroundColor(mFragmentActivity.getResources().getColor(R.color.color_grey_b));
		return vLine;
	}
	private LinearLayout createItem(String tvText, int ivSrc ,String url ,int index) {
		// TODO Auto-generated method stub
    	/*setting item*/
		tvItem = new TextView(mFragmentActivity);
		ivItem = new ImageView(mFragmentActivity);
		llItem = new LinearLayout(mFragmentActivity);	
		
		tvItem.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.setting_fragment_itemmargin), 0, 0, 0);
		tvItem.setDuplicateParentStateEnabled(true);
		tvItem.setTextSize(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.setting_fragment_textsize));
		tvItem.setTextColor(mFragmentActivity.getResources().getColor(R.color.color_white_a));
		tvItem.setText(tvText);
		
		
		ivItem.setLayoutParams(
                new LinearLayout.LayoutParams(
                		mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.setting_fragment_imagesize), 
                		mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.setting_fragment_imagesize))); 
		
		ivItem.setPadding(mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.setting_fragment_itemmargin), 0, 0, 0);
		ivItem.setDuplicateParentStateEnabled(true);
		
		if( ivSrc==0){
			imageLoader.displayImage(appProject.get(index).getIconUrl(), ivItem, options);
		}else{
			ivItem.setImageResource(ivSrc);
		}
		LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				mFragmentActivity.getResources().getDimensionPixelSize(R.dimen.setting_fragment_itemsize));
		llItem.setOrientation(LinearLayout.HORIZONTAL);
		llItem.setGravity(Gravity.CENTER_VERTICAL);
		llItem.setPadding(mFragmentActivity.getResources()
				.getDimensionPixelSize(R.dimen.setting_fragment_itempadding), 0, 0, 0);
		llItem.setBackgroundResource(R.drawable.setting_item_bg);
		llItem.setLayoutParams(llParams);
		llItem.addView(ivItem);
		llItem.addView(tvItem);
		
		return llItem;
	}
	class LoadDataTask extends AsyncTask<Integer, Integer, String>{  
        
    	@Override  
        protected void onPreExecute() {
    		pbInit.setVisibility(View.VISIBLE);
    		super.onPreExecute();
        }  
          
        @Override  
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            return fetchData();  
        }  
  
        @Override  
        protected void onProgressUpdate(Integer... progress) {    
            super.onProgressUpdate(progress);  
        }  
  
        @Override  
        protected void onPostExecute(String result) {
        	pbInit.setVisibility(View.GONE);
        	if(appProject != null){
        		setView();		
    		}

	        super.onPostExecute(result);  
        }
    }

	private String fetchData() {

		SQLiteMovieInfoHelper instance = SQLiteMovieInfoHelper.getInstance(mFragmentActivity);
		SQLiteDatabase db = instance.getReadableDatabase();
		appProject = instance.getAppProjectList(db);
		db.close();
        instance.closeHelper();
        
		return "progress end";
	}
	private void setView(){
		for(int index = 0 ; index < appProject.size() ; index++ ){
			LinearLayout llProject = new LinearLayout(mFragmentActivity);
			
	    	llProject = createItem(appProject.get(index).getName(),0,appProject.get(index).getIconUrl(),index);
	    	
	    	llAboutUs.addView(llProject);
	    	llAboutUs.addView(createLine());
	    	llProject.setOnClickListener(new ItemButtonClick(index, mFragmentActivity));
			
		}  
	}
	class ItemButtonClick implements OnClickListener {
		private int position;
		
		ItemButtonClick(int pos, FragmentActivity mFragmentActivity) {
			position = pos;
			
	}

	public void onClick(View v) {
		PackageManager pm = mFragmentActivity.getPackageManager();
	    Intent appStartIntent = pm.getLaunchIntentForPackage(appProject.get(position).getPack());
	    if(null != appStartIntent) {
	    	appStartIntent.addCategory("android.intent.category.LAUNCHER");
	    	appStartIntent.setComponent(new ComponentName(appProject.get(position).getPack(),
	    			appProject.get(position).getClas()));
	    	appStartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	mFragmentActivity.startActivity(appStartIntent);
	    } 
	    else {
	    	startActivity(new Intent(Intent.ACTION_VIEW, 
		    		Uri.parse("market://details?id=" + appProject.get(position).getPack())));
	    }
	}
}
	
}
