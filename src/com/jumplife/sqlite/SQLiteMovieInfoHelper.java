package com.jumplife.sqlite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jumplife.movieinfo.entity.AppProject;
import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;

@SuppressLint("SimpleDateFormat")
public class SQLiteMovieInfoHelper extends SQLiteOpenHelper {
	private static final String		MovieTable          = "movies";
    private static final String		TheaterTable        = "theaters";
    private static final String   	ProjectTable        = "projects";
    private static final String		DB_NAME             = "movieinfo.sqlite";                            // 資料庫名稱
    private static final int		DATABASE_VERSION    = 7;
    private static String 			DB_PATH_DATA;

    public static final String    	FILTER_RECENT       = "is_comming";
    public static final String    	FILTER_FIRST_ROUND  = "is_first_round";
    public static final String    	FILTER_SECOND_ROUND = "is_second_round";
    public static final String    	FILTER_TOP_10       = "is_hot";
    public static final String    	FILTER_THIS_WEEK    = "is_this_week";
    public static final String    	FILTER_ALL          = "FILTER_ALL";
    
    private final  Context 		  mContext;
    private static SQLiteMovieInfoHelper helper;

    public static synchronized SQLiteMovieInfoHelper getInstance(Context context) {
        if(helper == null) {
            helper = new SQLiteMovieInfoHelper(context.getApplicationContext());
        }

        return helper;
    }
    
    public SQLiteMovieInfoHelper(Context context) {
    	super(context.getApplicationContext(), DB_NAME, null, DATABASE_VERSION);
    	this.mContext = context.getApplicationContext();
    	//DB_PATH_DATA = Environment.getDataDirectory() + DB_PATH;
    	DB_PATH_DATA = mContext.getFilesDir().getAbsolutePath().replace("files", "databases") + "/";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void closeHelper() {
        if(helper != null)
        	helper.close();
    }

    public void createDataBase() {

    	SharePreferenceIO shareIO = new SharePreferenceIO(mContext);
        if (shareIO != null) {
        	int checkVersion = DATABASE_VERSION;
        	if(shareIO.SharePreferenceO("checkversion", 0) < checkVersion) {
		        File dbf = new File(DB_PATH_DATA + DB_NAME);
				if(dbf.exists()){
				    dbf.delete();
				    shareIO.SharePreferenceI("checkversion", checkVersion);
					Log.d(null, "delete dbf");
				}
        	}
        }
		
    	boolean dbExist = checkDataBase();
        if(dbExist){
            Log.d(null, "db exist");
        }else{
            File dir = new File(DB_PATH_DATA);
			if(!dir.exists()){
			    dir.mkdirs();
			}
			Log.d(null, "copy database");
			copyDataBase();			
        }
    }
    
    private boolean checkDataBase(){
    	File dbFile = new File(DB_PATH_DATA + DB_NAME);
        return dbFile.exists();
    }
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() {
    	try {
            InputStream is = mContext.getAssets().open(DB_NAME);
            OutputStream os = new FileOutputStream(DB_PATH_DATA + DB_NAME);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(null, "copy data base failed");
        }
    }

    /*
     * theater data
     */
    public long addTheater(SQLiteDatabase db, Theater theater) {
        long rowId = insertTheater(db, theater);

        return rowId;
    }

    public static void addTheaterList(SQLiteDatabase db, ArrayList<Theater> theaterLst) {
        for (int i = 0; i < theaterLst.size(); i++) {
            Theater theater = theaterLst.get(i);
            insertTheater(db, theater);
        }
    }

    private static long insertTheater(SQLiteDatabase db, Theater theater) {
        ContentValues args = new ContentValues();
        args.put("ID", theater.getId());
        args.put("NAME", theater.getName());
        args.put("LOCATION", theater.getAddress());
        args.put("AREA_ID", theater.getArea());
        args.put("BUY_LINK", theater.getBuyLink());
        args.put("PHONE", theater.getPhone());
        return db.insert(TheaterTable, null, args);
    }

    public ArrayList<Theater> getTheaterList(SQLiteDatabase db) throws SQLException {

        ArrayList<Theater> theater_lst = new ArrayList<Theater>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TheaterTable, null);
        while (cursor.moveToNext()) {
            Theater theater = new Theater();
            theater.setId(cursor.getInt(0));
            theater.setName(cursor.getString(1));
            theater.setAddress(cursor.getString(3));
            theater.setArea(cursor.getInt(6));
            theater.setBuyLink(cursor.getString(7));
            theater.setPhone(cursor.getString(8));

            theater_lst.add(theater);
        }

        cursor.close();
        return theater_lst;
    }

