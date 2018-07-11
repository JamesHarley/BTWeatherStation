package gnosisdevelopment.btweatherstation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BTWeatherStationDB.db";
    public static final String PREFS_TABLE_NAME = "prefs";
    public static final String PREFS_COLUMN_ID = "id";
    public static final String PREFS_COLUMN_TEMPSTATE = "tempstate";
    public static final String PREFS_COLUMN_HUMIDSTATE = "humidstate";
    public static final String PREFS_COLUMN_WINDSTATE = "windstate";
    public static final String PREFS_COLUMN_BTDEVICE = "btdevice";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        Log.d("BTWeather - DB", "db init");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Log.d("BTWeather - DB", "create db");
        db.execSQL(
                "create table prefs " +
                        "(id integer primary key, tempstate boolean,humidstate boolean, windstate boolean,btdevice text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS prefs");
        onCreate(db);
    }

    public boolean insertPrefs (Boolean tempstate,Boolean humidstate, Boolean windstate, String btdevice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tempstate", tempstate);
        contentValues.put("humidstate", humidstate);
        contentValues.put("windstate", windstate);
        contentValues.put("btdevice", btdevice);
        db.insert("prefs", null, contentValues);
        Log.d("BTWeather - DB", "insert db");
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from prefs where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PREFS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, Boolean tempstate,Boolean humidstate, Boolean windstate, String btdevice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tempstate", tempstate);
        contentValues.put("humidstate", humidstate);
        contentValues.put("windstate", windstate);
        contentValues.put("btdevice", btdevice);
        db.update("prefs", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deletePrefs (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("prefs",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<String> getAllPrefs() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from prefs", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(PREFS_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }
    public boolean isEmpty(){
        if( numberOfRows() >0){
            return false;
        }
        else
            return true;
    }
    public boolean updateTempState(Integer id, boolean tempstate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tempstate", tempstate);
        db.update("prefs", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        Log.d("BTWeather - DB", "update tempstate");
        return true;
    }
    public boolean updateHumidState(Integer id, boolean humidstate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("humidstate", humidstate);
        db.update("prefs", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        Log.d("BTWeather - DB", "update humid state");
        return true;
    }
    public boolean getHumidState(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from prefs", null );
       return res.getInt(res.getColumnIndex(DBHelper.PREFS_COLUMN_HUMIDSTATE))>0;

    }
    public boolean updateWindState(Integer id, boolean windstate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("windstate", windstate);
        db.update("prefs", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        Log.d("BTWeather - DB", "update windstate");
        return true;
    }
    public boolean updateBT(Integer id, String btdevice){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("btdevice", btdevice);
        db.update("prefs", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }
}