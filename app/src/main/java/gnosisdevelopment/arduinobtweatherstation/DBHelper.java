package gnosisdevelopment.arduinobtweatherstation;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "BTWeather.db";
    public static final String PREFS_TABLE_NAME = "prefs";
    public static final String PREFS_COLUMN_ID = "id";
    public static final String PREFS_COLUMN_TEMPSTATE = "tempstate";
    public static final String PREFS_COLUMN_HUMIDSTATE = "humidstate";
    public static final String PREFS_COLUMN_WINDSTATE = "windstate";
    public static final String PREFS_COLUMN_BTDEVICE = "btdevice";
    public static final String PREFS_COLUMN_CELSIUS = "celsius";

    public static final String PREFS_COLUMN_UPDATE_INTERVAL = "interval";
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
                        "(id integer primary key, tempstate boolean,humidstate boolean, windstate boolean,btdevice text, celsius boolean,interval integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS prefs");
        onCreate(db);
    }

    public boolean insertPrefs (Boolean tempstate,Boolean humidstate, Boolean windstate, String btdevice, Boolean celsius, int interval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tempstate", tempstate);
        contentValues.put("humidstate", humidstate);
        contentValues.put("windstate", windstate);
        contentValues.put("btdevice", btdevice);
        contentValues.put("celsius", celsius);
        contentValues.put("interval", interval);
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

    public boolean updateContact (Integer id, Boolean tempstate,Boolean humidstate, Boolean windstate, String btdevice, Boolean celsius, int interval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("tempstate", tempstate);
        contentValues.put("humidstate", humidstate);
        contentValues.put("windstate", windstate);
        contentValues.put("btdevice", btdevice);
        contentValues.put("celsius", celsius);
        contentValues.put("interval", interval);
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
    public boolean updateCelsius(Integer id, boolean celsius){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("celsius", celsius);
        db.update("prefs", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        Log.d("BTWeather - DB", "update celsius");
        return true;
    }
    public boolean updateInterval(Integer id, int interval){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("interval", interval);
        db.update("prefs", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        Log.d("BTWeather - DB", "update interval");
        return true;
    }

    public int getHumidState(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from prefs ",null );
            res.moveToFirst();
            return res.getInt(res.getColumnIndex(DBHelper.PREFS_COLUMN_HUMIDSTATE));}
        catch (Exception e ){Log.d("BTWeather-getHumidState", String.valueOf(e));}
        return 1;
    }
    public int getTempState(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from prefs ",null );
            res.moveToFirst();
            return res.getInt(res.getColumnIndex(DBHelper.PREFS_COLUMN_TEMPSTATE));}
        catch (Exception e ){Log.d("BTWeatherget-TempState", String.valueOf(e));}
        return 1;
    }
    public int getWindState(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from prefs ",null );
            res.moveToFirst();
            return res.getInt(res.getColumnIndex(DBHelper.PREFS_COLUMN_WINDSTATE));}
        catch (Exception e ){Log.d("BTWeatherget-WindState", String.valueOf(e));}
        return 1;
    }
    public String getBTDevice(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from prefs ",null );
            res.moveToFirst();
            return res.getString(res.getColumnIndex(DBHelper.PREFS_COLUMN_BTDEVICE));}
        catch (Exception e ){Log.d("BTWeather - getBTDevice", String.valueOf(e));}
        return "";
    }
    public int getCelsius(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from prefs ",null );
            res.moveToFirst();
            return res.getInt(res.getColumnIndex(DBHelper.PREFS_COLUMN_CELSIUS));}
        catch (Exception e ){Log.d("BTWeather - getCelsius", String.valueOf(e));}
        return 1;
    }
    public int getInterval(){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from prefs ",null );
            res.moveToFirst();
            return res.getInt(res.getColumnIndex(DBHelper.PREFS_COLUMN_UPDATE_INTERVAL));}
        catch (Exception e ){Log.d("BTWeatherget-interval", String.valueOf(e));}
        return -1;
    }

}