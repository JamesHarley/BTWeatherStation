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
    private double maxYBound = 0;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_graph);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.graphMenu);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mainActivity = new MainActivity() ;
        graph = (GraphView) findViewById(R.id.fullGraph);
        // Setting the very 1st item as home screen.
        navigation.setSelectedItemId(R.id.graph_hour);
    }
    public void grapher(GraphView graph, LineGraphSeries[] seriesArray){
        LineGraphSeries series = new LineGraphSeries();
        for(int i = 0; i<seriesArray.length; i++){
            series = new LineGraphSeries();
            series = seriesArray[i];
            series.setDrawBackground(true);

            if(i == 0) {
                series.setColor(Color.parseColor("#8d1007"));
                series.setBackgroundColor(Color.parseColor("#8d1007"));
            }
            if(i == 1) {
                series.setColor(Color.parseColor("#551a8b"));
                series.setBackgroundColor(Color.parseColor("#551a8b"));
            }
            if(i == 2) {
                series.setColor(Color.parseColor("#00F00F"));
                series.setBackgroundColor(Color.parseColor("#00F00F"));
            }
            series.setDataPointsRadius(2);
            series.setThickness(2);

            graph.addSeries(series);
        }


        //graph.addSeries(series);
        graph.getGridLabelRenderer().setGridColor(graphColor);
        graph.getGridLabelRenderer().setHighlightZeroLines(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(graphColor);
        graph.getGridLabelRenderer().setVerticalLabelsColor(graphColor);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        maxYBound=maxYBound+10;
        graph.getViewport().setMaxY(maxYBound);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().reloadStyles();
        java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(getApplicationContext());

        graph.getGridLabelRenderer().setLabelFormatter(
                new DateAsXAxisLabelFormatter(graph.getContext(),
                        dateTimeFormatter));
    }

    public LineGraphSeries[] seriesBuilder(List<Sensors> sensorsList){
        DataPoint d = null;
        LineGraphSeries[] seriesArray = new LineGraphSeries[3];
        DataPoint[] dataPoints = new DataPoint[sensorsList.size()];
        DataPoint[] dataPointsH = new DataPoint[sensorsList.size()];
        DataPoint[] dataPointsW = new DataPoint[sensorsList.size()];

        Date date1 = new Date();
        int i = 0;
        Log.d("BTWeather-seriesbuilder", " Length of sensorlist: " + String.valueOf(sensorsList.size()));
        for(Sensors sensor: sensorsList){
            findMaxY(sensor);
            try {

                date1 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(sensor.getmDate());

                Log.d("BTWeather-sensorlistFG", String.valueOf(date1)+" - " + String.valueOf(sensor.getmTemp()) );
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if( mainActivity.isCelsius()){
                d = new DataPoint(date1, Double.valueOf(sensor.getmTemp()));
            }else{
                double tmp = mainActivity.cToF(Double.valueOf(sensor.getmTemp()));

                Log.d("BTWeather-seriesdump",String.valueOf(tmp));
                d = new DataPoint(date1, tmp);
            }

            dataPoints[i]= d;
            d = new DataPoint(date1, Double.valueOf(sensor.getmHumidity()));
            dataPointsH[i]=d;
            d = new DataPoint(date1, Double.valueOf(sensor.getmWind()));
            dataPointsW[i]=d;

            i++;
        }
        seriesArray[0] = new LineGraphSeries<>(dataPoints);
        seriesArray[1] = new LineGraphSeries<>(dataPointsH);
        seriesArray[2] = new LineGraphSeries<>(dataPointsW);
        return seriesArray;
    }
    public void findMaxY (Sensors sensor){

        if(Double.valueOf(sensor.getmHumidity())> maxYBound){
            maxYBound = Double.valueOf(sensor.getmHumidity());
        }
        if(Double.valueOf(sensor.getmWind())> maxYBound){
            maxYBound = Double.valueOf(sensor.getmHumidity());
        }
        if( mainActivity.isCelsius()) {
            if (Double.valueOf(sensor.getmTemp()) > maxYBound) {
                maxYBound = Double.valueOf(sensor.getmTemp());
            }
        }else if(mainActivity.cToF(Double.valueOf(sensor.getmTemp()))>maxYBound){
            maxYBound=mainActivity.cToF(Double.valueOf(sensor.getmTemp()));
        }
    }


    //Database
    public static Date getMeYesterday(){
        //return new Date(System.currentTimeMillis()-24*60*60*1000);
        return new Date(System.currentTimeMillis()-24*60*60*1000);
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
