package com.jumplife.fragment;

import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.MainMenuActivity;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;
import com.jumplife.fragment.MovieTimeListFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuFragement extends Fragment {
	private View fragmentView;
	private LinearLayout llMovieinfo,
		llOneround,
		llTworound,
		llComeing,
		llWeek,
		llTvtime,
		llTheater,
		llFavorite,
		llSetting;
			
	private View vOneround,
		vTworound,
		vComeing,
	 	vWeek;
	private TextView
		tvOneround,
		tvTworound,
		tvComeing,
		tvWeek,
		tvTvtime,
		tvTheater,
		tvFavorite,
		tvSetting;
	private ImageView
		ivTvtime,
		ivTheater,
		ivFavorite,
		ivSetting;
	public final static int 
		FLAG_ONEROUND = 1,
		FLAG_TWOROUND = 2,
		FLAG_COMING = 3,
		FLAG_WEEK = 4,
		
		FLAG_TVTIME = -1,
		FLAG_THEATER = -2,
		FLAG_FAVORITE = -3,
		FLAG_SETTING = -4;
		
	
	private int typeId;
	
	private FragmentActivity mFragmentActivity;
	private SharePreferenceIO spIO = new SharePreferenceIO(mFragmentActivity);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fragmentView = inflater.inflate(R.layout.fragment_menu, container, false);				
		initView();
		setListener();
		
		return fragmentView;
	}
	@Override
    public void onAttach(Activity activity) {
    	mFragmentActivity = getActivity();
        super.onAttach(activity);
    }
	private void initView() {
		//llOneround = (LinearLayout)fragmentView.findViewById(R.id.ll_oneround);
		llOneround = (LinearLayout)fragmentView.findViewById(R.id.ll_oneround);
		llTworound = (LinearLayout)fragmentView.findViewById(R.id.ll_tworound);
		llComeing = (LinearLayout)fragmentView.findViewById(R.id.ll_comeing);
		llWeek = (LinearLayout)fragmentView.findViewById(R.id.ll_week);
		llTvtime = (LinearLayout)fragmentView.findViewById(R.id.ll_tvtime);
		llTheater = (LinearLayout)fragmentView.findViewById(R.id.ll_theater);
		llFavorite = (LinearLayout)fragmentView.findViewById(R.id.ll_myfavorite);
		llSetting = (LinearLayout)fragmentView.findViewById(R.id.ll_setting);
		
		vOneround = (View)fragmentView.findViewById(R.id.v_oneround);
		vTworound = (View)fragmentView.findViewById(R.id.v_tworound);
		vComeing = (View)fragmentView.findViewById(R.id.v_comeing);
	 	vWeek = (View)fragmentView.findViewById(R.id.v_week);
		
	 	tvOneround = (TextView)fragmentView.findViewById(R.id.tv_oneround);
		tvTworound = (TextView)fragmentView.findViewById(R.id.tv_tworound);
		tvComeing = (TextView)fragmentView.findViewById(R.id.tv_comeing);
		tvWeek = (TextView)fragmentView.findViewById(R.id.tv_week);
		tvTvtime = (TextView)fragmentView.findViewById(R.id.tv_tvtime);
		tvTheater = (TextView)fragmentView.findViewById(R.id.tv_theater);
		tvFavorite = (TextView)fragmentView.findViewById(R.id.tv_myfavorite);
		tvSetting = (TextView)fragmentView.findViewById(R.id.tv_setting);
		
		ivTvtime = (ImageView)fragmentView.findViewById(R.id.iv_tvtime);
		ivTheater = (ImageView)fragmentView.findViewById(R.id.iv_theater);
		ivFavorite = (ImageView)fragmentView.findViewById(R.id.iv_myfavorite);
		ivSetting = (ImageView)fragmentView.findViewById(R.id.iv_setting);
		
		typeId = spIO.SharePreferenceO("typeId", 1);
		switchItemState();
	}
	private void switchItemState() {		
		setItemStateNormal();
		setItemStatePress();
	}
	private void setItemStateNormal() {
				
		tvOneround.setTextColor(getResources().getColor(R.color.color_white_a));
		tvTworound.setTextColor(getResources().getColor(R.color.color_white_a));
		tvComeing.setTextColor(getResources().getColor(R.color.color_white_a));
		tvWeek.setTextColor(getResources().getColor(R.color.color_white_a));
		tvTvtime.setTextColor(getResources().getColor(R.color.color_white_a));
		tvTheater.setTextColor(getResources().getColor(R.color.color_white_a));
		tvFavorite.setTextColor(getResources().getColor(R.color.color_white_a));
		tvSetting.setTextColor(getResources().getColor(R.color.color_white_a));
		
		ivTvtime.setBackgroundResource(R.drawable.tvtime_normal);
		ivTheater.setBackgroundResource(R.drawable.theater_normal);
		ivFavorite.setBackgroundResource(R.drawable.liketheater_normal);
		ivSetting.setBackgroundResource(R.drawable.aboutus_normal);
		
		vOneround.setVisibility(View.INVISIBLE);
		vTworound.setVisibility(View.INVISIBLE);
		vComeing.setVisibility(View.INVISIBLE);
		vWeek.setVisibility(View.INVISIBLE);
		
	}
	/*
	 *  FLAG_ONEROUND = 1,
		FLAG_TWOROUND = 2,
		FLAG_COMING = 3,
		FLAG_WEEK = 4,
		
		FLAG_TVTIME = -1,
		FLAG_THEATER = -2,
		FLAG_FAVORITE = -3,
		FLAG_SETTING = -4;
	 */
	private void setItemStatePress() {
		switch(typeId) {
		case FLAG_ONEROUND :
			tvOneround.setTextColor(getResources().getColor(R.color.color_yellow_d));
			vOneround.setVisibility(View.VISIBLE);
			break;
		case FLAG_TWOROUND :
			tvTworound.setTextColor(getResources().getColor(R.color.color_yellow_d));
			vTworound.setVisibility(View.VISIBLE);
			break;
		case FLAG_COMING :
			tvComeing.setTextColor(getResources().getColor(R.color.color_yellow_d));
			vComeing.setVisibility(View.VISIBLE);
			break;
		case FLAG_WEEK :
			tvWeek.setTextColor(getResources().getColor(R.color.color_yellow_d));
			vWeek.setVisibility(View.VISIBLE);
			break;
		case FLAG_TVTIME :
			tvTvtime.setTextColor(getResources().getColor(R.color.color_yellow_d));
			ivTvtime.setBackgroundResource(R.drawable.tvtime_press);
			break;
		case FLAG_THEATER :
			tvTheater.setTextColor(getResources().getColor(R.color.color_yellow_d));
			ivTheater.setBackgroundResource(R.drawable.theater_press);
			break;
		case FLAG_FAVORITE :
			tvFavorite.setTextColor(getResources().getColor(R.color.color_yellow_d));
			ivFavorite.setBackgroundResource(R.drawable.liketheater_press);
			break;
		case FLAG_SETTING :
			tvSetting.setTextColor(getResources().getColor(R.color.color_yellow_d));
			ivSetting.setBackgroundResource(R.drawable.aboutus_press);
			break;
		default:
			break;
		}
	}
	private void setListener() {
		
		llOneround.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_ONEROUND;
				switchItemState();
				spIO.SharePreferenceI("typeId", FLAG_ONEROUND);
				MovieTimeListFragment movieTypes = MovieTimeListFragment.NewInstance(FLAG_ONEROUND);
				switchFragment(movieTypes, false);
			}			
		});		
		llTworound.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_TWOROUND;
				switchItemState();
				spIO.SharePreferenceI("typeId", FLAG_TWOROUND);
				MovieTimeListFragment movieTypes = MovieTimeListFragment.NewInstance(FLAG_TWOROUND);
				switchFragment(movieTypes, false);
			}			
		});
		llComeing.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_COMING;
				switchItemState();
				spIO.SharePreferenceI("typeId", FLAG_COMING);
				MovieTimeListFragment movieTypes = MovieTimeListFragment.NewInstance(FLAG_COMING);
				switchFragment(movieTypes, false);
			}			 
		});		
		llWeek.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_WEEK;
				switchItemState();
				spIO.SharePreferenceI("typeId", FLAG_WEEK);
				MovieTimeListFragment movieTypes = MovieTimeListFragment.NewInstance(FLAG_WEEK);
				switchFragment(movieTypes, false);
			}			
		});
		/*llTvtime.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_TVTIME;
				switchItemState();
				spIO.SharePreferenceI("typeId", FLAG_TVTIME);
				TvtimeFragment tvTime = new TvtimeFragment();
				switchFragment(tvTime, true);
			}			
		 });		
		llTheater.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_THEATER;
				switchItemState();
				spIO.SharePreferenceI("typeId", FLAG_THEATER);
				TheaterFragment Theater = new TheaterFragment();
				switchFragment(Theater, true);
			}			
		});
		llFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_FAVORITE;
				switchItemState();
				spIO.SharePreferenceI("typeId", FLAG_FAVORITE);
				MyFavoriteFragment myFavorite = new MyFavoriteFragment();
				switchFragment(myFavorite, true);
			}			
		 });		
		 llSetting.setOnClickListener(new OnClickListener() {
			 public void onClick(View arg0) {
				typeId = FLAG_SETTING;
				switchItemState();
				spIO.SharePreferenceI("typeId", FLAG_SETTING);
				SettingFragment setting = new SettingFragment();
				switchFragment(setting, true);
			 }			
		 });
		*/
		 
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment, boolean isAdd) {
		if (mFragmentActivity == null)
			return;
		
		if (mFragmentActivity instanceof MainMenuActivity) {
			MainMenuActivity tvfa = (MainMenuActivity) getActivity();
			tvfa.switchContent(fragment, typeId, isAdd);
		} else 
			return;
	}

	
}
