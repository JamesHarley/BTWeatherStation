package gnosisdevelopment.arduinobtweatherstation;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.constraint.Constraints.TAG;

//new async task for file export to csv
public class ExportDatabaseCSVTask extends AsyncTask<String, String, Boolean> {

    @Override
    protected Boolean doInBackground(String... strings) {
        return null;
    }
}