package gnosisdevelopment.arduinobtweatherstation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
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

public class FullGraphActivity extends AppCompatActivity {
    GraphView graph;
    private TextView mTextMessage;
    private final static int graphColor = Color.parseColor("#6a0c05");
    private MainActivity mainActivity;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.graph_hour:
                    mTextMessage.setText(R.string.graph_hour);
                    try{
                        grapher(graph,seriesBuilder(getTempData()));
                    }catch(Exception e){
                        Log.d("BTWeather-error15", e.toString());
                    }
                    return true;
                case R.id.graph_day:
                    mTextMessage.setText(R.string.graph_day);
                    return true;
                case R.id.graph_week:
                    mTextMessage.setText(R.string.graph_week);
                    return true;
            }
            return false;
        }
    };
//TODO Set Hourly as default load
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_graph);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.graphMenu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mainActivity = new MainActivity() ;
        graph = (GraphView) findViewById(R.id.fullGraph);

    }
    public void grapher(GraphView graph, LineGraphSeries series){
        series.setDrawBackground(true);
        series.setColor(Color.parseColor("#8d1007"));
        series.setBackgroundColor(graphColor);
        series.setDataPointsRadius(8);
        series.setThickness(5);
        graph.addSeries(series);
        graph.getGridLabelRenderer().setGridColor(graphColor);
        graph.getGridLabelRenderer().setHighlightZeroLines(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(graphColor);
        graph.getGridLabelRenderer().setVerticalLabelsColor(graphColor);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        graph.getGridLabelRenderer().reloadStyles();
        java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(getApplicationContext());

        graph.getGridLabelRenderer().setLabelFormatter(
                new DateAsXAxisLabelFormatter(graph.getContext(),
                        dateTimeFormatter));

    }

    public LineGraphSeries seriesBuilder(List<Sensors> sensorsList){
        DataPoint d = null;
        DataPoint[] dataPoints = new DataPoint[sensorsList.size()];

        Date date1 = new Date();
        int i = 0;
        Log.d("BTWeather-seriesbuilder", " Length of sensorlist: " + String.valueOf(sensorsList.size()));
        for(Sensors sensor: sensorsList){
            try {

                date1 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(sensor.getmDate());

                Log.d("BTWeather-sensorlistFG", String.valueOf(date1)+" - " + String.valueOf(sensor.getmTemp()) );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if( mainActivity.isCelsius()){
                d = new DataPoint(date1, Double.valueOf(

                        sensor.getmTemp())
                );
            }else{
                double tmp = mainActivity.cToF(Double.valueOf(sensor.getmTemp()));

                Log.d("BTWeather-seriesdump",String.valueOf(tmp));
                d = new DataPoint(date1, tmp);
            }

            dataPoints[i]= d;
            i++;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        return series;
    }



    //Database
    public static Date getMeYesterday(){
        //return new Date(System.currentTimeMillis()-24*60*60*1000);
        return new Date(System.currentTimeMillis()-2*60*60*1000);
    }
    public static Date getMeTomorrow(){
        return new Date(System.currentTimeMillis());
    }


    private List<Sensors> getTempData(){
        SensorsDatabase sDb = SensorsDatabase.getSensorsDatabase(this);
        List<Sensors> dataPoints = null;
        Date date1 = new Date();
        try {
             dataPoints = sDb.sensorsDao().findTempByDate(
                    DateFormat.format("MM-dd-yyyy HH:mm:ss", getMeYesterday()).toString(),
                    DateFormat.format("MM-dd-yyyy HH:mm:ss", getMeTomorrow()).toString());

        } catch (Exception e) {
            Log.d("BTWeather-error8", String.valueOf(e));
        }
        return dataPoints;

    }

}
