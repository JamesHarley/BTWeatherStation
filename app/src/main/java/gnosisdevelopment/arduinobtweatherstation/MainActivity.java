package gnosisdevelopment.arduinobtweatherstation;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.idescout.sql.SqlScoutServer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.GraphViewXML;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.pawelkleczkowski.customgauge.CustomGauge;


public class MainActivity extends AppCompatActivity {
    private TextView tempText;
    private TextView humidityText;
    private TextView windText;

    private ViewGroup weatherView;
    private View tempLayout;
    private View windLayout;
    private View humidityLayout;
    private View controlLayout;

    private View radioView;
    private Switch tempSw;
    private Switch humidSw;
    private Switch windSw;

    private boolean tempState = true;
    private boolean humidityState = true;
    private boolean windState = true;
    private boolean celsius = false;
    private RadioButton radioF;
    private RadioButton radioC;


    private Double temp = -99.99;
    private Double humidity = -99.99;
    private Double wind = -99.99;

    private GraphView graphTemp;
    private GraphView graphHumidity;
    private GraphView graphWind;
    private CustomGauge gauge1;
    private boolean threadState =false;

    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes

    private DBHelper mydb ;
    private LayoutInflater inflater;
    private SqlScoutServer sqlScoutServer;
    private SensorsDatabase sDb;
    private boolean btConnectedState = false;
    private int timeInMilliseconds= 30000;
    private BluetoothChatFragment fragment;
    final Handler handler = new Handler();
    TextView bluetoothStateText;

