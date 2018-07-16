package gnosisdevelopment.arduinobtweatherstation;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "sensors")
public class Sensors {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "date")
    public String mDate;

    @ColumnInfo(name = "temp")
    public String mTemp;


    @ColumnInfo(name = "wind")
    public String mWind;

    @ColumnInfo(name = "humidity")
    public String mHumidity;


    //Getters
    public int getUid() {
        return uid;
    }
    public String getmDate() {
        return mDate;
    }
    public String getmTemp() {
        return mTemp;
    }
    public String getmHumidity() {
        return mHumidity;
    }
    public String getmWind() {
        return mWind;
    }
    //Setters
    public void setmDate(String mDate) {
        this.mDate = mDate;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    public void setmWind(String mWind) {
        this.mWind = mWind;
    }
    public void setmTemp(String mTemp) {
        this.mTemp = mTemp;
    }
    public void setmHumidity(String mHumidity) {
        this.mHumidity = mHumidity;
    }
}
