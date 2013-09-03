package com.jumplife.adapter;

import java.util.ArrayList;

import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Movie;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieListAdapter extends BaseAdapter implements Filterable{
	
    public static final int ALL=-1; //全部需要  
    public static final int FILTER_HEAD=0;//从头开始过滤  
    public static final int FILTER_FOOT=1;//从后开始过滤  
    public static final int FILTER_BODY=2;//任意过滤  
    
    Context mContext;
    private ArrayList<Movie> movies;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public MovieListAdapter(Context mContext, ArrayList<Movie> movieList){
		this.movies = movieList;
		this.mContext = mContext;
		
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
		
		LayoutInflater myInflater = LayoutInflater.from(mContext);
		View converView = myInflater.inflate(R.layout.listview_movies, null);
		
		ImageView poster = (ImageView)converView.findViewById(R.id.movie_poster);
		TextView name = (TextView)converView.findViewById(R.id.movie_name);
		TextView name_en = (TextView)converView.findViewById(R.id.movie_name_en);
		TextView hall = (TextView)converView.findViewById(R.id.movie_hall);
		name.setText(movies.get(position).getChineseName());
		name_en.setText(movies.get(position).getEnglishName());
		if(movies.get(position).getHall()!= null && !movies.get(position).getHall().equals("")) {
			hall.setText(movies.get(position).getHall());
			hall.setVisibility(View.VISIBLE);
		} else
			hall.setVisibility(View.GONE);
		imageLoader.displayImage(movies.get(position).getPosterUrl(), poster, options);
		
		return converView;

	}

	public Filter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}
}
