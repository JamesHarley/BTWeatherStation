package gnosisdevelopment.btweatherstation;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.security.AccessController.getContext;


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

    final String DEGREE = "\u00b0";
    private Double temp = 28.00;
    private Double humidity = 50.10;
    private Double wind = 15.5;

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    weatherPanelInflator();
                    controlPanelDeflator();
                    return true;
                case R.id.navigation_dashboard:
                    weatherPanelDeflator();
                    controlPanelInflator();
                    return true;
                case R.id.navigation_notifications:
                    weatherPanelDeflator();
                    controlPanelDeflator();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlScoutServer = SqlScoutServer.create(this, getPackageName());
        setContentView(R.layout.activity_main);
        //Child layout
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        weatherPanelInflator();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothChatFragment fragment = new BluetoothChatFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }


         mHandlerClock = new Handler();
        //mydb.deletePrefs(2);

         mydb = new DBHelper(this);
        ;
         if(mydb.isEmpty()==true){
                 mydb.insertPrefs(tempState,humidityState,windState,"empty", celsius);
                 Log.d("BTWeather - isEmpty()", "insertpref");
             //mydb.updateContact(1,tempState,humidityState,windState,"empty");
         }
         else {
             pullFromdb();
         }
        DatabaseInitializer.populateAsync(SensorsDatabase.getSensorsDatabase(this));
    }



    protected void setTemp() {
        if (celsius == true) {
            tempText.setText(String.valueOf(temp) + DEGREE);
           graphUpdater(graphTemp, temp, tempSeries);
        } else {
            tempText.setText(String.valueOf(cToF(temp)) + DEGREE);
           graphUpdater(graphTemp, cToF(temp),tempSeries);
        }
    }

    protected void setHumidity() {
        humidityText.setText(String.valueOf(humidity) + "%");
       graphUpdater(graphHumidity, humidity,humiditySeries);
    }

    protected void setWind() {
        windText.setText(String.valueOf(wind));
       // graphUpdater(graphWind, wind,windSeries);
    }

    // convert C to F
    protected Double cToF(Double cTemp) {
        return (temp * 9 / 5.0) + 32;
    }

    protected void weatherPanelInflator() {
        weatherPanelDeflator();
        updateGraph();
       //
        //
        //
        pullFromdb();
        try{
            startRepeatingTask();
        }catch (Exception e){
            Log.d("BTWeather-error2", String.valueOf(e));
        }
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
    }

    protected void weatherPanelDeflator() {
        try{
            stopRepeatingTask();
        }catch (Exception e){
            Log.d("BTWeather-error1", String.valueOf(e));
        }
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

    }

    protected void controlPanelDeflator() {
        if (controlLayout != null) weatherView.removeView(controlLayout);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

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
    }
    public void gaugeUpdater(String [] gaugeValues){
            if(gaugeValues[0]!=null) {
                try {
                    temp = Double.valueOf(gaugeValues[0]);
                    setTemp();
                    humidity= Double.valueOf(gaugeValues[1]);
                    setHumidity();
                    wind = Double.valueOf(gaugeValues[2]);
                    setWind();
                }catch (Exception e)
                {
                    //error handling code
                    Log.d("BTWeather", e.toString());
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
    public void graphInit(GraphView graph, LineGraphSeries series){
        series.setDrawBackground(true);
        series.setColor(Color.parseColor("#8d1007"));
        series.setBackgroundColor(graphColor);
        series.setDataPointsRadius(8);
        series.setThickness(5);

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(graph.getContext()));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(2);
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


    public Runnable mHandlerTask1 = new Runnable()
    {
        @Override
        public void run() {
            updateGraph();
            mHandlerClock.postDelayed(mHandlerTask1, INTERVAL);
        }
    };

    void startRepeatingTask()
    {
        mHandlerTask1.run();
    }

    void stopRepeatingTask()
    {
        mHandlerClock.removeCallbacks(mHandlerTask1);
    }
    public void updateGraph(){
        if(tempState==true){
            if (celsius == true) {
                 graphUpdater(graphTemp, temp, tempSeries);
            } else {
                 graphUpdater(graphTemp, cToF(temp),tempSeries);
            }
        }
        if(humidityState == true)
            graphUpdater(graphHumidity, humidity,humiditySeries);
        if(windState)
            graphUpdater(graphWind, wind,windSeries);


    }

    public void btDeviceSave(String mac){
        mydb.updateBT(1, mac);
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
    }
    public String getBT() {
        mydb = new DBHelper(this);
        return mydb.getBTDevice();
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
}

