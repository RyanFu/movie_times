package com.jumplife.movieinfo.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;

import com.jumplife.movieinfo.TvScheduleActivity;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.News;
import com.jumplife.movieinfo.entity.Record;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.movieinfo.entity.User;
import com.jumplife.sqlite.SQLiteMovieDiary;

public class MovieAPI {

	private String urlAddress;
	private HttpURLConnection connection;
	private String requestedMethod;
	private Activity mActivity;
	private int connectionTimeout;
	private int readTimeout;
	private boolean usercaches;
	private boolean doInput;
	private boolean doOutput;
	
	public static final String TAG = "MOVIE_API";
	public static final boolean DEBUG = true;
	
	public static final String FILTER_RECENT = "FILTER_RECENT";
	public static final String FILTER_FIRST_ROUND = "FILTER_FIRST_ROUND";
	public static final String FILTER_SECOND_ROUND = "FILTER_SECOND_ROUND";
	public static final String FILTER_TOP_10 = "FILTER_TOP_10";
	public static final String FILTER_THIS_WEEK = "FILTER_THIS_WEEK";
	public static final String FILTER_ALL = "FILTER_ALL";
	
	public static final int SCORE_GOOD = 0;
	public static final int SCORE_NORMAL = 1;
	public static final int SCORE_BAD = 2;
	
	
	public MovieAPI(String urlAddress, int connectionTimeout, int readTimeout) {
		this.urlAddress = new String(urlAddress + "/");
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
		this.usercaches = false;
		this.doInput = true;
		this.doOutput = true;
	}
	public MovieAPI(String urlAddress) {
		this(new String(urlAddress), 5000, 5000);
	}
	
	public MovieAPI(Activity a) {
		this(new String("http://106.187.101.252"));
		this.mActivity = a;
	}
	
	public MovieAPI() {
		this(new String("http://106.187.101.252"));
	}
	
	public int connect(String requestedMethod, String apiPath) {
		int status = -1;
		try {
			URL url = new URL(urlAddress + apiPath);
			
			if(DEBUG)
				Log.d(TAG, "URL: " + url.toString());
			connection = (HttpURLConnection) url.openConnection();
					
			connection.setRequestMethod(requestedMethod);
			connection.setReadTimeout(this.readTimeout);
			connection.setConnectTimeout(this.connectionTimeout);
			connection.setUseCaches(this.usercaches);
			connection.setDoInput(this.doInput);
			connection.setDoOutput(this.doOutput);
			connection.setRequestProperty("Content-Type",  "application/json;charset=utf-8");
			
			connection.connect();

		} 
		catch (MalformedURLException e1) {
			e1.printStackTrace();
			return status;
		}
		catch (IOException e) {
			e.printStackTrace();
			return status;
		}
		
		return status;
	}
	
	public void disconnect()
	{
		connection.disconnect();
	}
	