    public ArrayList<Theater> getTheaterList(SQLiteDatabase db, int areaId) {
    	
        ArrayList<Theater> theater_lst = new ArrayList<Theater>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TheaterTable + " WHERE area_id = \'" + areaId + "\'", null);
        while (cursor.moveToNext()) {
            Theater theater = new Theater();
            theater.setId(cursor.getInt(0));
            theater.setName(cursor.getString(1));
            theater.setAddress(cursor.getString(3));
            theater.setArea(cursor.getInt(6));
            theater.setBuyLink(cursor.getString(7));
            theater.setPhone(cursor.getString(8));

            theater_lst.add(theater);
        }

        cursor.close();
        return theater_lst;
    }

    public ArrayList<Theater> getTheaterListById(SQLiteDatabase db, String theaterID) {
        String[] likeTheaters = theaterID.split(",");
        String theaterIDs = "";
        for (int i = 0; i < likeTheaters.length; i++) {
            if (!likeTheaters[i].equals("")) {
                theaterIDs = theaterIDs + likeTheaters[i];
                if (i < likeTheaters.length - 1)
                    theaterIDs = theaterIDs + ",";
            }
        }

        ArrayList<Theater> theater_lst = new ArrayList<Theater>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TheaterTable + " WHERE id in (" + theaterIDs + ")", null);
        while (cursor.moveToNext()) {
            Theater theater = new Theater();
            theater.setId(cursor.getInt(0));
            theater.setName(cursor.getString(1));
            theater.setAddress(cursor.getString(3));
            theater.setArea(cursor.getInt(6));
            theater.setBuyLink(cursor.getString(7));
            theater.setPhone(cursor.getString(8));

            theater_lst.add(theater);
        }

        cursor.close();
        return theater_lst;
    }

    /*
     * movie data
     */
    public boolean insertMovies(SQLiteDatabase db, ArrayList<Movie> movies) {
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            if(movie != null) 
            	insertMovie(db, movie);
        }
        return true;
    }

    public static long insertMovie(SQLiteDatabase db, Movie movie) {
        String actors = "";
        if(movie.getActors() != null) {
	        for (int i = 0; i < movie.getActors().size(); i++)
	            actors = actors + movie.getActors().get(i) + "、";
        }
        String directors = "";
        if(movie.getDirectors() != null) {
	        for (int i = 0; i < movie.getDirectors().size(); i++)
	            directors = directors + movie.getDirectors().get(i) + "、";
        }

        DateFormat createFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String youtubeId;
        if (movie.getYoutubeId() != null)
            youtubeId = movie.getYoutubeId();
        else
            youtubeId = "null";
        String date;
        if (movie.getReleaseDate() != null)
            date = createFormatter.format(movie.getReleaseDate());
        else
            date = "null";

        Cursor cursor = db.rawQuery(
                "INSERT OR IGNORE INTO " + MovieTable + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                new String[] { movie.getId() + "", movie.getChineseName(), movie.getEnglishName(), movie.getInttroduction(), movie.getPosterUrl(), date,
                        movie.getRunningTime() + "", movie.getLevelUrl(), getSQLiteBoolean(movie.get_is_first_round()),
                        getSQLiteBoolean(movie.get_is_second_round()), getSQLiteBoolean(movie.get_is_hot()), youtubeId,
                        getSQLiteBoolean(movie.get_is_comming()), getSQLiteBoolean(movie.get_is_this_week()), actors, directors });

        cursor.moveToFirst();
        cursor.close();
        return 0;
    }

    static String getSQLiteBoolean(boolean b) {
        if (b)
            return "t";
        else
            return "f";
    }
    
    public void updateMovieIs(SQLiteDatabase db, ArrayList<Integer>[] a) {
        String[] ids = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            if (a[i].size() > 0) {
                ids[i] = String.valueOf(a[i].get(0));
                for (int j = 1; j < a[i].size(); j++) {
                    ids[i] = ids[i] + "," + a[i].get(j);
                }
            }
        }

        Cursor cursor = db.rawQuery("UPDATE " + MovieTable + " SET " + FILTER_FIRST_ROUND + " = CASE WHEN id IN (" + ids[0] + ")  THEN 't' ELSE 'f' END, "
                + FILTER_SECOND_ROUND + " = CASE WHEN id IN (" + ids[1] + ")  THEN 't' ELSE 'f' END, " + FILTER_RECENT + " = CASE WHEN id IN (" + ids[2]
                + ")  THEN 't' ELSE 'f' END, " + FILTER_TOP_10 + " = CASE WHEN id IN (" + ids[3] + ")  THEN 't' ELSE 'f' END, " + FILTER_THIS_WEEK
                + " = CASE WHEN id IN (" + ids[4] + ")  THEN 't' ELSE 'f' END;", null);
        cursor.moveToFirst();
        cursor.close();
    }

    public ArrayList<Integer> findMoviesIdNotInDB(SQLiteDatabase db, ArrayList<Integer> moviesId) {
        ArrayList<Integer> returnsID = new ArrayList<Integer>();
        ArrayList<Integer> dbsID = new ArrayList<Integer>();

        Cursor cursor = db.rawQuery("SELECT id FROM " + MovieTable, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                dbsID.add(cursor.getInt(0));
            }
        }
        cursor.close();

        HashSet<Integer> hashSet = new HashSet<Integer>(dbsID);
        for (Integer id : moviesId) {
            if (!hashSet.contains(id))
                returnsID.add(id);
        }
        return returnsID;
    }

    public boolean updateMovie(SQLiteDatabase db, Movie movie) {
    	Log.d(null, "update movie");
        DateFormat createFormatter = new SimpleDateFormat("yyyy-MM-dd");
        
        String levelUrl;
        if (movie.getLevelUrl() != null)
            levelUrl = movie.getLevelUrl();
        else
            levelUrl = "null";
        
        String youtubeId;
        if (movie.getYoutubeId() != null)
            youtubeId = movie.getYoutubeId();
        else
            youtubeId = "null";
        
        String date;
        if (movie.getReleaseDate() != null)
            date = createFormatter.format(movie.getReleaseDate());
        else
            date = "null";

        
		String actors = "";
        if (movie.getActors() != null && movie.getActors().size() > 0) {
        	for (String s : movie.getActors()) {
    			if(s != movie.getActors().get(movie.getActors().size()-1))
    				actors += s + ", ";
    			else
    				actors += s;
    		}
            
        } else
        	actors = "null";
        
        String directors = "";
        if (movie.getDirectors() != null && movie.getDirectors().size() > 0) {
        	for (String s : movie.getDirectors()) {
    			if(s != movie.getDirectors().get(movie.getDirectors().size()-1))
    				directors += s + ", ";
    			else
    				directors += s;
    		}
        } else
        	directors = "null";

        try {
            String[] arrayOfString = new String[1];
	        arrayOfString[0] = String.valueOf(movie.getId());
	        ContentValues localContentValues = new ContentValues();
	        localContentValues.put("level_url", levelUrl);
	        localContentValues.put("youtube_video_id", youtubeId);
	        localContentValues.put("running_time", movie.getRunningTime());
	        localContentValues.put("release_date", date);
	        localContentValues.put("actors_str", actors);
	        localContentValues.put("directors_str", directors);
	        localContentValues.put("intro", movie.getInttroduction());
	        db.update(MovieTable, localContentValues, "id = ?", arrayOfString);    	
		} catch (Exception e) {
			
		}
        return true;
    }
    
    public boolean updateMovieAll(SQLiteDatabase db, Movie movie) {

    	String actors = "";
        for (int i = 0; i < movie.getActors().size(); i++)
            actors = actors + movie.getActors().get(i) + "、";
        String directors = "";
        for (int i = 0; i < movie.getDirectors().size(); i++)
            directors = directors + movie.getDirectors().get(i) + "、";
        
        DateFormat createFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String levelUrl;
        if (movie.getLevelUrl() != null)
            levelUrl = movie.getLevelUrl();
        else
            levelUrl = "null";
        String youtubeId;
        if (movie.getYoutubeId() != null)
            youtubeId = movie.getYoutubeId();
        else
            youtubeId = "null";
        String date;
        if (movie.getReleaseDate() != null)
            date = createFormatter.format(movie.getReleaseDate());
        else
            date = "null";
        
        try {
            String[] arrayOfString = new String[1];
	        arrayOfString[0] = String.valueOf(movie.getId());
	        ContentValues localContentValues = new ContentValues();
	        localContentValues.put("intro", movie.getInttroduction());
	        localContentValues.put("actors_str", actors);
	        localContentValues.put("directors_str", directors);
	        localContentValues.put("level_url", levelUrl);
	        localContentValues.put("youtube_video_id", youtubeId);
	        localContentValues.put("running_time", movie.getRunningTime());
	        localContentValues.put("release_date", date);
	        db.update(MovieTable, localContentValues, "id = ?", arrayOfString);    	
		} catch (Exception e) {
			
		}
        
        return true;
    }

    public Movie getMovie(SQLiteDatabase db, int movieId) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + MovieTable + " WHERE id = \'" + movieId + "\'", null);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Movie movie = new Movie();
        while (cursor.moveToNext()) {
            movie.setId(cursor.getInt(0));
            movie.setChineseName(cursor.getString(1));
            movie.setEnglishName(cursor.getString(2));
            movie.setInttroduction(cursor.getString(3));
            movie.setPosterUrl(cursor.getString(4));
            Date releaseDate = null;
            if(!cursor.isNull(5)) {
	            try {
	                releaseDate = formatter.parse(cursor.getString(5));
	            } catch (ParseException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            movie.setReleaseDate(releaseDate);
            }
            movie.setRunningTime(cursor.getInt(6));
            movie.setLevelUrl(cursor.getString(7));

            String actor = cursor.getString(14);
            String[] actors = actor.split("、");
            ArrayList<String> actorlst = new ArrayList<String>(20);
            for (int i = 0; i < actors.length; i++)
                actorlst.add(actors[i]);
            movie.setActors(actorlst);

            String director = cursor.getString(15);
            Log.d("SQLiteMovieDiary", "directors" + director);
            String[] directors = director.split("、");
            ArrayList<String> directorlst = new ArrayList<String>(21);
            for (int i = 0; i < directors.length; i++)
                directorlst.add(directors[i]);
            movie.setDirectors(directorlst);

            movie.setYoutubeId(cursor.getString(11));
        }
        cursor.close();
        return movie;
    }

    public ArrayList<Movie> getMovieList(SQLiteDatabase db, ArrayList<Integer> movieIds) throws SQLException {
        String idLst = "";
        for (int i = 0; i < movieIds.size(); i++)
            idLst = movieIds.get(i) + "," + idLst;
        idLst = idLst.substring(0, idLst.length() - 1);

        ArrayList<Movie> movie_lst = new ArrayList<Movie>();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT id, name, name_en, poster_url, release_date FROM " + MovieTable + " WHERE id in (" + idLst + ")", null);
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(0));
            movie.setChineseName(cursor.getString(1));
            movie.setEnglishName(cursor.getString(2));
            movie.setPosterUrl(cursor.getString(3));
            if(!cursor.isNull(4)) {
	            try {
	                movie.setReleaseDate(dateFormatter.parse(cursor.getString(4)));
	            } catch (ParseException e) {
	                try {
	                    movie.setReleaseDate(dateFormatter.parse("1899-11-30"));
	                } catch (ParseException e1) {
	                    // TODO Auto-generated catch block
	                    e1.printStackTrace();
	                }
	                e.printStackTrace();
	            }
            } else
				try {
					movie.setReleaseDate(dateFormatter.parse("1899-11-30"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            movie_lst.add(movie);
        }

        cursor.close();
        return movie_lst;
    }

    public ArrayList<Movie> getMovieList(SQLiteDatabase db, String filter) throws SQLException {
    	
        ArrayList<Movie> movie_lst = new ArrayList<Movie>();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Cursor cursor = null;
        if (filter == FILTER_ALL)
            cursor = db.rawQuery("SELECT * FROM " + MovieTable, null);
        else
            cursor = db.rawQuery("SELECT id, name, name_en, poster_url, release_date FROM " + MovieTable + " WHERE " 
            											+ filter + " = 't' ORDER BY release_date DESC", null);
        if(filter ==FILTER_RECENT){
        	cursor = db.rawQuery("SELECT id, name, name_en, poster_url, release_date FROM " + MovieTable + " WHERE " 
				+ filter + " = 't' ORDER BY release_date ASC", null);}
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(0));
            movie.setChineseName(cursor.getString(1));
            movie.setEnglishName(cursor.getString(2));
            movie.setPosterUrl(cursor.getString(3));
            if(!cursor.isNull(4) && !cursor.getString(4).equals("null")) {
	            try {
	                movie.setReleaseDate(dateFormatter.parse(cursor.getString(4)));
	            } catch (ParseException e) {
	                try {
	                    movie.setReleaseDate(dateFormatter.parse("1899-11-30"));
	                } catch (ParseException e1) {
	                    // TODO Auto-generated catch block
	                    e1.printStackTrace();
	                }
	                e.printStackTrace();
	            }
            } else
				try {
					movie.setReleaseDate(dateFormatter.parse("1899-11-30"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            movie_lst.add(movie);
        }

        cursor.close();
        return movie_lst;
    }
        
    public ArrayList<Movie> getMovieListWithHall(SQLiteDatabase db, ArrayList<Movie> movies) throws SQLException {
    	String idLst = "";
        for (int i = 0; i < movies.size(); i++)
            idLst = movies.get(i).getId() + "," + idLst;
        idLst = idLst.substring(0, idLst.length() - 1);
        Log.d(null, "idLst : " + idLst);
        ArrayList<Movie> movie_lst = new ArrayList<Movie>();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Cursor cursor = null;
        cursor = db.rawQuery("SELECT id, name, name_en, poster_url, release_date FROM " + MovieTable + " WHERE id in (" + idLst + ")", null);
        while (cursor.moveToNext()) {
            Movie movie = new Movie();
            movie.setId(cursor.getInt(0));
            movie.setChineseName(cursor.getString(1));
            movie.setEnglishName(cursor.getString(2));
            movie.setPosterUrl(cursor.getString(3));
            if(!cursor.isNull(4)) {
	            try {
	                movie.setReleaseDate(dateFormatter.parse(cursor.getString(4)));
	            } catch (ParseException e) {
	                try {
	                    movie.setReleaseDate(dateFormatter.parse("1899-11-30"));
	                } catch (ParseException e1) {
	                    // TODO Auto-generated catch block
	                    e1.printStackTrace();
	                }
	                e.printStackTrace();
	            }
            } else
				try {
					movie.setReleaseDate(dateFormatter.parse("1899-11-30"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            movie_lst.add(movie);
        }

        cursor.close();
        return movie_lst;
    }
    
    /*
     * Project Promote Information
     * Update
     * Get
     */
    public void updateProject(SQLiteDatabase db, ArrayList<AppProject> appProjects) {
    	db.execSQL("DROP TABLE IF EXISTS " + ProjectTable);
    	createAppProject(db);
    	insertProjects(db, appProjects);
    }
    
    public static void createAppProject(SQLiteDatabase db) {
        String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + ProjectTable + " (" 
        		+ " name VARCHAR,"
                + " title VARCHAR,"
                + " description VARCHAR,"
                + " icon_url VARCHAR,"
                + " pack VARCHAR,"
        		+ " clas VARCHAR"
                + " );";

        db.execSQL(DATABASE_CREATE_TABLE);
    }

    public boolean insertProjects(SQLiteDatabase db, ArrayList<AppProject> appProjects) {
        for (int i = 0; i < appProjects.size(); i++) {
            AppProject appProject = appProjects.get(i);
            if(appProject != null)
            	insertProject(db, appProject);
        }
        return true;
    }

    public void insertProject(SQLiteDatabase db, AppProject appProject) {    	
	    	db.execSQL( "INSERT OR IGNORE INTO " + ProjectTable + " VALUES(?,?,?,?,?,?)",
	                new String[] {appProject.getName(), appProject.getTitle(), appProject.getDescription(),
	    			appProject.getIconUrl(), appProject.getPack(), appProject.getClas()});
    }
    
    public ArrayList<AppProject> getAppProjectList(SQLiteDatabase db) throws SQLException {
    	ArrayList<AppProject> appProjectList = new ArrayList<AppProject>();
    	try {
	        Cursor cursor = null;
	        cursor = db.rawQuery("SELECT name, title, description, icon_url, pack, clas FROM " + ProjectTable + " ;", null);
	        if (cursor != null) {
	        	while(cursor.moveToNext()) {
	        		AppProject appProject = new AppProject();
	        		appProject.setName(cursor.getString(0));
	        		appProject.setTitle(cursor.getString(1));
	        		appProject.setDescription(cursor.getString(2));
	        		appProject.setIconUrl(cursor.getString(3));
	        		appProject.setPack(cursor.getString(4));
	        		appProject.setClas(cursor.getString(5));
	        		appProjectList.add(appProject);
	        	}
	            cursor.close();
	        }
	    } catch (Exception e) {
		     return appProjectList;
		}
        return appProjectList;
    }
}