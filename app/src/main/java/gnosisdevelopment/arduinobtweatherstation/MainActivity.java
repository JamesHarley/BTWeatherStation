package gnosisdevelopment.arduinobtweatherstation;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.idescout.sql.SqlScoutServer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import static java.util.Calendar.SHORT;


public class MainActivity extends AppCompatActivity {
    private TextView tempText;
    private TextView humidityText;
    private TextView windText;

    private ViewGroup weatherView;
    private View tempLayout;
    private View windLayout;
    private View humidityLayout;
    private View controlLayout;
    private View notificationsLayout;

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

    private SQLiteDatabase db;
    private LineGraphSeries tempSeries;
    private LineGraphSeries windSeries;
    private LineGraphSeries humiditySeries;
    private final static int INTERVAL = 1000 * 60 * 2; //2 minutes
    private Handler mHandlerClock;
    private final static int graphColor = Color.parseColor("#6a0c05");
    private DBHelper mydb ;
    private LayoutInflater inflater;
    private SqlScoutServer sqlScoutServer;
    private SensorsDatabase sDb;
    private boolean btConnectedState = false;
    private int timeInMilliseconds= 1000;
    private Button forgetBT;
    private  TextView bluetoothText;
    BluetoothChatFragment frag;
    private Button connectBT;
    private Button aboutBt;
    private Intent aboutIntent;

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
               /** case R.id.navigation_notifications:
                    weatherPanelDeflator();
                    controlPanelDeflator();
                    notificationPanelInflater();
                    return true;**/
            }
            return false;
        }
    };
