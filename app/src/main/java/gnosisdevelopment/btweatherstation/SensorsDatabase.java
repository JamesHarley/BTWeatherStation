package gnosisdevelopment.btweatherstation;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Sensors.class}, version = 1)
public abstract class SensorsDatabase extends RoomDatabase {

    private static SensorsDatabase INSTANCE;

    public abstract SensorsDao sensorsDao();

    public static SensorsDatabase getSensorsDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), SensorsDatabase.class, "sensors-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}