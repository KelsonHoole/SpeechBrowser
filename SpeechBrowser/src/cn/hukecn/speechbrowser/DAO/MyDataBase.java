package cn.hukecn.speechbrowser.DAO;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.hukecn.speechbrowser.bean.BookMarkBean;
import cn.hukecn.speechbrowser.bean.HistoryBean;
import cn.hukecn.speechbrowser.bean.LocationBean;
import cn.hukecn.speechbrowser.bean.MailBean;

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
    
    private static String TableMail = "mail";
    private static String ColumnType = "type";
    private static String ColumnUserName = "username";
    private static String ColumnPassword = "password";
    
    //历史记录表   1-id,2-time,3-title,4-url
    private static String TableHistory = "history";
    private static String CulumnTime = "time";
    
    private static int DB_VERSION = 1;
    private static String CREAT_TABLE = "CREATE TABLE "+TABLE_NAME
    		+" (_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
    		+COLUMN1+" TEXT  NOT NULL,"+COLUMN2+" TEXT  NOT NULL,"
    		+COLUMN3+" TEXT  NOT NULL)";
    private static String CREAT_TABLE_BookMark = "CREATE TABLE "
    		+TableBookMark+" (_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
    		+ColumnTitle+" TEXT  NOT NULL,"
    		+ColumnUrl+" TEXT  NOT NULL)";
    private static String CREAT_TABLE_Mail = "CREATE TABLE "
    		+TableMail+" (_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
    		+ColumnType+" TEXT  NOT NULL,"
    		+ColumnUserName+" TEXT  NOT NULL,"
    		+ColumnPassword+" TEXT  NOT NULL)";
    private static String CREAT_TABLE_History = "CREATE TABLE "
    		+TableHistory+" (_id INTEGER DEFAULT '1' NOT NULL PRIMARY KEY AUTOINCREMENT,"
    		+CulumnTime+" TEXT  NOT NULL,"
    		+ColumnTitle+" TEXT  NOT NULL,"
    		+ColumnUrl+" TEXT  NOT NULL)";

    
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
        db.execSQL(CREAT_TABLE_Mail);
        db.execSQL(CREAT_TABLE_History);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("drop table if exist "+TABLE_NAME);
//        db.execSQL("drop table if exist "+TableBookMark);
//        db.execSQL("drop table if exist "+TableMail);
//        db.execSQL("drop table if exist "+TableHistory);
//        onCreate(db);
//    	db.execSQL(CREAT_TABLE_History);
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

    public long insertMail(MailBean bean)
    {
    	db = getWritableDatabase();
        ContentValues valus = new ContentValues();
        valus.put(ColumnType,bean.type);
        valus.put(ColumnUserName, bean.username);
        valus.put(ColumnPassword, bean.password);
        long ret = db.insert(TableMail, null, valus);
        db.close();
        return ret;
    }
    
    public List<MailBean> queryMail(String type)
    {
    	List<MailBean> list = new ArrayList<MailBean>();
    	db = getReadableDatabase();

        Cursor cursor = db.query(
                TableMail,
                new String[]{"_id", ColumnType, ColumnUserName,ColumnPassword},
                null,
                null, null, null, "_id");
        
        if (cursor.getCount() == 0) {
			return null;
		}
        
        while(cursor.moveToNext())
        {
        	MailBean bean = new MailBean();
        	bean.type = cursor.getString(1);
        	bean.username= cursor.getString(2);
        	bean.password= cursor.getString(3);
        	list.add(bean);
        }
        
        cursor.close();
        db.close();
    	return list;
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
	
	public void deleteHistory()
	{
		db = getWritableDatabase();
//		db.dele
	}
	
	public long insertHistory(HistoryBean bean)
    {
    	db = getWritableDatabase();
    	//插入前判断数据库中是否存在此记录
    	Cursor cursor = db.query(
                TableHistory,
                new String[]{"_id", CulumnTime,ColumnTitle, ColumnUrl},
                null,
                null, null, null, "_id desc");
        while(cursor.moveToNext())
        {
        	String title = cursor.getString(2);
        	String url = cursor.getString(3);
        	
        	if(url.equals(bean.url))
        	{//存在相同记录
       	     	db.delete(TableHistory, ColumnUrl+"=?", new String[]{bean.url});
        	}
        }
        //插入
        ContentValues valus = new ContentValues();
        valus.put(CulumnTime, bean.time);
        valus.put(ColumnTitle,bean.title);
        valus.put(ColumnUrl, bean.url);
        long ret = db.insert(TableHistory, null, valus);
        db.close();
        return ret;
    }
	public List<HistoryBean> queryHistory()
    {
    	List<HistoryBean> list = new ArrayList<HistoryBean>();
    	db = getReadableDatabase();
        Cursor cursor = db.query(
                TableHistory,
                new String[]{"_id", CulumnTime,ColumnTitle, ColumnUrl},
                null,
                null, null, null, "time desc");
        while(cursor.moveToNext())
        {
        	HistoryBean bean = new HistoryBean();
        	bean.time= cursor.getString(1);
        	bean.title = cursor.getString(2);
        	bean.url = cursor.getString(3);
        	list.add(bean);
        }
        cursor.close();
        db.close();
    	return list;
    }
}
