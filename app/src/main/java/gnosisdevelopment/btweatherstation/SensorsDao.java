package gnosisdevelopment.btweatherstation;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.hardware.Sensor;

import java.util.List;

@Dao
public interface SensorsDao {

    @Query("SELECT * FROM sensors")
    List<Sensors> getAll();

    @Query("SELECT * FROM sensors where date LIKE  :startDate AND date LIKE :endDate")
    Sensors findByDate(String startDate, String endDate);

    @Query("SELECT COUNT(*) from sensors")
    int countSensorss();

    @Insert
    void insertAll(Sensors... sensors);

    @Delete
    void delete(Sensors sensors);
}
