package com.example.fei.yhb_20.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.example.fei.yhb_20.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 抽象出来的数据库管理类，包括了数据库的open和close
 * 但是需要指定db文件的位置
 */
public class DBManager {
    private final int BUFFER_SIZE = 1024;
    public static final String DB_NAME = "city_cn.s3db";
    public static final String PACKAGE_NAME = "com.example.fei.yhb_20";
    public static final String DB_PATH = "/data"
            + Environment.getDataDirectory().getAbsolutePath() + "/"+ PACKAGE_NAME;
    private SQLiteDatabase database;
    private Context context;
    private File file=null;
    
    public DBManager(Context context) {
    	Log.e("cc", "DBManager");
        this.context = context;
    }
 
    public void openDatabase() {
    	Log.e("cc", "openDatabase()");
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME);
    }
    public SQLiteDatabase getDatabase(){
    	Log.e("cc", "getDatabase()");
    	return this.database;
    }
 
    private SQLiteDatabase openDatabase(String dbfile) {
        try {
        	Log.e("cc", "open and return");
        	file = new File(dbfile);
            if (!file.exists()) {
            	Log.e("cc", "file");
            	InputStream is = context.getResources().openRawResource(R.raw.city);
            	if(is!=null){
            		Log.e("cc", "is null");
            	}else{
            	}
            	FileOutputStream fos = new FileOutputStream(dbfile);
            	if(is!=null){
            		Log.e("cc", "fosnull");
            	}else{
            	}
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count =is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                		Log.e("cc", "while");
                	fos.flush();
                }
                fos.close();
                is.close();
            }
            database = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
            return database;
        } catch (FileNotFoundException e) {
            Log.e("cc", "File not found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("cc", "IO exception");
            e.printStackTrace();
        } catch (Exception e){
        	Log.e("cc", "exception "+e.toString());
        }
        return null;
    }
    public void closeDatabase() {
    	Log.e("cc", "closeDatabase()");
    	if(this.database!=null)
    		this.database.close();
    }
}