/**
    private void notificationPanelDeflator() {
        if (notificationsLayout != null) weatherView.removeView(notificationsLayout);
    }
    private void notificationPanelInflater(){
        notificationsLayout = inflater.inflate(R.layout.notifications_panel,
                (ViewGroup) findViewById(R.id.notifications_panel));

        weatherView = findViewById(R.id.container);
        weatherView.addView(notificationsLayout);
    }**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlScoutServer = SqlScoutServer.create(this, getPackageName());
        setContentView(R.layout.activity_main);
        aboutIntent = new Intent(this, About.class);

        sDb = SensorsDatabase.getSensorsDatabase(this);
        //Child layout
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }


         mydb = new DBHelper(this);
        ;
         if(mydb.isEmpty()==true){
                 mydb.insertPrefs(tempState,humidityState,windState,"empty", celsius);
                 Log.d("BTWeather - isEmpty()", "insertpref");
             mydb.close();

         }
         else {
             pullFromdb();
             mydb.close();

         }
         weatherPanelInflator();
        getData();

    }


    protected void setBtConnectedState(boolean b){

        if(btConnectedState==false && b==true){
            setRepeatingAsyncTask();
            btConnectedState =true;
        }
    }
    protected void setTemp() {
        if (celsius == true) {
            if(temp != -99.99){
                tempText.setText(String.valueOf(temp));
            }else {
                tempText.setText(" --.-- ");
            }
        } else {
            if(temp != -99.99){
                tempText.setText(String.valueOf(cToF(temp)));
            }else {
                tempText.setText(" --.-- ");
            }
        }
    }
//TODO Fix layout for graph spacing to match temp
    protected void setHumidity() {
        if(humidity != -99.99){
            humidityText.setText(String.valueOf(humidity) + "");
        }else {
            humidityText.setText(" --.-- ");
        }
    }

    protected void setWind() {
        if(wind != -99.99){
            windText.setText(String.valueOf(wind) + "");
        }else {
            windText.setText(" --.-- ");
        }
    }

    // convert C to F
    protected Double cToF(Double cTemp) {
        return (temp * 9 / 5.0) + 32;
    }

    protected void weatherPanelInflator() {
        weatherPanelDeflator();
        pullFromdb();
        getData();
        if (tempState == true) {
            tempLayout = inflater.inflate(R.layout.weather_temp,
                    (ViewGroup) findViewById(R.id.weatherPanelTemp));

            weatherView = findViewById(R.id.container);
            weatherView.addView(tempLayout);
            graphTemp = (GraphView) findViewById(R.id.graphTemp);
            tempSeries = new LineGraphSeries<>();
            graphTemp.addSeries(tempSeries);
            graphInit(graphTemp, tempSeries);
            tempText = (TextView) findViewById(R.id.temp);
        }
        if (humidityState == true) {
            humidityLayout = inflater.inflate(R.layout.weather_humidity,
                    (ViewGroup) findViewById(R.id.weatherPanelHumidity));

            weatherView = findViewById(R.id.container);
            weatherView.addView(humidityLayout);
            graphHumidity = (GraphView) findViewById(R.id.graphHumidity);
            humiditySeries = new LineGraphSeries<>();
            graphHumidity.addSeries(humiditySeries);
            graphInit(graphHumidity, humiditySeries);
            humidityText = (TextView) findViewById(R.id.humidity);
        }
        if (windState == true) {
            windLayout = inflater.inflate(R.layout.weather_wind,
                    (ViewGroup) findViewById(R.id.weatherPanelWind));

            weatherView = findViewById(R.id.container);
            weatherView.addView(windLayout);
            graphWind = (GraphView) findViewById(R.id.graphWind);
            windSeries = new LineGraphSeries<>();
            graphWind.addSeries(windSeries);
            graphInit(graphWind, windSeries);
            windText = (TextView) findViewById(R.id.wind);
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
        //updateGraph();
        getData();
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

        tempSw = (Switch) findViewById(R.id.tempSwitch);
        humidSw = (Switch) findViewById(R.id.humiditySwitch);
        windSw = (Switch) findViewById(R.id.windSwitch);

        radioC = findViewById(R.id.radio_celsius);
        radioF = findViewById(R.id.radio_fahrenheit);

        if (tempState == true) tempSw.setChecked(true);
        if (humidityState == true) humidSw.setChecked(true);
        if (windState == true) windSw.setChecked(true);
        mydb = new DBHelper(this);
        tempSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked == false) {
                    tempState = false;
                }
                if (isChecked == true) {
                    tempState = true;
                }
                mydb.updateTempState(1, tempState);
            }
        });
        humidSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked == false) {
                    humidityState = false;
                }
                if (isChecked == true) {
                    humidityState = true;
                }
                mydb.updateHumidState(1,humidityState);
            }
        });
        windSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked == false) {
                    windState = false;
                }
                if (isChecked == true) {
                    windState = true;
                }
                mydb.updateWindState(1,windState);
            }
        });
        radioView = findViewById(R.id.radio_fahrenheit);
        if (celsius == true) {
            radioC.setChecked(true);
        } else {
            radioF.setChecked(true);
        }
        onRadioButtonClicked(radioView);
        mydb.close();

        forgetBT = findViewById(R.id.btButton);
        bluetoothText = findViewById(R.id.btButtonText);
        final ViewGroup layout = (ViewGroup) forgetBT.getParent();
        if(!(getBT().matches("empty"))){
            forgetBT.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    removeBtDevice();
                    if(null!=layout){
                        layout.removeView(forgetBT);
                    }
                }
            });
        }else {
            layout.removeView(forgetBT);
            layout.removeView(bluetoothText);
        }
        aboutBt = findViewById(R.id.aboutButton);
        aboutBt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(aboutIntent);
            }
        });
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
                break;
            case R.id.radio_fahrenheit:
                if (checked)
                    celsius = false;
                    mydb.updateCelsius(1,false);
                break;
        }
        mydb.close();
    }
    public void gaugeUpdater(String [] gaugeValues){
            if(gaugeValues[0]!=null && gaugeValues[1] !=null && gaugeValues[2]!=null)  {
                try {
                    if(tempState !=false){
                        temp = Double.valueOf(gaugeValues[0]);
                        setTemp();
                    }
                    if(humidityState !=false){
                        humidity= Double.valueOf(gaugeValues[1]);
                        setHumidity();
                    }
                    if(windState !=false){
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

    public void graphUpdater(GraphView graph, double data, LineGraphSeries series){
        try {
            series.appendData(new DataPoint(getCurrentTime(), data), true, 40);
            LineGraphSeries<DataPoint> seriesA = series;
            graph.addSeries(seriesA);
        }
        catch (Exception e){

        }
    }
    public void graphUpdater(GraphView graph, double data, LineGraphSeries series, Date date){
        try {
            series.appendData(new DataPoint(date, data), true, 40);
            LineGraphSeries<DataPoint> seriesA = series;
            graph.addSeries(seriesA);
        }
        catch (Exception e){

        }
    }
    public void graphInit(GraphView graph, LineGraphSeries series){
        series.setDrawBackground(true);
        series.setColor(Color.parseColor("#8d1007"));
        series.setBackgroundColor(graphColor);
        series.setDataPointsRadius(8);
        series.setThickness(5);
        // set date label formatter
        java.text.DateFormat d = null;
        d.getDateInstance(SHORT);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext()) );
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setHumanRounding(true);
        // legend
        // styling grid/labels
        graph.getGridLabelRenderer().setGridColor(graphColor);
        graph.getGridLabelRenderer().setHighlightZeroLines(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(graphColor);
        graph.getGridLabelRenderer().setVerticalLabelsColor(graphColor);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        graph.getGridLabelRenderer().reloadStyles();

        // styling viewport
        //graph.getViewport().setBackgroundColor(Color.argb(255, 222, 222, 222));
        graph.getViewport().setDrawBorder(false);
        graph.getViewport().setBorderColor(graphColor);
    }

    public java.util.Date  getCurrentTime() {
        //String mytime = (DateFormat.format("dd-MM-yyyy hh:mm:ss", ).toString());
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();
        return d1;
    }


    public void updateGraph(double temp, double humidity, double wind, Date date){
        Log.d("BTWeather", "updateGraph()");
        if(tempState==true){
            if (celsius == true) {
                graphUpdater(graphTemp, temp, tempSeries,date);
            } else {
                graphUpdater(graphTemp, cToF(temp),tempSeries,date);
            }
        }
        if(humidityState == true)
            graphUpdater(graphHumidity, humidity,humiditySeries,date);
        if(windState==true)
            graphUpdater(graphWind, wind,windSeries,date);

    }
    public void removeBtDevice(){
        mydb = new DBHelper(this);
        mydb.updateBT(1, "empty");
        mydb.close();
    }
    public void btDeviceSave(String mac){
        mydb = new DBHelper(this);
        mydb.updateBT(1, mac);
        mydb.close();
        getData();
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


    private void setRepeatingAsyncTask() {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {

                        try{
                            
                            if(wind != -99.99 && temp != -99.99 & humidity!=-99.99) {
                                DatabaseInitializer.populateAsync(sDb,
                                        String.valueOf(temp),
                                        String.valueOf(humidity),
                                        String.valueOf(wind),
                                        dbDate());

                                Log.d("BTWeather-storeAsync", "Store success");
                            }
                        } catch (Exception e) {
                            //error handling code
                            Log.d("BTWeather4", e.toString());
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

    }
    public String dbDate(){
        Date d = new Date();
       return  DateFormat.format("MM-dd-yyyy hh:mm:ss",d).toString();
    }
    public static Date getMeYesterday(){
        return new Date(System.currentTimeMillis()-24*60*60*1000);
    }
    public static Date getMeTomorrow(){
        return new Date(System.currentTimeMillis()+24*60*60*1000);
    }
    private void getData(){
        sDb = SensorsDatabase.getSensorsDatabase(this);
        final Handler handler2 = new Handler();
            try{
                handler2.post(new Runnable() {
                    public void run() {
                        handler2.post(new Runnable() {
                            public void run() {
                                Date date1 = new Date();
                                try {
                                    List<Sensors> sensorList = sDb.sensorsDao().findByDate(
                                            DateFormat.format("MM-dd-yyyy hh:mm:ss", getMeYesterday()).toString(),
                                            DateFormat.format("MM-dd-yyyy hh:mm:ss", getMeTomorrow()).toString());
                                    for (Sensors sensor : sensorList) {
                                        double tmp = Double.valueOf(sensor.getmTemp());
                                        double hmd = Double.valueOf(sensor.getmHumidity());
                                        double wnd = Double.valueOf(sensor.getmWind());
                                        String sdate = sensor.getmDate();
                                        try {
                                            date1 = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss").parse(sdate);
                                        } catch (ParseException e) {
                                            Log.d("BTWeather-error6", String.valueOf(e));
                                        }
                                        try {
                                            updateGraph(tmp, hmd, wnd, date1);

                                        } catch (Exception e) {
                                            Log.d("BTWeather-error7", String.valueOf(e));
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.d("BTWeather-error8", String.valueOf(e));
                                }
                            }

                            ;
                        });
                    }
                });
            }catch (Exception e){
                Log.d("BTWeather-error12", String.valueOf(e));
            }

    }

}

