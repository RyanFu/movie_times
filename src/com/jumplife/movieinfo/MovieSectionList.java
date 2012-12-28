package com.jumplife.movieinfo;

import java.text.DateFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.TrackedActivity;
import com.ifixit.android.sectionheaders.SectionHeadersAdapter;
import com.ifixit.android.sectionheaders.SectionListView;
import com.jumplife.ad.AdGenerator;
import com.jumplife.movieinfo.api.MovieAPI;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.sectionlistview.MovieSectionAdapter;
import com.jumplife.sqlite.SQLiteMovieDiary;

public class MovieSectionList extends TrackedActivity {

    private AdView                          adView;
    private TreeMap<Date, ArrayList<Movie>> movieMap;

    public String[]                         ListType = { "近期上映", "首輪電影", "二輪電影", "本周新片" };

    public enum TYPE {
        RECENT, FIRSTROUND, SECONDROUND, WEEKLY, THEATER
    };

    TYPE                     listType  = TYPE.RECENT;

    private ArrayList<Movie> movieList = new ArrayList<Movie>();
    private SectionListView  movieListView;

    private View             viewHeader;
    private LinearLayout     theaterCheck;
    private LinearLayout     theaterLocation;
    private LinearLayout     theaterPhone;
    private LinearLayout     theaterLike;
    private Theater          theater;
    private LoadDataTask     tast;
    private static String    TAG       = "MovieSectionList";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_movie_list);

        getType();
        findViews();
        tast = new LoadDataTask();
        if(Build.VERSION.SDK_INT < 11)
        	tast.execute();
        else
        	tast.executeOnExecutor(LoadDataTask.THREAD_POOL_EXECUTOR, 0);

        AdTask adTask = new AdTask();
        adTask.execute();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
    }

    private void getType() {
        Bundle extras = getIntent().getExtras();
        try {
            int type = extras.getInt("movielist");
            switch (type) {
            case 0:
                listType = TYPE.RECENT;
                break;
            case 1:
                listType = TYPE.FIRSTROUND;
                break;
            case 2:
                listType = TYPE.SECONDROUND;
                break;
            case 3:
                listType = TYPE.WEEKLY;
                break;
            case 4:
                theater = new Theater(extras.getInt("theater_id"), extras.getString("theater_name"), new ArrayList<Movie>(10), "", "",
                        extras.getString("theater_buylink"), -1, extras.getString("theater_phone"), extras.getString("theater_location"));
                listType = TYPE.THEATER;
            }
        } catch (Exception e) {

        }

    }

    private void setListListener() {
        /*
         * movieListView.getListView().setOnItemClickListener(new OnItemClickListener() { public void onItemClick(AdapterView<?> parent, View view, int
         * position, long id) { if(listType == TYPE.THEATER) position = position-1; Movie movie = movieList.get(position); Intent newAct = new Intent();
         * newAct.putExtra("movie_id", movie.getId()); if(listType == TYPE.THEATER) newAct.putExtra("theater_id", theater.getId()); else
         * newAct.putExtra("theater_id", -1); newAct.setClass( MovieSectionList.this, MovieInfoTabActivities.class ); startActivity( newAct ); } });
         */
        theaterCheck.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(theater.getBuyLink()));
                startActivity(intent);
            }
        });
        theaterPhone.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Uri uri = Uri.parse("tel:" + theater.getPhone());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });
        theaterLocation.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + theater.getAddress()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
        theaterLike.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + theater.getAddress()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
    }

    private void setView() {
        if (listType == TYPE.THEATER) {
            if (theater.getBuyLink() == null)
                theaterCheck.setVisibility(View.INVISIBLE);
            else
            	theaterCheck.setVisibility(View.VISIBLE);
            /*if (theater.getAddress() != null)
                theaterPhone.setText(theater.getPhone());
            else
                theaterPhone.setText("電話未提供");
            if (theater.getAddress() != null)
                theaterLocation.setText(theater.getAddress());
            else
                theaterLocation.setText("地址未提供");*/
        }
    }

    private void fetchData() {
        MovieAPI movieAPI = new MovieAPI();
        SQLiteMovieDiary sqlMovieDiary = new SQLiteMovieDiary(this);
        if (listType == TYPE.RECENT)
            movieList = sqlMovieDiary.getMovieList(SQLiteMovieDiary.FILTER_RECENT);
        else if (listType == TYPE.FIRSTROUND)
            movieList = sqlMovieDiary.getMovieList(SQLiteMovieDiary.FILTER_FIRST_ROUND);
        else if (listType == TYPE.SECONDROUND)
            movieList = sqlMovieDiary.getMovieList(SQLiteMovieDiary.FILTER_SECOND_ROUND);
        else if (listType == TYPE.WEEKLY)
            movieList = sqlMovieDiary.getMovieList(SQLiteMovieDiary.FILTER_THIS_WEEK);
        else {
            theater = movieAPI.getMovieList(theater, null);
            if (theater.getMovies() == null)
                movieList = null;
            else
                movieList = theater.getMovies();
        }

        // 利用上映時間把電影做分類
        movieMap = new TreeMap<Date, ArrayList<Movie>>();

        for (int i = 0; i < movieList.size(); i++) {
            Movie movie = movieList.get(i);
            // 先確認key是否存在
            if (movieMap.containsKey(movie.getReleaseDate())) {
                // 已經有的話就把movie加進去
                movieMap.get(movie.getReleaseDate()).add(movie);
            } else {
                // 沒有的話就建一個加進去
                ArrayList<Movie> newMovieList = new ArrayList<Movie>(10);
                newMovieList.add(movie);

                movieMap.put(movie.getReleaseDate(), newMovieList);
            }
        }
        /*
         * Iterator<Date> iterator = movieMap.keySet().iterator();
         * 
         * //目前時間排序是由遠而近 while(iterator.hasNext()) { Date keyDate = iterator.next(); Log.d("Ben", "Key Date: " + keyDate.toString()); Log.d("Ben",
         * "Movie List Size: " + movieMap.get(keyDate).size()); }
         */
        /*
         * //由近而遠 Set<Date> keySet = movieMap.keySet(); Object[] dateArray = keySet.toArray(); for (int i = (dateArray.length - 1); i > 0; i--) { Date keyDate =
         * (Date) dateArray[i]; //Log.d("Ben", "Key Date: " + keyDate.toString()); //Log.d("Ben", "Movie List Size: " + movieMap.get(keyDate).size()); }
         */
    }

    private void setListAdatper() {
        if (listType == TYPE.THEATER) {
            // movieListView.addHeaderView(viewHeader);
        }

        SectionHeadersAdapter adapter = new SectionHeadersAdapter();

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        if (listType == TYPE.RECENT) {
            Iterator<Date> iterator = movieMap.keySet().iterator();

            // 目前時間排序是由遠而近
            while (iterator.hasNext()) {
                Date keyDate = iterator.next();
                adapter.addSection(new MovieSectionAdapter(this, movieMap.get(keyDate), dateFormatter.format(keyDate)));
            }
        } else {
            Set<Date> keySet = movieMap.keySet();
            Object[] dateArray = keySet.toArray();
            for (int i = (dateArray.length - 1); i >= 0; i--) {
                Date keyDate = (Date) dateArray[i];
                if (dateFormatter.format(keyDate).equals("1899-11-30"))
                    adapter.addSection(new MovieSectionAdapter(this, movieMap.get(keyDate), "上映日期未提供"));
                else
                    adapter.addSection(new MovieSectionAdapter(this, movieMap.get(keyDate), dateFormatter.format(keyDate)));
            }
        }

        movieListView.setAdapter(adapter);
        movieListView.getListView().setOnItemClickListener(adapter);

        // movieListView.setAdapter(new MovieListAdapter(MovieSectionList.this, movieList));
        /*
         * SectionHeadersAdapter adapter = new SectionHeadersAdapter();
         * 
         * for (int i = 0; i < 10; i ++) { adapter.addSection(new SampleSectionAdapter(this, getSampleList(), "Header #" + i)); }
         */

        // movieListView.setTextFilterEnabled(true);
    }

    private void findViews() {
        TextView topbar_text = (TextView) findViewById(R.id.topbar_text);
        viewHeader = LayoutInflater.from(MovieSectionList.this).inflate(R.layout.listview_theaterinfo_header, null);
        theaterCheck = (LinearLayout) viewHeader.findViewById(R.id.ll_check);
        theaterLocation = (LinearLayout) viewHeader.findViewById(R.id.ll_address);
        theaterPhone = (LinearLayout) viewHeader.findViewById(R.id.ll_phone);
        theaterLike = (LinearLayout) viewHeader.findViewById(R.id.ll_like);
        if (listType == TYPE.RECENT)
            topbar_text.setText(ListType[0]);
        else if (listType == TYPE.FIRSTROUND)
            topbar_text.setText(ListType[1]);
        else if (listType == TYPE.SECONDROUND)
            topbar_text.setText(ListType[2]);
        else if (listType == TYPE.WEEKLY)
            topbar_text.setText(ListType[3]);
        else
            topbar_text.setText(theater.getName());

        movieListView = (SectionListView) findViewById(R.id.section_list);
    }

    class AdTask extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... arg0) {
			
			return null;
		}
		
		 @Override  
	     protected void onPostExecute(String result) {
			 //setAd();
			 AdGenerator adGenerator = new AdGenerator(MovieSectionList.this);
			 adGenerator.setAd();
			 super.onPostExecute(result);
		 }
    }

    public class LoadDataTask extends AsyncTask<Integer, Integer, String> {

        private ProgressDialog         progressdialogInit;
        private final OnCancelListener cancelListener = new OnCancelListener() {
                                                          public void onCancel(DialogInterface arg0) {
                                                              LoadDataTask.this.cancel(true);
                                                              finish();
                                                          }
                                                      };

        @Override
        protected void onPreExecute() {
            progressdialogInit = ProgressDialog.show(MovieSectionList.this, "Load", "Loading…");
            progressdialogInit.setTitle("Load");
            progressdialogInit.setMessage("Loading…");
            progressdialogInit.setOnCancelListener(cancelListener);
            progressdialogInit.setCanceledOnTouchOutside(false);
            progressdialogInit.setCancelable(true);
            progressdialogInit.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
        	Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            fetchData();
            return "progress end";
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
        	if(MovieSectionList.this != null && !MovieSectionList.this.isFinishing())
        		progressdialogInit.dismiss();
            if (movieList == null && movieList.size() <= 0) {
                showReloadDialog(MovieSectionList.this);
            } else {
                setView();
                setListAdatper();
                setListListener();
            }
            super.onPostExecute(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tast != null && tast.getStatus() != AsyncTask.Status.FINISHED)
            tast.cancel(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showReloadDialog(final Context context) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

        alt_bld.setMessage("是否重新載入資料？").setCancelable(true).setPositiveButton("確定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                LoadDataTask tast = new LoadDataTask();
                tast.execute();
                dialog.dismiss();
            }
        });

        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("讀取錯誤");
        // Icon for AlertDialog
        // alert.setIcon(R.drawable.gnome_logout);
        alert.show();
    }
}
