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

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jumplife.movieinfo.entity.Movie;
import com.jumplife.movieinfo.entity.Theater;
import com.jumplife.sharedpreferenceio.SharePreferenceIO;

public class SQLiteMovieDiary extends SQLiteOpenHelper {
    private static final String   MovieTable          = "movies";
    private static final String   TheaterTable        = "theaters";
    private static final String   DB_PATH             = "/data/data/com.jumplife.movieinfo/databases/";
    private static final String   DB_NAME             = "movieinfo.sqlite";                            // 資料庫名稱
    private static final int      DATABASE_VERSION    = 4;
    private static String DB_PATH_DATA;                                             // 資料庫版本
    private static SQLiteDatabase db;

    public static final String    FILTER_RECENT       = "is_comming";
    public static final String    FILTER_FIRST_ROUND  = "is_first_round";
    public static final String    FILTER_SECOND_ROUND = "is_second_round";
    public static final String    FILTER_TOP_10       = "is_hot";
    public static final String    FILTER_THIS_WEEK    = "is_this_week";
    public static final String    FILTER_ALL          = "FILTER_ALL";

    public SQLiteMovieDiary(Activity mActivity) {
        super(mActivity, DB_NAME, null, DATABASE_VERSION);
        
        //DB_PATH_DATA = Environment.getDataDirectory() + DB_PATH;
        DB_PATH_DATA = DB_PATH;
        if (mActivity != null) {
            SharePreferenceIO shareIO = new SharePreferenceIO(mActivity);
            if (shareIO != null) {
            	int checkVersion = DATABASE_VERSION;
            	if(shareIO.SharePreferenceO("checkversion", 0) < checkVersion) {
            		shareIO.SharePreferenceI("checkversion", checkVersion);
            		shareIO.SharePreferenceI("checkfile", true);
            		mActivity.deleteDatabase(DB_NAME);
            	}
                boolean checkFile = shareIO.SharePreferenceO("checkfile", true);
                if (checkFile) {
                	checkFileSystem(mActivity);
                    shareIO.SharePreferenceI("checkfile", false);
                }
            }
        }
        
        /*if (mActivity != null) {
            SharePreferenceIO shareIO = new SharePreferenceIO(mActivity);
            if (shareIO != null) {
                boolean checkFile = shareIO.SharePreferenceO("checkfile", true);
                if (checkFile) {
                    checkFileSystem(mActivity);
                    shareIO.SharePreferenceI("checkfile", false);
                }
            }
        }*/

        if (!checkDataBase()) {
            db = this.getWritableDatabase();
            closeDB();
        }

        // test
        // LoadDataTask tast = new LoadDataTask();
        // tast.execute();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        /*
         * boolean dbExist = checkDataBase();
         * 
         * if(!dbExist){ //By calling this method and empty database will be created into the default system path //of your application so we are gonna be able
         * to overwrite that database with our database. this.getReadableDatabase();
         * 
         * try { checkFileSystem(mActivity);
         * 
         * } catch (IOException e) {
         * 
         * throw new Error("Error copying database");
         * 
         * } }
         */

        createMovie();
        createTheater();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + MovieTable);
        db.execSQL("DROP TABLE IF EXISTS " + TheaterTable);
        Log.d("SQLiteMovieDiary", "Delete old Database");
        onCreate(db);
    }

    public SQLiteDatabase GetDB() {
        openDataBase();
        return db;
    }

    public static void openDataBase() {
        if (db == null || !db.isOpen())
            db = SQLiteDatabase.openOrCreateDatabase(DB_PATH_DATA + DB_NAME, null);
    }

    public static void closeDB() {
        if (db != null)
            db.close();
    }

    private boolean checkDataBase() {
        File dbtest = new File(DB_PATH_DATA + DB_NAME);
        if (dbtest.exists())
            return true;
        else
            return false;
    }

    private void checkFileSystem(Activity mActivity) {
        // if((new File(DB_PATH + DB_NAME)).exists() == false) {
        // 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
        File f = new File(DB_PATH_DATA);
        // 如 database 目录不存在，新建该目录
        if (!f.exists()) {
            f.mkdir();
        }

        try {
            // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
            InputStream is = mActivity.getBaseContext().getAssets().open(DB_NAME);
            // 输出流
            OutputStream os = new FileOutputStream(DB_PATH_DATA + DB_NAME);

            // 文件写入
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            // 关闭文件流
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }
    }

    /*
     * theater data
     */
    public static void createTheater() {
        String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TheaterTable + " (" + " ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,"
                + " NAME VARCHAR," + " LOCATION VARCHAR," + " AREA_ID INTEGER" + " BUY_LINK VARCHAR" + " PHONE VARCHAR" + " );";

        db.execSQL(DATABASE_CREATE_TABLE);
    }

    public long addTheater(Theater theater) {
        openDataBase();
        long rowId = insertTheater(theater);

        return rowId;
    }

    private static long insertTheater(Theater theater) {
        ContentValues args = new ContentValues();
        args.put("ID", theater.getId());
        args.put("NAME", theater.getName());
        args.put("LOCATION", theater.getAddress());
        args.put("AREA_ID", theater.getArea());
        args.put("BUY_LINK", theater.getBuyLink());
        args.put("PHONE", theater.getPhone());
        return db.insert(TheaterTable, null, args);
    }

    public int deleteTheater(long rowId) {
        return db.delete(TheaterTable, "MAIN_ID = " + rowId, null);
    }

    public static void addTheaterList(ArrayList<Theater> theaterLst) {
        openDataBase();
        Log.d("SQLiteMovieDiary", "Theater List Size : " + theaterLst.size());
        for (int i = 0; i < theaterLst.size(); i++) {
            Theater theater = theaterLst.get(i);
            insertTheater(theater);
        }
    }

    public static void deleteTheaterList() {
        openDataBase();
        db.execSQL("DROP TABLE IF EXISTS " + TheaterTable);
    }

    public ArrayList<Theater> getTheaterList() throws SQLException {
        openDataBase();
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

    public ArrayList<Theater> getTheaterList(int areaId) {
        openDataBase();
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

    public ArrayList<Theater> getTheaterListById(String theaterID) {
        String[] likeTheaters = theaterID.split(",");
        String theaterIDs = "";
        for (int i = 0; i < likeTheaters.length; i++) {
            if (!likeTheaters[i].equals("")) {
                theaterIDs = theaterIDs + likeTheaters[i];
                if (i < likeTheaters.length - 1)
                    theaterIDs = theaterIDs + ",";
            }
        }
        Log.d(null, "theaterID : " + theaterID);
        Log.d(null, "theaterIDs : " + theaterIDs);
        openDataBase();
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
    public static void createMovie() {
        String DATABASE_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieTable + " (" + " id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE,"
                + " name VARCHAR," + " name_en VARCHAR," + " intro VARCHAR," + " poster_url VARCHAR" + " release_date VARCHAR" + " running_time INTEGER"
                + " level_url VARCHAR" + " actors_str VARCHAR" + " theaters_str VARCHAR" + " youtube_video_id VARCHAR" + " is_first_round BOOL"
                + " is_second_round BOOL" + " is_hot BOOL" + " is_comming BOOL" + " is_this_week BOOL" + " );";

        db.execSQL(DATABASE_CREATE_TABLE);
    }

    public boolean insertMovies(ArrayList<Movie> movies) {
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            insertMovie(movie);
        }
        return true;
    }

    public static long insertMovie(Movie movie) {
        String actors = "";
        for (int i = 0; i < movie.getActors().size(); i++)
            actors = actors + movie.getActors().get(i) + "、";
        String directors = "";
        for (int i = 0; i < movie.getDirectors().size(); i++)
            directors = directors + movie.getDirectors().get(i) + "、";
        openDataBase();
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
                "INSERT OR IGNORE INTO \"movies\" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
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

    public int deleteMovie(long rowId) {
        openDataBase();
        int deleteId = db.delete(MovieTable, "id = " + rowId, null);
        return deleteId;
    }

    public static void deleteMovie_lst() {
        openDataBase();
        db.execSQL("DROP TABLE IF EXISTS " + MovieTable);
    }

    public void updateMovieIs(ArrayList<Integer>[] a) {
        String[] ids = new String[a.length];
        for (int i = 0; i < a.length; i++) {
            if (a[i].size() > 0) {
                ids[i] = String.valueOf(a[i].get(0));
                for (int j = 1; j < a[i].size(); j++) {
                    ids[i] = ids[i] + "," + a[i].get(j);
                }
            }
        }
        openDataBase();
        Cursor cursor = db.rawQuery("UPDATE " + MovieTable + " SET " + FILTER_FIRST_ROUND + " = CASE WHEN id IN (" + ids[0] + ")  THEN 't' ELSE 'f' END, "
                + FILTER_SECOND_ROUND + " = CASE WHEN id IN (" + ids[1] + ")  THEN 't' ELSE 'f' END, " + FILTER_RECENT + " = CASE WHEN id IN (" + ids[2]
                + ")  THEN 't' ELSE 'f' END, " + FILTER_TOP_10 + " = CASE WHEN id IN (" + ids[3] + ")  THEN 't' ELSE 'f' END, " + FILTER_THIS_WEEK
                + " = CASE WHEN id IN (" + ids[4] + ")  THEN 't' ELSE 'f' END;", null);
        cursor.moveToFirst();
        cursor.close();
    }

    public ArrayList<Integer> findMoviesIdNotInDB(ArrayList<Integer> moviesId) {
        ArrayList<Integer> returnsID = new ArrayList<Integer>();
        ArrayList<Integer> dbsID = new ArrayList<Integer>();
        openDataBase();
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

    public boolean updateMovie(Movie movie) {
        openDataBase();
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

        Cursor cursor = db.rawQuery(
                "UPDATE movies SET `level_url` = ?, `youtube_video_id` = ?, `running_time` = ?, `release_date` = ? WHERE `movies`.`id` = ?", new String[] {
                        levelUrl, youtubeId, movie.getRunningTime() + "", date, movie.getId() + "" });
        cursor.moveToFirst();
        cursor.close();
        return true;
    }

    public Movie getMovie(int movieId) throws SQLException {
        openDataBase();
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

    public ArrayList<Movie> getMovieList(ArrayList<Integer> movieIds) throws SQLException {
        openDataBase();
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

    public ArrayList<Movie> getMovieList(String filter) throws SQLException {
        openDataBase();
        ArrayList<Movie> movie_lst = new ArrayList<Movie>();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Cursor cursor = null;
        if (filter == FILTER_ALL)
            cursor = db.rawQuery("SELECT * FROM " + MovieTable, null);
        else
            cursor = db.rawQuery("SELECT id, name, name_en, poster_url, release_date FROM " + MovieTable + " WHERE " + filter + " = 't'", null);
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
        closeDB();
        return movie_lst;
    }
        
    public ArrayList<Movie> getMovieListWithHall(ArrayList<Movie> movies) throws SQLException {
    	openDataBase();
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
}