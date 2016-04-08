package cn.hukecn.speechbrowser.DAO;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.hukecn.speechbrowser.bean.BookMarkBean;
import cn.hukecn.speechbrowser.bean.LocationBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelson on 2015/11/9.
 */
public class MyDataBase extends SQLiteOpenHelper {

    private static String DBNAME = "SpeechBrowser";
    //private static String ID="id";
    private static String TABLE_NAME = "location";
    private static String COLUMN2 = "longitude";
    private static String COLUMN3 = "latitude";
    private static String COLUMN1 = "time";
 
    private static String TableBookMark = "bookmark";
    private static String ColumnUrl = "url";
    private static String ColumnTitle = "title";
    
    
    private static int DB_VERSION = 2;
    private static String CREAT_TABLE = "CREATE TABLE "+TABLE_NAME+" (_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"+COLUMN1+" TEXT  NOT NULL,"+COLUMN2+" TEXT  NOT NULL,"+COLUMN3+" TEXT  NOT NULL)";
    private static String CREAT_TABLE_BookMark = "CREATE TABLE "+TableBookMark+" (_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"+ColumnTitle+" TEXT  NOT NULL,"+ColumnUrl+" TEXT  NOT NULL)";

    private SQLiteDatabase db;
    private static MyDataBase myDB;
    
    public static MyDataBase getInstance()
    {
    	return myDB;
    }
    
    public static void init(Context context){
    	myDB = new MyDataBase(context);
    }

    private MyDataBase(Context context) {
        this(context, DBNAME, null,DB_VERSION);
    }

    private MyDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
    	super(context, DBNAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAT_TABLE);
        db.execSQL(CREAT_TABLE_BookMark);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exist "+TABLE_NAME);
        onCreate(db);
    }

    public long insert(LocationBean bean)
    {
        db = getWritableDatabase();
        ContentValues valus = new ContentValues();
        valus.put(COLUMN1,bean.time);
        valus.put(COLUMN3, bean.latitude);
        valus.put(COLUMN2,bean.longitude);
        long ret = db.insert(TABLE_NAME, null, valus);
        db.close();
        return ret;
    }

    public LocationBean query()
    {
        db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{"_id", COLUMN1, COLUMN2,COLUMN3},
                null,
                null, null, null, "_id desc");
        if (cursor.getCount() == 0) {
			return null;
		}
        
        cursor.moveToNext();
        LocationBean bean = new LocationBean();
        bean.time = cursor.getString(1);
        bean.longitude = cursor.getString(2);
        bean.latitude = cursor.getString(3);
        
        cursor.close();
        db.close();
        return bean;
    }

    public long insertBookMark(BookMarkBean bean)
    {
    	db = getWritableDatabase();
        ContentValues valus = new ContentValues();
        valus.put(ColumnTitle,bean.title);
        valus.put(ColumnUrl, bean.url);
        long ret = db.insert(TableBookMark, null, valus);
        db.close();
        return ret;
    }
    
    public List<BookMarkBean> queryBookMark()
    {
    	List<BookMarkBean> list = new ArrayList<BookMarkBean>();
    	db = getReadableDatabase();

        Cursor cursor = db.query(
                TableBookMark,
                new String[]{"_id", ColumnTitle, ColumnUrl},
                null,
                null, null, null, "_id");
        while(cursor.moveToNext())
        {
        	BookMarkBean bean = new BookMarkBean();
        	bean.title = cursor.getString(1);
        	bean.url = cursor.getString(2);
        	list.add(bean);
        }
        cursor.close();
        db.close();
    	return list;
    }

	public int delete(BookMarkBean bean) {
		// TODO Auto-generated method stub
		 SQLiteDatabase db = getWritableDatabase();

	        int return_code = db.delete(TableBookMark, ColumnUrl+"=?", new String[]{bean.url});

	        db.close();

	        return  return_code;
	}
}
