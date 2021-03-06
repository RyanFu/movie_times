package com.jumplife.fragment;

import com.jumplife.movieinfo.MovieInfoAppliccation;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.MainMenuActivity;

import com.jumplife.fragment.MovieTimeListFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuFragement extends Fragment {
	
	private View fragmentView;
	private LinearLayout llOneround,
						 llTworound,
						 llComeing,
						 llWeek,
						 llTheater,
						 llTvtime,
						 llFavorite,
						 llSetting;
			
	private View vOneround,
				 vTworound,
				 vComeing,
			 	 vWeek,
				 vTheater,
				 vTvtime,
				 vFavorite,
				 vSetting;
	 
	private TextView tvOneround,
					 tvTworound,
					 tvComeing,
					 tvWeek,
					 tvTheater,
					 tvTvtime,
					 tvFavorite,
					 tvSetting;
					 
	private ImageView ivOneround,
	 				  ivTworound,
	 				  ivComeing,
	 				  ivWeek,
	 				  ivTvtime,
					  ivTheater,
					  ivFavorite,
					  ivSetting;
	
	public final static int 
		FLAG_ONEROUND = 1,
		FLAG_WEEK =2,
		FLAG_COMING = 3,
		FLAG_TWOROUND = 4,
		
		FLAG_THEATER = -1,
		FLAG_TVTIME = -2,
		FLAG_FAVORITE = -3,
		FLAG_SETTING = -4;
		
	
	private int typeId;
	
	private FragmentActivity mFragmentActivity;
	
	
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
		//llMovieinfo = (LinearLayout)fragmentView.findViewById(R.id.ll_movieinfo);
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
	 	vTvtime = (View)fragmentView.findViewById(R.id.v_tvtime);
	 	vTheater = (View)fragmentView.findViewById(R.id.v_theater);
	 	vFavorite = (View)fragmentView.findViewById(R.id.v_myfavorite);
	 	vSetting = (View)fragmentView.findViewById(R.id.v_setting);
	 	
	 	tvOneround = (TextView)fragmentView.findViewById(R.id.tv_oneround);
		tvTworound = (TextView)fragmentView.findViewById(R.id.tv_tworound);
		tvComeing = (TextView)fragmentView.findViewById(R.id.tv_comeing);
		tvWeek = (TextView)fragmentView.findViewById(R.id.tv_week);
		tvTvtime = (TextView)fragmentView.findViewById(R.id.tv_tvtime);
		tvTheater = (TextView)fragmentView.findViewById(R.id.tv_theater);
		tvFavorite = (TextView)fragmentView.findViewById(R.id.tv_myfavorite);
		tvSetting = (TextView)fragmentView.findViewById(R.id.tv_setting);
		
		ivOneround = (ImageView)fragmentView.findViewById(R.id.iv_oneround);
		ivTworound = (ImageView)fragmentView.findViewById(R.id.iv_tworound);
		ivComeing = (ImageView)fragmentView.findViewById(R.id.iv_coming);
		ivWeek = (ImageView)fragmentView.findViewById(R.id.iv_week);
		ivTvtime = (ImageView)fragmentView.findViewById(R.id.iv_tvtime);
		ivTheater = (ImageView)fragmentView.findViewById(R.id.iv_theater);
		ivFavorite = (ImageView)fragmentView.findViewById(R.id.iv_myfavorite);
		ivSetting = (ImageView)fragmentView.findViewById(R.id.iv_setting);
		
		typeId = MovieInfoAppliccation.shIO.getInt("typeId", 1);
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
		
		
		ivOneround.setBackgroundResource(R.drawable.theater_normal);
		ivTworound.setBackgroundResource(R.drawable.theater_normal);
		ivComeing.setBackgroundResource(R.drawable.theater_normal);
		ivWeek.setBackgroundResource(R.drawable.theater_normal);
		
		ivTvtime.setBackgroundResource(R.drawable.tvtime_normal);
		ivTheater.setBackgroundResource(R.drawable.theater_normal);
		ivFavorite.setBackgroundResource(R.drawable.liketheater_normal);
		ivSetting.setBackgroundResource(R.drawable.aboutus_normal);
		
		vOneround.setVisibility(View.INVISIBLE);
		vTworound.setVisibility(View.INVISIBLE);
		vComeing.setVisibility(View.INVISIBLE);
		vWeek.setVisibility(View.INVISIBLE);
		vTvtime.setVisibility(View.INVISIBLE);
		vTheater.setVisibility(View.INVISIBLE);
		vFavorite.setVisibility(View.INVISIBLE);
		vSetting.setVisibility(View.INVISIBLE);
		
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
			ivOneround.setBackgroundResource(R.drawable.theater_press);
			break;
		case FLAG_TWOROUND :
			tvTworound.setTextColor(getResources().getColor(R.color.color_yellow_d));
			vTworound.setVisibility(View.VISIBLE);
			ivTworound.setBackgroundResource(R.drawable.theater_press);
			break;
		case FLAG_COMING :
			tvComeing.setTextColor(getResources().getColor(R.color.color_yellow_d));
			vComeing.setVisibility(View.VISIBLE);
			ivComeing.setBackgroundResource(R.drawable.theater_press);
			break;
		case FLAG_WEEK :
			tvWeek.setTextColor(getResources().getColor(R.color.color_yellow_d));
			vWeek.setVisibility(View.VISIBLE);
			ivWeek.setBackgroundResource(R.drawable.theater_press);
			break;
		case FLAG_TVTIME :
			vTvtime.setVisibility(View.VISIBLE);
			tvTvtime.setTextColor(getResources().getColor(R.color.color_yellow_d));
			ivTvtime.setBackgroundResource(R.drawable.tvtime_press);
			break;
		case FLAG_THEATER :
			vTheater.setVisibility(View.VISIBLE);
			tvTheater.setTextColor(getResources().getColor(R.color.color_yellow_d));
			ivTheater.setBackgroundResource(R.drawable.theater_press);
			break;
		case FLAG_FAVORITE :
			vFavorite.setVisibility(View.VISIBLE);
			tvFavorite.setTextColor(getResources().getColor(R.color.color_yellow_d));
			ivFavorite.setBackgroundResource(R.drawable.liketheater_press);
			break;
		case FLAG_SETTING :
			vSetting.setVisibility(View.VISIBLE);
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
				
				typeId = 1;
				switchItemState();
				MovieInfoAppliccation.shIO.edit().putInt("typeId",typeId).commit();
				MovieTimeListFragment movieTypes = MovieTimeListFragment.NewInstance(typeId);
				switchFragment(movieTypes, false);
			}			
		});		
		llTworound.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
			
				typeId = 4;
				switchItemState();
				MovieInfoAppliccation.shIO.edit().putInt("typeId", typeId).commit();
				MovieTimeListFragment movieTypes = MovieTimeListFragment.NewInstance(typeId);
				switchFragment(movieTypes, false);
			}			
		});
		llComeing.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				
				typeId = 3;
				switchItemState();
				MovieInfoAppliccation.shIO.edit().putInt("typeId", typeId).commit();
				MovieTimeListFragment movieTypes = MovieTimeListFragment.NewInstance(typeId);
				switchFragment(movieTypes, false);
			}			 
		});		
		llWeek.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = 2;
				switchItemState();
				MovieInfoAppliccation.shIO.edit().putInt("typeId", typeId).commit();
				MovieTimeListFragment movieTypes = MovieTimeListFragment.NewInstance(typeId);
				switchFragment(movieTypes, false);
			}			
		});
		llTvtime.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_TVTIME;
				switchItemState();
				MovieInfoAppliccation.shIO.edit().putInt("typeId", FLAG_TVTIME).commit();
				TvtimeFragment tvTime = new TvtimeFragment();
				switchFragment(tvTime, true);
			}			
		 });		
		llTheater.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_THEATER;
				switchItemState();
				MovieInfoAppliccation.shIO.edit().putInt("typeId", FLAG_THEATER).commit();
				TheaterFragment Theater = new TheaterFragment();
				switchFragment(Theater, true);
			}			
		});
		llFavorite.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				typeId = FLAG_FAVORITE;
				switchItemState();
				MovieInfoAppliccation.shIO.edit().putInt("typeId", FLAG_FAVORITE).commit();
				MyFavoriteFragment myFavorite = new MyFavoriteFragment();
				switchFragment(myFavorite, true);
			}			
		 });		
		 llSetting.setOnClickListener(new OnClickListener() {
			 public void onClick(View arg0) {
				typeId = FLAG_SETTING;
				switchItemState();
				MovieInfoAppliccation.shIO.edit().putInt("typeId", FLAG_SETTING).commit();
				SettingFragment setting = new SettingFragment();
				switchFragment(setting, true);
				
			 }			
		 });
		
		 
	}

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
