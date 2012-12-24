package com.jumplife.sectionlistview;

import java.util.ArrayList;

import com.jumplife.imageload.ImageLoader;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Movie;

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
	private ImageLoader imageLoader;
	public MovieGalleryAdapter(Context mContext, ArrayList<Movie> movies, boolean showVideo){
		this.movies = movies;
		this.mContext = mContext;
		this.imageLoader=new ImageLoader(mContext);
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
		
		name.setText(movies.get(position).getChineseName());
		imageLoader.DisplayImage(movies.get(position).getPosterUrl(), poster);
		
		return converView;
	}

}