    private Intent aboutIntent;
    private Intent fullGraphIntent;
    protected  Intent mainIntent;
    private Activity mActivity;
    private int focus;
    EditText et;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    weatherPanelInflator();
                    controlPanelDeflator();
                    //notificationPanelDeflator();
                    return true;
                case R.id.navigation_dashboard:
                    weatherPanelDeflator();
                    controlPanelInflator();
                    //notificationPanelDeflator();
                    return true;

            }
            return false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        sqlScoutServer = SqlScoutServer.create(this, getPackageName());
        setContentView(R.layout.activity_main);
        aboutIntent = new Intent(this, About.class);
        fullGraphIntent = new Intent(this, FullGraphActivity.class);
        mainIntent = new Intent(this, MainActivity.class);
        mActivity = MainActivity.this;

        sDb = SensorsDatabase.getSensorsDatabase(this);
        //Child layout
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }


         mydb = new DBHelper(this);
        ;
         if(mydb.isEmpty()){
                 mydb.insertPrefs(tempState,humidityState,windState,"empty", celsius, timeInMilliseconds);
                 Log.d("BTWeather - isEmpty()", "insertpref");
             mydb.close();

         }
         else {
             pullFromdb();
             mydb.close();

         }
         weatherPanelInflator();
         //getData();
    }


    public void setBtConnectedState(boolean b){

        if(!btConnectedState && b){
            if(!windState)
                wind = 0.0;

            if(!humidityState)
                humidity = 0.0;

            if(!tempState)
                temp = 0.0;
            if(!threadState) {
                setRepeatingAsyncTask();
            }
            btConnectedState =true;
        }else{
            btConnectedState=b;
        }
    }
    public boolean getBtConnectedState(){
        return btConnectedState;
    }
    protected void setTemp() {
        if(tempText != null) {
            if (celsius) {
                if (temp != -99.99) {
                    tempText.setText(String.valueOf(temp));
                   // gauge1.setValue(temp.intValue());
                } else {
                    tempText.setText(" --.-- ");
                }
            } else {
                if (temp != -99.99) {
                    tempText.setText(String.valueOf(cToF(temp)));
                   // gauge1.setValue(cToF(temp).intValue());
                } else {
                    tempText.setText(" --.-- ");
                }
            }
        }
    }
    protected void setHumidity() {
        if(humidityText != null) {
            if (humidity != -99.99) {
                humidityText.setText(String.valueOf(humidity));
            } else {
                humidityText.setText(" --.-- ");
            }
        }
    }

    protected void setWind() {
        if(windText!=null) {
            if (wind != -99.99) {
                if (wind == 0) {
                    windText.setText("00.0");
                } else
                    windText.setText(String.valueOf(wind));
            } else {
                windText.setText(" --.-- ");
            }
        }
    }
    // convert C to F
    protected Double cToF(Double cTemp) {
        return (cTemp * 9 / 5.0) + 32;
    }

    public void gaugeUpdater(String [] gaugeValues){
        if(gaugeValues[0]!=null && gaugeValues[1] !=null && gaugeValues[2]!=null)  {
            try {
                if(tempState){
                    temp = Double.valueOf(gaugeValues[0]);
                    setTemp();

                }
                if(humidityState){
                    humidity= Double.valueOf(gaugeValues[1]);
                    setHumidity();
                }

                if(windState){
                    wind = Double.valueOf(gaugeValues[2]);
                    setWind();
                }
            }catch (Exception e)
            {
                //error handling code
                Log.d("BTWeather3", e.toString());
            }
        }
    }


    protected void weatherPanelInflator() {
        weatherPanelDeflator();
        pullFromdb();
        if (tempState) {
            tempLayout = inflater.inflate(R.layout.weather_temp,
                    (ViewGroup) findViewById(R.id.weatherPanelTemp));

            weatherView = findViewById(R.id.container);
            weatherView.addView(tempLayout);
            graphTemp = (GraphView) findViewById(R.id.graphTemp);
            try {
                GraphUtility gu = new GraphUtility(1,1,3,false,false, this,celsius);
                gu.grapher( this,graphTemp, gu.seriesBuilder(gu.getTempData(gu.getYesterday())),gu.getYesterdayDouble());
            }catch(Exception e){
                Log.d("BTWeather-error24", e.toString());
            }
            tempText = (TextView) findViewById(R.id.temp);
            View graphAreaTemp =findViewById(R.id.graphTemp);
            graphAreaTemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullGraphIntent.putExtra("focus", 1);
                    fullGraphIntent.putExtra("celsius", celsius);
                    startActivity(fullGraphIntent);
                }
            });
            //gauge1 = findViewById(R.id.gauge1);

            //gauge1.setEndValue(100);
        }
        if (humidityState) {
            humidityLayout = inflater.inflate(R.layout.weather_humidity,
                    (ViewGroup) findViewById(R.id.weatherPanelHumidity));

            weatherView = findViewById(R.id.container);
            weatherView.addView(humidityLayout);
            graphHumidity = (GraphView) findViewById(R.id.graphHumidity);

            try {
                GraphUtility gu = new GraphUtility(2,1,3,false,false, this,celsius);
                gu.grapher( this,graphHumidity, gu.seriesBuilder(gu.getTempData(gu.getYesterday())),gu.getYesterdayDouble());
            }catch(Exception e){
                Log.d("BTWeather-error24", e.toString());
            }
            humidityText = (TextView) findViewById(R.id.humidity);
            View graphAreaHumid =findViewById(R.id.graphHumidity);
            graphAreaHumid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullGraphIntent.putExtra("focus", 2);
                    fullGraphIntent.putExtra("celsius", celsius);
                    startActivity(fullGraphIntent);
                }
            });
        }
        if (windState) {
            windLayout = inflater.inflate(R.layout.weather_wind,
                    (ViewGroup) findViewById(R.id.weatherPanelWind));

            weatherView = findViewById(R.id.container);
            weatherView.addView(windLayout);
            graphWind = (GraphView) findViewById(R.id.graphWind);


            try {
                GraphUtility gu = new GraphUtility(3,1,3,false,false, this,celsius);
                gu.grapher( this,graphWind, gu.seriesBuilder(gu.getTempData(gu.getYesterday())),gu.getYesterdayDouble());
            }catch(Exception e){
                Log.d("BTWeather-error25", e.toString());
            }
            windText = (TextView) findViewById(R.id.wind);

            View graphAreaWind =findViewById(R.id.graphWind);
            graphAreaWind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fullGraphIntent.putExtra("focus", 3);
                    fullGraphIntent.putExtra("celsius", celsius);
                    startActivity(fullGraphIntent);
                }
            });

        }
        if (temp != null && tempText != null) {
            setTemp();
        }
        if (humidity != null && humidityText != null) {
            setHumidity();
        }
        if (wind != null && windText != null) {
            setWind();
        }

    }

    protected void weatherPanelDeflator() {

        if (tempLayout != null) weatherView.removeView(tempLayout);
        if (humidityLayout != null) weatherView.removeView(humidityLayout);
        if (windLayout != null) weatherView.removeView(windLayout);
    }

    protected void controlPanelInflator() {
        controlPanelDeflator();
        controlLayout = inflater.inflate(R.layout.control_panel,
                (ViewGroup) findViewById(R.id.controlPanel));
        weatherView = findViewById(R.id.container);
        weatherView.addView(controlLayout);
        bluetoothStateText= findViewById(R.id.bluetootStateText);

        if(getBtConnectedState()) {

            (mActivity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bluetoothStateText.setText("Bluetooth is connected");
                }
            });
        }
        else {
            (mActivity).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bluetoothStateText.setText("Bluetooth is disconnected");
                }
            });
        }
        tempSw = (Switch) findViewById(R.id.tempSwitch);
        humidSw = (Switch) findViewById(R.id.humiditySwitch);
        windSw = (Switch) findViewById(R.id.windSwitch);

        radioC = findViewById(R.id.radio_celsius);
        radioF = findViewById(R.id.radio_fahrenheit);

        if (tempState) tempSw.setChecked(true);
        if (humidityState) humidSw.setChecked(true);
        if (windState) windSw.setChecked(true);
        mydb = new DBHelper(this);
        tempSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (!isChecked) {
                    tempState = false;
                }
                if (isChecked) {
                    tempState = true;
                }
                mydb.updateTempState(1, tempState);
            }
        });
        humidSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (!isChecked ) {
                    humidityState = false;
                }
                if (isChecked ) {
                    humidityState = true;
                }
                mydb.updateHumidState(1,humidityState);
            }
        });
        windSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (!isChecked) {
                    windState = false;
                }
                if (isChecked ) {
                    windState = true;
                }
                mydb.updateWindState(1,windState);
            }
        });

        if (celsius) {
            radioC.setChecked(true);
        } else {
            radioF.setChecked(true);
        }
        //nRadioButtonClicked(radioView);
        mydb.close();


        et = findViewById(R.id.selectInterval);
        et.setText(String.valueOf(timeInMilliseconds/1000));
        Button saveBt = findViewById(R.id.saveIntervalButton);

        saveBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mydb = new DBHelper(getApplicationContext());
                timeInMilliseconds = 1000 * Integer.valueOf(et.getText().toString());
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (imm.isActive()){
                    // Hide keyboard
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                }
                try {
                    mydb.updateInterval(1, timeInMilliseconds);

                    Toast.makeText(MainActivity.this,
                            "Database save interval set to  " +
                                    String.valueOf(timeInMilliseconds/1000) + " minutes",
                            Toast.LENGTH_SHORT).show();
                    mActivity.recreate();
                    }catch (Exception e){
                    Toast.makeText(MainActivity.this,
                            "Database save interval Failed - Report Bug  " +
                                    String.valueOf(e.toString()), Toast.LENGTH_LONG).show();
                    Log.d("BTWeather-error16", e.toString());
                }
                mydb.close();
            }
            });
        Button exportbt = findViewById(R.id.exportBt);
        exportbt.setOnClickListener((new View.OnClickListener() {
            public void onClick(View v) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission

                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    FileUtil fu = new FileUtil(getApplicationContext());
                    try{
                        if(fu.saveDB()) {


                            Toast.makeText(MainActivity.this,
                                    "Export file saved in Downloads/ArduinoBT-Weather",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d("BTWeather-error26", "Export failed with false");
                        }
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this,
                                "Database export Failed - Report Bug  " +
                                        String.valueOf(e.toString()), Toast.LENGTH_LONG).show();
                        Log.d("BTWeather-error26", e.toString());
                    }
                }
            }
        }));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // write-related task you need to do.
                    FileUtil fu = new FileUtil(getApplicationContext());
                    try{
                        if(fu.saveDB()) {


                            Toast.makeText(MainActivity.this,
                                    "Export file saved in Downloads/ArduinoBT-Weather",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d("BTWeather-error26", "Export failed with false");
                        }
                    }catch(Exception e){
                        Toast.makeText(MainActivity.this,
                                "Database export Failed - Report Bug  " +
                                        String.valueOf(e.toString()), Toast.LENGTH_LONG).show();
                        Log.d("BTWeather-error26", e.toString());
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this,
                            "Permission was denied. Export needs permission to write to directory ",
                            Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
    protected void controlPanelDeflator() {
        if (controlLayout != null) weatherView.removeView(controlLayout);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        mydb = new DBHelper(this);
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_celsius:
                if (checked)
                    celsius = true;
                    mydb.updateCelsius(1,true);
                    mydb.close();
                break;
            case R.id.radio_fahrenheit:
                if (checked)
                    celsius = false;
                    mydb.updateCelsius(1,false);
                    mydb.close();
                break;
        }

    }

    public void removeBtDevice(){
        try{
            mydb = new DBHelper(this);
            mydb.updateBT(1, "empty");
            mydb.close();
            Toast.makeText(MainActivity.this,
                    "Bluetooth Device Removed", Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Log.d("BTWeather-error17", e.toString());
        }

    }
    public void btDeviceSave(String mac){
        mydb = new DBHelper(this);
        mydb.updateBT(1, mac);
        mydb.close();
        //getData();
    }
    public void pullFromdb(){
        Log.d("BTWeather pullfromdb", "pull");
        mydb = new DBHelper(this);

        if( mydb.getHumidState() == 0){
            humidityState = false;
        }else
            humidityState = true;
        if( mydb.getTempState() == 0){
            tempState = false;
        }else
            tempState = true;
        if( mydb.getWindState() == 0){
            windState = false;
        }else
        windState = true;
        if( mydb.getCelsius() == 0){
            celsius = false;
        }else
            celsius = true;
        if(mydb.getInterval()!= -1){
            timeInMilliseconds = mydb.getInterval();
        }
        mydb.close();
    }
    public String getBT() {
        mydb = new DBHelper(this);
        String bt = mydb.getBTDevice();
        mydb.close();
        return bt;
    }

    public String getTemp(){
        return String.valueOf(temp);
    }
    public String getHumidity(){
        return String.valueOf(humidity);
    }
    public String getWind(){
        return String.valueOf(wind);
    }
    public String getDate(){
        return String.valueOf(getCurrentTime());
    }
    public java.util.Date  getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        return d1;
    }
    public boolean isTempState() {
        DBHelper mydb = new DBHelper(this);
        int ts = mydb.getTempState();
        Log.d(Constants.LOG_TAG,"tempState:" + String.valueOf(ts));

        mydb.close();
        if(ts == 0){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean isHumidityState() {
        DBHelper mydb = new DBHelper(this);
        int hs = mydb.getHumidState();

        Log.d(Constants.LOG_TAG,"humidState:" + String.valueOf(hs));
        mydb.close();
        if(hs == 0){
            return false;
        }
        else{
            return true;
        }
    }

    public boolean isWindState() {
        DBHelper mydb = new DBHelper(this);
        int ws = mydb.getWindState();

        Log.d(Constants.LOG_TAG,"windState:" + String.valueOf(ws));
        mydb.close();
        if(ws == 0){
            return false;
        }
        else{
            return true;
        }
    }
    public boolean isCelsius(Context C) {

        return celsius;
    }


    private void setRepeatingAsyncTask() {

        try{
            threadState = true;
            sDb = SensorsDatabase.getSensorsDatabase(this);
            Timer timer = new Timer();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            if(btConnectedState) {
                                try {
                                    if (wind != -99.99 && temp != -99.99 & humidity != -99.99) {
                                        DatabaseInitializer.populateAsync(sDb,
                                                String.valueOf(temp),
                                                String.valueOf(humidity),
                                                String.valueOf(wind),
                                                dbDate());

                                        Log.d("BTWeather-storeAsync", "Store success");
                                        Log.d(Constants.LOG_TAG, "Btconnected state:"+btConnectedState);
                                    } else {
                                        Log.d("BTWeather-storeAsync", "Store no-success");
                                    }
                                } catch (Exception e) {
                                    //error handling code
                                    Log.d("BTWeather4", e.toString());
                                }
                            }else{
                                Log.d(Constants.LOG_TAG,"BTdisconnected");

                            }
                        }
                    });

                }
            };

            try{
                timer.schedule(task, 0, 60 * timeInMilliseconds);
                Log.d("BTWeather-storeAsync", "TimerStart");
            }catch (Exception e) {
                Log.d("BTWeather-error11", e.toString());
            }
        }catch(Exception e){
            Log.d("BTWeather-error13", e.toString());
        }


    }
    public String dbDate(){
        Date d = new Date();
       return  DateFormat.format("MM-dd-yyyy HH:mm:ss",d).toString();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        if(findViewById(R.id.controlPanel) != null){
            if(findViewById(R.id.controlPanel).getVisibility() == View.VISIBLE){
                controlPanelDeflator();
                weatherPanelInflator();
            }else{
                super.onBackPressed();
            }
        }
        else{
            super.onBackPressed();
        }

    }
}

