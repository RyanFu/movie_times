package com.jumplife.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.ifixit.android.sectionheaders.Section;
import com.jumplife.movieinfo.MovieInfoTabActivities;
import com.jumplife.movieinfo.R;
import com.jumplife.movieinfo.entity.Movie;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class MovieSectionAdapter extends Section implements Filterable{

	private Context mContext;
    private ArrayList<Movie> movieList;
	private String headerString;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	
	public MovieSectionAdapter(Context context, ArrayList<Movie> movieList, String headerString) {
		this.mContext = context;
		this.movieList = movieList;
		this.headerString = headerString;
		
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
		return movieList.size();
	}

	public Object getItem(int position) {
		return movieList.get(position);
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
		name.setText(movieList.get(position).getChineseName());
		name_en.setText(movieList.get(position).getEnglishName());

		imageLoader.displayImage(movieList.get(position).getPosterUrl(), poster, options);
		
		return converView;
	}

	@Override
	public Object getHeaderItem() {
		return headerString;
	}

	@Override
	public View getHeaderView(View convertView, ViewGroup parent) {
		TextView header = (TextView)convertView;
		
		if (header == null) {
			header = new TextView(mContext);
			header.setTextSize(16);
			header.setPadding(2, 1, 2, 1);
			header.setBackgroundColor(mContext.getResources().getColor(R.color.black));
			header.setTextColor(mContext.getResources().getColor(R.color.main_color_yellow));
		}
		header.setText(headerString);
	
		return header;
	}

	public Filter getFilter() {
		return null;
	}
	
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		Movie movie = movieList.get(position);
	   	Intent newAct = new Intent();
	   	newAct.putExtra("movie_id", movie.getId());
	   	newAct.putExtra("movie_name", movie.getChineseName());
	    newAct.putExtra("theater_id", -1);
        newAct.setClass(mContext, MovieInfoTabActivities.class );
        mContext.startActivity( newAct );
	}
}
