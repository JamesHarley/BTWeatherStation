package gnosisdevelopment.arduinobtweatherstation;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import com.jjoe64.graphview.series.DataPoint;

import java.util.List;

@Dao
public interface SensorsDao {

    @Query("SELECT * FROM sensors")
    List<Sensors> getAll();
    @Query("SELECT * FROM sensors")

    Cursor getAllCursor();

    @Query("SELECT * FROM sensors where date BETWEEN  :startDate AND :endDate")
    List<Sensors>  findByDate(String startDate, String endDate);

    @Query("SELECT *  FROM sensors where date BETWEEN  :startDate AND :endDate")
    List<Sensors> findTempByDate(String startDate, String endDate);



    @Query("SELECT COUNT(*) from sensors")
    int countSensorss();

    @Insert
    void insertAll(Sensors... sensors);

    @Delete
    void delete(Sensors sensors);
}
