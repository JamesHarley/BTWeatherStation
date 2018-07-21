package gnosisdevelopment.arduinobtweatherstation;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

public class FileUtil {
    Date date = new Date();
    Context mainContect;
    SensorsDatabase sqldb = SensorsDatabase.getSensorsDatabase(mainContect);
    Cursor c = null;
    MainActivity mainActivity = new MainActivity();
    public FileUtil(Context mainContect){
        this.mainContect = mainContect;
    }
    public boolean saveDB(){
        try {
            c = sqldb.sensorsDao().getAllCursor();
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = getStorageDir(mainContect,"ArduinoBT-weather");
            String filename = "weatherexport"+date.getTime()+ ".csv";
            // the name of the file to export with
            //FilveToPosition(i);
            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();
                Log.d("BTWeather-file", "has rows");

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);
                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                return true;
            }
        } catch(Exception ex) {
            Log.d("BTWeather-File1",String.valueOf(ex));
            if (sqldb.isOpen()) {
                sqldb.close();
                return false;
            }

        } finally {
            return true;
        }

    }
    public File getStorageDir(Context context, String folder) {
        // Get the directory for the app's private pictures directory.
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/"+folder);

        if (!file.mkdirs()) {
            Log.e("BTWeather", "Directory not created");
        }
        Log.d("BTWeather-DOWNLOADS", file.getAbsolutePath());

        return file;
    }
}
