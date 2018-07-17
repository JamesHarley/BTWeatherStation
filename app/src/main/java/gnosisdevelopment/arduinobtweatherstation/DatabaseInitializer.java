package gnosisdevelopment.arduinobtweatherstation;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;


import com.jjoe64.graphview.GraphView;

import java.util.List;

public class DatabaseInitializer {

    private static String temp;
    private static String humidity;
    private static String wind;
    private static String date;


    private static final String TAG = DatabaseInitializer.class.getName();

    public static void populateAsync(@NonNull final SensorsDatabase db, String mtemp, String mhumidity, String mwind, String mdate) {
        temp = mtemp;
        humidity = mhumidity;
        wind = mwind;
        date = mdate;

        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final SensorsDatabase db) {
        populateWithData(db);
    }



    private static Sensors addSensors(final SensorsDatabase db, Sensors sensors) {
        db.sensorsDao().insertAll(sensors);
        return sensors;
    }

    private static void populateWithTestData(SensorsDatabase db) {
        Sensors sensors = new Sensors();
        sensors.setmDate("07/11/18");
        sensors.setmTemp("70.00");
        sensors.setmHumidity("15.00");
        sensors.setmWind("5.00");
        addSensors(db, sensors);

        List<Sensors> sensorsList = db.sensorsDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + sensorsList.size());
    }
    private static void populateWithData(SensorsDatabase db) {
        Sensors sensors = new Sensors();
        sensors.setmDate(date);
        sensors.setmTemp(temp);
        sensors.setmHumidity(humidity);
        sensors.setmWind(wind
        );
        addSensors(db, sensors);

        List<Sensors> sensorsList = db.sensorsDao().getAll();
        Log.d(DatabaseInitializer.TAG, "Rows Count: " + sensorsList.size());
    }


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final SensorsDatabase mDb;

        PopulateDbAsync(SensorsDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {

            populateWithData(mDb);
            return null;
        }

    }

}
