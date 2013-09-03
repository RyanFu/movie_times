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
import android.widget.ImageView;
import android.widget.TextView;

public class MovieGalleryAdapter extends BaseAdapter{

	private ArrayList<Movie> movies;
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public MovieGalleryAdapter(Context mContext, ArrayList<Movie> movies, boolean showVideo){
		this.movies = movies;
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
		View converView = myInflater.inflate(R.layout.gallery_movie, null);
		
		ImageView poster = (ImageView)converView.findViewById(R.id.movie_poster);
		TextView name = (TextView)converView.findViewById(R.id.movie_name);
		TextView rank = (TextView)converView.findViewById(R.id.textview_rank);
		
		rank.setText(String.valueOf((position + 1)));
		
		if(position < movies.size()) {
			name.setText(movies.get(position).getChineseName());
			imageLoader.displayImage(movies.get(position).getPosterUrl(), poster, options);
		}
		
		return converView;
	}

}
