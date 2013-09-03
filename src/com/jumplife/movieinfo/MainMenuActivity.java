package com.jumplife.movieinfo;


import com.actionbarsherlock.app.ActionBar;
import com.jumplife.fragment.MenuFragement;
import com.jumplife.fragment.MovieTimeListFragment;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;

public class MainMenuActivity extends SlidingFragmentActivity {

	private static SlidingMenu menu;
	private int typeId;
	
	public final static int 
	FLAG_ONEROUND = 1,
	FLAG_TWOROUND = 2,
	FLAG_COMING = 3,
	FLAG_WEEK = 4,
	
	FLAG_TVTIME = -1,
	FLAG_THEATER = -2,
	FLAG_FAVORITE = -3,
	FLAG_SETTING = -4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	    getSupportActionBar().setIcon(R.drawable.movietime);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		
		SharePreferenceIO spIO = new SharePreferenceIO(this);
		typeId = spIO.SharePreferenceO("type_Id", 1);
		
		setActionBarTitle(typeId);
		
		initView();
		initSlidingMenu(savedInstanceState);
		//initPromote();
	}
	
	private void initView() {
		
	}
	
	private void initSlidingMenu(Bundle savedInstanceState) {
		/*
		 * Init SlidingMenu
		 */
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		
		menu = getSlidingMenu();
	    menu.setShadowWidth(screenWidth/4);
	    menu.setBehindOffset(screenWidth/2);
	    menu.setFadeDegree(0.35f);
	    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	    menu.setOnCloseListener(new OnCloseListener(){
			@Override
			public void onClose() {
				setActionBarTitle(typeId);
			}	    	
	    });
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	   
	    /*
		 *  set the Behind View
		 */
		setBehindContentView(R.layout.fragmentlayout_movietime_menu);
		MenuFragement menuFrag = new MenuFragement();
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
			t.replace(R.id.menu_frame, menuFrag);
			t.commit();
		} else {
			menuFrag = (MenuFragement)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame);
		}
		/*
		 *  set the Above View
		 */
				

		setContentView(R.layout.fragmentlayout_movietime_menu);
		
		if(typeId == FLAG_TVTIME) {/*
			TvtimeFragment tvTime = new TvtimeFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, tvTime)
			.commit();	*/		
		} else if(typeId == FLAG_THEATER) {/*
			TheaterFragment theater = new TheaterFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, theater)
			.commit();	*/		
		} else if(typeId == FLAG_FAVORITE) {/*
			MyFavoriteFragment myFavorite = new MyFavoriteFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, myFavorite)
			.commit();	*/		
		} else if(typeId == FLAG_SETTING) {/*
			SettingFragment setting = new SettingFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, setting)
			.commit();*/
		} else {
			MovieTimeListFragment tvchannels = MovieTimeListFragment.NewInstance(typeId); 
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.menu_frame, tvchannels)
			.commit();
		}
		
		//setSlidingActionBarEnabled(false);
		 
	}
	/*
	 * Sliding Menu Exchange Content Fragment
	 */
	public void switchContent(Fragment fragment, int type, boolean isAdd) {
		typeId = type;
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.menu_frame, fragment)
		.commit();
		menu.showContent();		
	}
	private void setActionBarTitle(int type) {
		switch(type) {
		case MenuFragement.FLAG_ONEROUND:
			getSupportActionBar().setTitle("首輪電影");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragement.FLAG_TWOROUND:
			getSupportActionBar().setTitle("二輪電影");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragement.FLAG_COMING:
			getSupportActionBar().setTitle("近期上映");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragement.FLAG_WEEK:
			getSupportActionBar().setTitle("本週新片");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragement.FLAG_TVTIME :
			getSupportActionBar().setTitle("電視時刻");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragement.FLAG_THEATER :
			getSupportActionBar().setTitle("戲院資訊");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragement.FLAG_FAVORITE :
			getSupportActionBar().setTitle("最愛戲院");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case MenuFragement.FLAG_SETTING :
			getSupportActionBar().setTitle("關於我們");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		
		default:
			getSupportActionBar().setTitle("");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		}
	}
}
