package gnosisdevelopment.arduinobtweatherstation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FullGraphActivity extends AppCompatActivity {
    GraphView graph;
    private TextView mTextMessage;
    private final static int graphColor = Color.parseColor("#6a0c05");
    private MainActivity mainActivity;
    private int focus =0;
    private int time = 0;
    GraphUtility gu;

    DBHelper mydb = new DBHelper(this);
    private boolean celsius;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.graph_hour:
                    time=1;
                    if(focus==0) {
                        mTextMessage.setText(R.string.AllHourly);

                        try {
                            gu = new GraphUtility(focus, time, 6, true, true, mainActivity, celsius);
                            gu.grapher(getApplicationContext(), graph, gu.seriesBuilder(gu.getTempData(gu.getYesterday())));
                        } catch (Exception e) {
                            Log.d("BTWeather-error15", e.toString());
                        }
                    }else {
                        if (focus == 1)
                            mTextMessage.setText(R.string.graph_hour_temp);
                        if (focus == 2)
                            mTextMessage.setText(R.string.graph_hour_humid);
                        if (focus == 3)
                            mTextMessage.setText(R.string.graph_hour_wind);
                        try {
                            gu = new GraphUtility(focus, time, 6, true, true, mainActivity, celsius);
                            gu.grapher(getApplicationContext(), graph, gu.seriesBuilder(gu.getTempData(gu.getYesterday())));
                        } catch (Exception e) {
                            Log.d("BTWeather-error15", e.toString());
                        }
                    }
                    return true;
                case R.id.graph_day:
                    time=2;
                    if(focus==0){
                        mTextMessage.setText(R.string.AllDaily);
                        try {
                            gu = new GraphUtility(focus, time, 6, true, true, mainActivity, celsius);
                            gu.grapher(getApplicationContext(), graph, gu.seriesBuilder(gu.getTempData(gu.getWeek())));
                        } catch (Exception e) {
                            Log.d("BTWeather-error15", e.toString());
                        }
                    }else {
                        if (focus == 1)
                            mTextMessage.setText(R.string.graph_day_temp);
                        if (focus == 2)
                            mTextMessage.setText(R.string.graph_day_humid);
                        if (focus == 3)
                            mTextMessage.setText(R.string.graph_day_wind);
                        try {
                            gu = new GraphUtility(focus, time, 6, true, true, mainActivity, celsius);
                            gu.grapher(getApplicationContext(), graph, gu.seriesBuilder(gu.getTempData(gu.getWeek())));
                        } catch (Exception e) {
                            Log.d("BTWeather-error15", e.toString());
                        }
                    }
                    return true;
                case R.id.graph_week:
                    time=3;
                    if(focus==0){
                        mTextMessage.setText(R.string.AllWeekly);
                        try {
                            gu = new GraphUtility(focus, time, 6, false, false, mainActivity, celsius);
                            gu.grapher(getApplicationContext(), graph, gu.seriesBuilder(gu.getTempData(gu.getMonth())));
                        } catch (Exception e) {
                            Log.d("BTWeather-error15", e.toString());
                        }
                    }else {
                        if (focus == 1)
                            mTextMessage.setText(R.string.graph_week_temp);
                        if (focus == 2)
                            mTextMessage.setText(R.string.graph_week_humid);
                        if (focus == 3)
                            mTextMessage.setText(R.string.graph_week_wind);
                        try {
                            gu = new GraphUtility(focus, time, 6, true, true, mainActivity, celsius);
                            gu.grapher(getApplicationContext(), graph, gu.seriesBuilder(gu.getTempData(gu.getMonth())));
                        } catch (Exception e) {
                            Log.d("BTWeather-error15", e.toString());
                        }
                    }

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_graph);
        Intent mIntent = getIntent();
        focus = mIntent.getIntExtra("focus", 0);
        celsius = mIntent.getBooleanExtra("celsius", false);
        mTextMessage = (TextView) findViewById(R.id.message);
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.graphMenu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mainActivity = new MainActivity();
        graph = (GraphView) findViewById(R.id.fullGraph);
        // Setting the very 1st item as home screen.
        navigation.setSelectedItemId(R.id.graph_hour);
        final Button buttonTemp = findViewById(R.id.graphBtnA);
        final Button buttonHumid = findViewById(R.id.graphBtnB);
        final Button buttonWind = findViewById(R.id.graphBtnC);
        final Button buttonAll = findViewById(R.id.graphBtnAll);

        buttonAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                focus=0;
                navigation.setSelectedItemId(R.id.graph_hour);
                // Code here executes on main thread after user presses button
                //gu = new GraphUtility(focus,time,6,false,false,mainActivity,celsius);
               // gu.grapher(getApplicationContext(),graph,gu.seriesBuilder(gu.getTempData(gu.getYesterday())));

            }
        });


        if(isTempState()){
            buttonTemp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    focus=1;
                    navigation.setSelectedItemId(R.id.graph_hour);
                    // Code here executes on main thread after user presses button
                    //gu = new GraphUtility(focus,time,6,true,true,mainActivity,celsius);
                    //gu.grapher(getApplicationContext(),graph,gu.seriesBuilder(gu.getTempData(gu.getYesterday())));
                }
            });
        }
        else{
            ViewGroup layout = (ViewGroup) buttonTemp.getParent();
            if(null!=layout){
                layout.removeView(buttonTemp);
            }
        }
        if(isHumidityState()){
            buttonHumid.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    focus=2;
                    navigation.setSelectedItemId(R.id.graph_hour);
                    // Code here executes on main thread after user presses button
                    //gu = new GraphUtility(focus,time,6,true,true,mainActivity,celsius);
                    //gu.grapher(getApplicationContext(),graph,gu.seriesBuilder(gu.getTempData(gu.getYesterday())));
                }
            });
        }
        else{
            ViewGroup layout = (ViewGroup) buttonHumid.getParent();
            if(null!=layout){
                layout.removeView(buttonHumid);
            }
        }
        if(isWindState()){
            buttonWind.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    focus=3;
                    navigation.setSelectedItemId(R.id.graph_hour);
                    // Code here executes on main thread after user presses button
                    //gu = new GraphUtility(focus,time,6,true,true,mainActivity,celsius);
                    //gu.grapher(getApplicationContext(),graph,gu.seriesBuilder(gu.getTempData(gu.getYesterday())));
                }
            });
        }
        else{
            ViewGroup layout = (ViewGroup) buttonWind.getParent();
            if(null!=layout){
                layout.removeView(buttonWind);
            }
        }




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
