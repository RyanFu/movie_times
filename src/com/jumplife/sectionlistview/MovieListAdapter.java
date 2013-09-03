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

public class MovieListAdapter extends BaseAdapter {
	
	Context mContext;
    private ArrayList<Movie> movies;
	private ImageLoader imageLoader;
	
	public MovieListAdapter(Context mContext, ArrayList<Movie> movieList){
		this.movies = movieList;
		this.mContext = mContext;
		imageLoader=new ImageLoader(mContext);
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
		View converView = myInflater.inflate(R.layout.fragment_movietime_listitem, null);
		
		ImageView poster = (ImageView)converView.findViewById(R.id.listitem_movie_poster);
		TextView name = (TextView)converView.findViewById(R.id.listitem_movie_name);
		TextView name_en = (TextView)converView.findViewById(R.id.listitem_movie_name_en);
		TextView date = (TextView)converView.findViewById(R.id.listitem_movie_release_date);
		name.setText(movies.get(position).getChineseName());
		name_en.setText(movies.get(position).getEnglishName());
		if(movies.get(position).getReleaseDate()!= null && !movies.get(position).getReleaseDate().equals("")) {
			String dateString = String.format("yyyy-mm-dd", movies.get(position).getReleaseDate());
			date.setText(dateString);
			date.setVisibility(View.VISIBLE);
		} else
			date.setVisibility(View.GONE);
		imageLoader.DisplayImage(movies.get(position).getPosterUrl(), poster);
		
		return converView;

	}

}
