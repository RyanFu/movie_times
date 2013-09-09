package com.jumplife.adapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.jumplife.movieinfo.MovieInfoAppliccation;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Movie;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MovieListAdapter extends BaseAdapter {
	
	Activity activity;
    private ArrayList<Movie> movies;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private int mWidth;
	private int mHeight;
	private int typeId;
	
	
	public MovieListAdapter(Activity mActivity, ArrayList<Movie> movieList ,int mtypeId){
		this.movies = movieList;
		this.activity = mActivity;
		this.typeId = mtypeId;
			
		DisplayMetrics displayMetrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		mWidth = screenWidth;
		mHeight = screenWidth *3 / 8;
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.stub)
		.showImageForEmptyUri(R.drawable.stub)
		.showImageOnFail(R.drawable.stub)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.cacheOnDisc()
		.cacheInMemory()
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}

	public int getCount() {
		
		return movies.size();
	}

	public Object getItem(int position) {

		return movies.get(position);
	}

	public long getItemId(int position) {
	
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater myInflater = LayoutInflater.from(activity);
		View converView = myInflater.inflate(R.layout.fragment_movietime_listitem, null);
		RelativeLayout rlMovieListItem = (RelativeLayout)converView.findViewById(R.id.rl_movie_list_item);
		RelativeLayout rlPoster = (RelativeLayout)converView.findViewById(R.id.rl_poster);
		RelativeLayout rlIntro = (RelativeLayout)converView.findViewById(R.id.rl_names_date);
		RelativeLayout rlRankLabel = (RelativeLayout)converView.findViewById(R.id.rl_rank_label);
		TextView tvRankNum = (TextView)converView.findViewById(R.id.tv_rank_num);
		ImageView poster = (ImageView)converView.findViewById(R.id.listitem_movie_poster);
		TextView name = (TextView)converView.findViewById(R.id.listitem_movie_name);
		TextView name_en = (TextView)converView.findViewById(R.id.listitem_movie_name_en);
		TextView date = (TextView)converView.findViewById(R.id.listitem_movie_release_date);
		name.setText(movies.get(position).getChineseName());
		name_en.setText(movies.get(position).getEnglishName());
		
		
		rlMovieListItem.setPadding(mWidth * 1 / 24, 0, 0, 0);
		rlPoster.getLayoutParams().width = mWidth*2 / 8;
		rlPoster.getLayoutParams().height = mHeight;
		
		rlIntro.getLayoutParams().width = mWidth*2 / 3;
		rlIntro.getLayoutParams().height = mHeight;
		
		DateFormat createFormatter = new SimpleDateFormat("yyyy-MM-dd");
		if(movies.get(position).getReleaseDate()!= null && !movies.get(position).getReleaseDate().equals(""))
			date.setText("上映日期:"+createFormatter.format(movies.get(position).getReleaseDate()));
		else
			date.setText("未提供上映日期");
		
		
		imageLoader.displayImage(movies.get(position).getPosterUrl(), poster);
		
		rlRankLabel.setVisibility(View.GONE);
			
		if( typeId == 1){
			
			rlRankLabel.setVisibility(View.VISIBLE);
			rlRankLabel.getLayoutParams().width = mWidth*1 / 12;
			rlRankLabel.getLayoutParams().height = mWidth*1 / 12;
			tvRankNum.setTextSize(mWidth*1 / 24);
			
			if(position + 1 > 20)
				rlRankLabel.setVisibility(View.GONE);
			else
				tvRankNum.setText(Integer.toString(position+1));
			
		}else 
			rlRankLabel.setVisibility(View.GONE);
		
		
		
		return converView;

	}

}