	public ArrayList<Integer>[] getUpdateMoviesId(){
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] moviesId = (ArrayList<Integer>[])new ArrayList[6];
		ArrayList<Integer> updateIds = new ArrayList<Integer>(100);
		String message = getMessageFromServer("GET", "/api/v1/movies/first_second_comming_hot_update.json", null);
		if(message == null) {
			return null;
		}
		else {			
			try {
				JSONObject IdObject;
				int id = -1;
				IdObject = new JSONObject(message.toString());
				
				JSONArray firstJson = IdObject.getJSONArray("first_round");
				ArrayList<Integer> first_round = new ArrayList<Integer>(100);
				for(int i=0; i<firstJson.length(); i++){
					id = firstJson.getJSONObject(i).getInt("id");
					first_round.add(id);
					updateIds.add(id);
				}
				moviesId[0]=first_round;
				
				JSONArray secondJson = IdObject.getJSONArray("movies_second");
				ArrayList<Integer> movies_second = new ArrayList<Integer>(100);
				for(int i=0; i<secondJson.length(); i++){
					id = secondJson.getJSONObject(i).getInt("id");
					movies_second.add(id);
					updateIds.add(id);
				}
				moviesId[1]=movies_second;
			
				JSONArray commingJson = IdObject.getJSONArray("movies_commig");
				ArrayList<Integer> movies_commig = new ArrayList<Integer>(100);
				for(int i=0; i<commingJson.length(); i++){
					id = commingJson.getJSONObject(i).getInt("id");
					movies_commig.add(id);
					updateIds.add(id);
				}
				moviesId[2]=movies_commig;
				
				JSONArray hotJson = IdObject.getJSONArray("movies_hot");
				ArrayList<Integer> movies_hot = new ArrayList<Integer>(100);
				for(int i=0; i<hotJson.length(); i++){
					id = hotJson.getJSONObject(i).getInt("id");
					movies_hot.add(id);
					updateIds.add(id);
				}
				moviesId[3]=movies_hot;
				
				JSONArray thisWeekJson = IdObject.getJSONArray("movies_this_week");
				ArrayList<Integer> movies_this_week = new ArrayList<Integer>(100);
				for(int i=0; i<thisWeekJson.length(); i++){
					id = thisWeekJson.getJSONObject(i).getInt("id");
					movies_this_week.add(id);
					updateIds.add(id);
				}	
				moviesId[4]=movies_this_week;
				
				HashSet<Integer> hashSet = new HashSet<Integer>(updateIds);
				updateIds = new ArrayList<Integer>(hashSet) ;
				moviesId[5]=updateIds;
				
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return moviesId;
	}
	
	// Get Movie List
	public ArrayList<Movie> getMovieList (String filter) {
		ArrayList<Movie> movieList = new ArrayList<Movie>(10);
		String message = "";
		
		if(filter.equalsIgnoreCase(MovieAPI.FILTER_RECENT)) {
			message = getMessageFromServer("GET", "/api/v1/movies/comming.json", null);
		}
		else if(filter.equalsIgnoreCase(MovieAPI.FILTER_FIRST_ROUND)) {
			message = getMessageFromServer("GET", "/api/v1/movies/first_round.json", null);
		}
		else if (filter.equalsIgnoreCase(MovieAPI.FILTER_SECOND_ROUND)) {
			message = getMessageFromServer("GET", "/api/v1/movies/second_round.json", null);
		}
		else if (filter.equalsIgnoreCase(MovieAPI.FILTER_TOP_10)) {
			message = getMessageFromServer("GET", "api/v1/movies/hot.json", null);
		}
		else if (filter.equalsIgnoreCase(MovieAPI.FILTER_THIS_WEEK)) {
			message = getMessageFromServer("GET", "api/v1/movies/this_week.json", null);
		}
		else if (filter.equalsIgnoreCase(MovieAPI.FILTER_ALL)) {
			message = getMessageFromServer("GET", "api/v1/movies/all.json", null);
		}
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray movieArray;
			try {
				movieArray = new JSONArray(message.toString());
				for (int i = 0; i < movieArray.length() ; i++) {
					JSONObject movieJson = movieArray.getJSONObject(i).getJSONObject("movie");
					Movie movie = new Movie();
					movie.setId(movieJson.getInt("id"));
					movie.setChineseName(movieJson.getString("name"));
					movie.setEnglishName(movieJson.getString("name_en"));
					movie.setPosterUrl(movieJson.getString("poster_url"));
					movie.setGoodCount(movieJson.getInt("good_count"));
					
					if(!movieJson.isNull("records_count")) {
						movie.setRecordsCount(movieJson.getInt("records_count"));
					}
					
					if (!filter.equalsIgnoreCase(MovieAPI.FILTER_ALL))
						movie.setYoutubeId(movieJson.getString("youtube_video_id"));
					movieList.add(movie);
				}
				
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		
		return movieList;
	}
	
	//取得電影時刻表
	public Movie getMovieUpdate(Movie movie) {
		    
		    DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
			String message = getMessageFromServer("GET", "/api/v1/movies/" + movie.getId() + "/update_release_date_running_time_youtube.json", null);
			
			if(message == null) {
				return movie;
			}
			else {
				try {
					JSONObject movieJson = new JSONObject(message);
					if(!movieJson.isNull("release_date")){
						Date releaseDate = formatter2.parse(movieJson.getString("release_date"));
						movie.setReleaseDate(releaseDate);
					}
					if (!movieJson.isNull("youtube_video_id"))
					    movie.setYoutubeId(movieJson.getString("youtube_video_id"));
					if (!movieJson.isNull("level_url"))
					    movie.setLevelUrl(movieJson.getString("level_url"));
					if(!movieJson.isNull("running_time")){
						int runningTime = movieJson.getInt("running_time");
						movie.setRunningTime(runningTime);
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return movie;
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return movie;
				}
			}
			SQLiteMovieDiary sqlMovieDiary = new SQLiteMovieDiary(mActivity);
			sqlMovieDiary.updateMovie(movie);
			return movie;
	}
	
	public void AddMoviesFromInfo(String idlst) {
		String message = getMessageFromServer("GET", "/api/v1/movies/movies_info.json?movies_id=" + idlst, null);
		SQLiteMovieDiary sqlMovieDiary = new SQLiteMovieDiary(mActivity);
		
		if(message != null) {
			try {
				JSONArray movieArray;		
				movieArray = new JSONArray(message.toString());
				ArrayList<Movie> movies = new ArrayList<Movie>();
				for (int i = 0; i < movieArray.length() ; i++) {
					Log.d(TAG, "i : " + i);
					JSONObject movieJson = movieArray.getJSONObject(i);
					Movie movie = movieJsonToClass(movieJson);
					movies.add(movie);
				}
				sqlMovieDiary.insertMovies(movies);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<Record> getMovieRecordLimitList (String movieId) {
		ArrayList<Record> recordList = new ArrayList<Record> (10);
		
		String message = getMessageFromServer("GET", "/api/v1/records/get_movie_records_limit.json?movie_id=" + movieId, null);
		
		if(message == null) {
			return null;
		}
		try {
			JSONArray recordArray = new JSONArray(message);
			for(int i = 0; i < recordArray.length(); i++) {
				JSONObject recordJson  = recordArray.getJSONObject(i).getJSONObject("record");
				
				JSONObject movieJson = recordJson.getJSONObject("movie");
				
				Movie movie = new Movie();
				movie.setChineseName(movieJson.getString("name"));
				movie.setPosterUrl(movieJson.getString("poster_url"));
				
				User user = new User();
				user.setName(recordJson.getString("user_name"));
				user.setAccount(recordJson.getString("user_fb_id"));
				
				int loveCount = 0;
				if(!(recordJson.isNull("love_count")))
					loveCount = recordJson.getInt("love_count");
				
				boolean isLovedByUser = false;
				if(!(recordJson.isNull("is_loved_by_user")))
					isLovedByUser = recordJson.getBoolean("is_loved_by_user");
				
				Record record = new Record();
				record.setId(recordJson.getInt("id"));
				record.setMovie(movie);
				record.setComment(recordJson.getString("comment"));
			    record.setScore(recordJson.getInt("score"));
				record.setUser(user);
				record.setLoveCount(loveCount);
				record.setIsLovedByUser(isLovedByUser);
				
				if(!(recordJson.isNull("created_at"))){
					DateFormat releaseFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
					Date date = releaseFormatter.parse(recordJson.getString("created_at"));
					record.setCheckinTime(date);
				}
				
				recordList.add(record);
				
			}
		} 
		catch (JSONException e){
			e.printStackTrace();
			return null;
		}
		catch (ParseException e){
			e.printStackTrace();
			return null;
		}
		
		return recordList;
	}
	
	//取得電影時刻表
	public ArrayList<Theater> getMovieTimeTableListWithHall(int movieId) {
		
		ArrayList<Theater> threaterList = new ArrayList<Theater>(10);
		
		String message = getMessageFromServer("GET", "api/v1/movies/" + movieId + "/timetable.json", null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray theaterArray;
			String hallType = null;
			String buyLink = null;
			String hallStr = null;
			try {
				theaterArray = new JSONArray(message.toString());
				for (int i = 0; i < theaterArray.length() ; i++) {
					JSONObject theaterJson = theaterArray.getJSONObject(i).getJSONObject("movie_theater_ship");
					if(theaterJson.isNull("hall_type"))
						hallType = null;
					else 
						hallType = theaterJson.getString("hall_type");
					if(theaterJson.isNull("buy_link"))
						buyLink = null;
					else 
						buyLink = theaterJson.getString("buy_link");
					if(theaterJson.isNull("hall_str"))
						hallStr = null;
					else 
						hallStr = theaterJson.getString("hall_str");
					
					Theater theater = new Theater(theaterJson.getInt("id"), 
						theaterJson.getString("theater"), 
						theaterJson.getString("timetable"),
						hallType,
						buyLink,
						theaterJson.getInt("area_id"),
						null,
						null,
						hallStr);
					
					threaterList.add(theater);
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return threaterList;
	}
	
	//取得戲院電影時刻表
	public ArrayList<Theater> getMovieTheaterTimeTableListWithHall(int movieId, int theaterId) {
		
		ArrayList<Theater> threaterList = new ArrayList<Theater>(10);
		
		String message = getMessageFromServer("GET", "/api/v1/movies/" + movieId + "/timetable.json?theater_id=" + theaterId, null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray theaterArray;
			String hallType = null;
			String buyLink = null;
			String hallStr = null;
			try {
				theaterArray = new JSONArray(message.toString());
				for (int i = 0; i < theaterArray.length() ; i++) {
					JSONObject theaterJson = theaterArray.getJSONObject(i).getJSONObject("movie_theater_ship");
					if(theaterJson.isNull("hall_type"))
						hallType = null;
					else 
						hallType = theaterJson.getString("hall_type");
					if(theaterJson.isNull("buy_link"))
						buyLink = null;
					else
						buyLink = theaterJson.getString("buy_link");
					if(theaterJson.isNull("hall_str"))
						hallStr = null;
					else 
						hallStr = theaterJson.getString("hall_str");
					
					Theater theater = new Theater(theaterJson.getInt("id"), 
							theaterJson.getString("theater"), 
							theaterJson.getString("timetable"),
							hallType,
							buyLink,
							theaterJson.getInt("area_id"),
							null,
							null,
							hallStr);
					
					threaterList.add(theater);
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return threaterList;
	}
	
	//取得某一間戲院的電影列表
	public Theater getMovieList(Theater theater, String fbId) {
		ArrayList<Movie> movieList = new ArrayList<Movie>(10);
		String message = getMessageFromServer("GET", "/api/v1/movies.json?theater_id=" + theater.getId() + "&fb_id=" + fbId, null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray movieArray;

			try
			{
				movieArray = new JSONArray(message.toString());
				
				for (int i = 0; i < movieArray.length() ; i++) {
					JSONObject movieJson = movieArray.getJSONObject(i).getJSONObject("movie");
					Movie movie = movieJsonToClass(movieJson);
					movieList.add(movie);
				}
			} 
			catch (JSONException e){
				e.printStackTrace();
				return null;
			}
			theater.setMovies(movieList);
		}
		return theater;
	}
	
	//取得某一間戲院的電影列表
	public ArrayList<Integer> getMoviesIdList(Theater theater) {
		ArrayList<Integer> movieList = new ArrayList<Integer>(10);
		String message = getMessageFromServer("GET", "/api/v1/theaters/"+theater.getId()+"/get_movies_id.json", null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray movieArray;

			try
			{
				movieArray = new JSONArray(message.toString());
				
				for (int i = 0; i < movieArray.length() ; i++) {
					movieList.add(movieArray.getInt(i));
				}
			} 
			catch (JSONException e){
				e.printStackTrace();
				return null;
			}
		}
		return movieList;
	}
		
	//取得某一間戲院的電影和廳院列表
	public ArrayList<Movie> getMoviesIdandHallList(Theater theater) {
		ArrayList<Movie> movieList = new ArrayList<Movie>(10);
		String message = getMessageFromServer("GET", "/api/v1/theaters/"+theater.getId()+"/get_movies_id_and_hall_str.json", null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray movieArray;

			try
			{
				movieArray = new JSONArray(message.toString());
				for (int i = 0; i < movieArray.length(); i++) {
					Movie movie = new Movie();
					JSONObject movieObject = movieArray.getJSONObject(i);
					movie.setId(movieObject.getInt("movie_id"));
					movie.setHall(movieObject.getString("hall_str"));
					movieList.add(movie);
				}
			} 
			catch (JSONException e){
				e.printStackTrace();
				return null;
			}
		}
		return movieList;
	}
	
	//取得新聞列表
	public ArrayList<News> getNewsList(int page) {
		ArrayList<News> newsList = new ArrayList<News>(10);
		
		String message = getMessageFromServer("GET", "/api/v1/news.json?page=" + page, null);
		
		if(message == null) {
			return null;
		}
		else {
			JSONArray newsArray;
			
			try {
				newsArray = new JSONArray(message.toString());
				for(int i = 0; i < newsArray.length(); i++) {
					JSONObject newsJson = newsArray.getJSONObject(i).getJSONObject("news");
					String title = newsJson.getString("title");
					String thumbnailUrl = newsJson.getString("thumbnail_url");
					
					News news = new News();
				
					if(!(newsJson.isNull("created_at"))){
						DateFormat releaseFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
						Date date = releaseFormatter.parse(newsJson.getString("created_at"));
						news.setReleaseDate(date);
					}
					
					if(!(newsJson.isNull("source")))
						news.setSource(newsJson.getString("source"));
					
					news.setTitle(title);
					news.setThumbnailUrl(thumbnailUrl);
					int type = newsJson.getInt("news_type");
					news.setType(type);
					
					if(type == News.TYPE_LINK){
						if(!(newsJson.isNull("link"))){
							String link = newsJson.getString("link");
							news.setLink(link);
						}
					}
					else if (type == News.TYPE_PIC){
						if(!(newsJson.isNull("picture_url"))){
							String pictureUrl = newsJson.getString("picture_url");
							news.setPictureUrl(pictureUrl);
						}
						if(!(newsJson.isNull("content"))){
							String content = newsJson.getString("content");
							news.setContent(content);
						}
					}
					
					newsList.add(news);
					
					int a = 0;
					a = a + 1;
				}
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return null;
			} catch (ParseException e){
				e.printStackTrace();
				return null;
			}
			
		}
		
		return newsList;
	}
	
	public Movie movieJsonToClass (JSONObject movieJson) {
		Movie movie = null;
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		DateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
		
		if(movieJson == null) {
			return null;
		}
		else {
			try {
				//actor list
				ArrayList<String> actors = new ArrayList<String>(5);
				JSONArray actorsArray = movieJson.getJSONArray("actors");
				for (int j = 0; j < actorsArray.length(); j++) {
					actors.add(actorsArray.getString(j));
				}
				
				//director list
				ArrayList<String> directors = new ArrayList<String>(3);
				if(!(movieJson.isNull("directors"))) {
					JSONArray directorsArray = movieJson.getJSONArray("directors");
					for (int k = 0; k < directorsArray.length(); k++) {
						directors.add(directorsArray.getString(k));
					}
				}
				
				ArrayList<Record> recordList = new ArrayList<Record> (10);
				//確認是否有record
				if(!(movieJson.isNull("record"))) {
					JSONArray recordArray = movieJson.getJSONArray("record");
					for (int l = 0; l < recordArray.length(); l++) {
						Record record = new Record();
						JSONObject recordJson = recordArray.getJSONObject(l);
						record.setId(recordJson.getInt("id"));
						
						int score = -1;
						if(!(recordJson.isNull("score"))) {
							score = recordJson.getInt("score");
							record.setScore(score);
						}
						
						String comment = "";
						if(!(recordJson.isNull("comment"))) {
							comment = recordJson.getString("comment");
							record.setComment(comment);
						}
						
						if(!(recordJson.isNull("created_at"))){
							DateFormat releaseFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
							Date date = releaseFormatter.parse(recordJson.getString("created_at"));
							record.setCheckinTime(date);
						}
							
						
						User user = new User();
						user.setAccount(recordArray.getJSONObject(l).getString("user_fb_id"));
						record.setUser(user);
						recordList.add(record);
					}
				}
				Date releaseDate = null;
				int runningTime = -1;
				if(!movieJson.isNull("release"))
					releaseDate = formatter.parse(movieJson.getString("release"));
				else if(!movieJson.isNull("release_date"))
					releaseDate = formatter2.parse(movieJson.getString("release_date"));
				if(!movieJson.isNull("running_time"))
					runningTime = movieJson.getInt("running_time");
				boolean is_comming = false;
				boolean is_first_round = false;
				boolean is_hot = false;
				boolean is_second_round = false;
				boolean is_this_week = false;
				if(!movieJson.isNull("is_comming"))
					is_comming = movieJson.getBoolean("is_comming");
				if(!movieJson.isNull("is_first_round"))
					is_first_round = movieJson.getBoolean("is_first_round");
				if(!movieJson.isNull("is_hot"))
					is_hot = movieJson.getBoolean("is_hot");
				if(!movieJson.isNull("is_second_round"))
					is_second_round = movieJson.getBoolean("is_second_round");
				if(!movieJson.isNull("is_this_week"))
					is_this_week = movieJson.getBoolean("is_this_week");
				
				
				 movie = new Movie(movieJson.getInt("id"), movieJson.getString("name"), movieJson.getString("name_en"), movieJson.getString("intro"), 
						 releaseDate, movieJson.getString("poster_url"), runningTime, movieJson.getString("level_url"), actors, directors, recordList, 
						 movieJson.getString("youtube_video_id"), 0, 0, is_first_round,  is_second_round,  is_hot,  is_this_week, is_comming);
				 
			} 
			catch (JSONException e) {
				e.printStackTrace();
				return null;
			} 
			catch (ParseException e) {
				e.printStackTrace();
				return null;
			}	
		}
		return movie;
	}
	
	public String getMessageFromServer(String requestMethod, String apiPath, JSONObject json) {
		URL url;
		try {
			url = new URL(this.urlAddress +  apiPath);
			if(DEBUG)
				Log.d(TAG, "URL: " + url);
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			
			connection.setRequestProperty("Content-Type",  "application/json;charset=utf-8");
			if(requestMethod.equalsIgnoreCase("POST"))
				connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.connect();
			
			
			if(requestMethod.equalsIgnoreCase("POST")) {
				OutputStream outputStream;
				
				outputStream = connection.getOutputStream();
				if(DEBUG)
					Log.d("post message", json.toString());
				
				outputStream.write(json.toString().getBytes());
				outputStream.flush();
				outputStream.close();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder lines = new StringBuilder();;
			String tempStr;
			
			while ((tempStr = reader.readLine()) != null) {
	            lines = lines.append(tempStr);
	        }
			if(DEBUG)
				Log.d("MOVIE_API", lines.toString());
			
			reader.close();
			connection.disconnect();
			
			return lines.toString();
		} 
		catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getPromotion(String picUrl, String link, String title, String description) {
				
		String message = getMessageFromServer("GET", "api/promotion.json", null);
		
		try{
			JSONObject responseJson = new JSONObject(message);
			
			picUrl = responseJson.getString("picture_link");
			link = responseJson.getString("link");
			title = responseJson.getString("tilte");
			description = responseJson.getString("description");
		} 
		catch (JSONException e){
			e.printStackTrace();
		}
	}
	
	public String[] getPromotion() {
				
		String message = getMessageFromServer("GET", "api/movieinfo_promotion.json", null);
		String[] tmp = new String[5];
				
		if(message == null) {
			return null;
		}
		try{
			JSONObject responseJson = new JSONObject(message);
			
			tmp[0] = (responseJson.getString("picture_link"));
			tmp[1] = (responseJson.getString("link"));
			tmp[2] = (responseJson.getString("tilte"));
			tmp[3] = (responseJson.getString("description"));
			tmp[4] = (responseJson.getString("version"));
		} 
		catch (JSONException e){
			e.printStackTrace();
			return null;
		}
		
		return tmp;
	}
	
	public String[] getTvSchedule(int tvId) {
		String[] tvSchedule = new String[TvScheduleActivity.totalDays*2];
		
		String message = getMessageFromServer("GET", "/api/v1/channels/" + tvId + "/channel_time.json", null);
		
		if(message == null) {
			return null;
		}
		try {
			JSONArray recordArray = new JSONArray(message);
			for(int i = 0; i < TvScheduleActivity.totalDays; i++) {
				JSONObject channelsJson  = recordArray.getJSONObject(i).getJSONObject("channel_time");
				tvSchedule[i*2] = channelsJson.getString("time");
				tvSchedule[i*2+1] = channelsJson.getString("date");
			}
		} 
		catch (JSONException e){
			e.printStackTrace();
			return null;
		} 
		
		return tvSchedule;
	}
	
	public String getUrlAddress() {
		return urlAddress;
	}
	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}
	public HttpURLConnection getConnection() {
		return connection;
	}
	public void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}
	public String getRequestedMethod() {
		return requestedMethod;
	}
	public void setRequestedMethod(String requestedMethod) {
		this.requestedMethod = requestedMethod;
	}
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public boolean isUsercaches() {
		return usercaches;
	}
	public void setUsercaches(boolean usercaches) {
		this.usercaches = usercaches;
	}
	public boolean isDoInput() {
		return doInput;
	}
	public void setDoInput(boolean doInput) {
		this.doInput = doInput;
	}
	public boolean isDoOutput() {
		return doOutput;
	}
	public void setDoOutput(boolean doOutput) {
		this.doOutput = doOutput;
	}
}
