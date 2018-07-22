package gnosisdevelopment.arduinobtweatherstation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GraphUtility {
    public int focus;
    public Activity activity;
    public MainActivity mainActivity;
    public Context context;
    Constants constants;


    private final static int graphColor = Color.parseColor("#6a0c05");
    private double maxYBound = 0;
    private double minYBound = 999;
    private int time = 0;
    private int labelCount = 0;
    private boolean maxy = true;
    private boolean miny = true;
    private boolean celsius;
    public GraphUtility(int focus, int time, int labelCount,boolean maxy, boolean miny, MainActivity mainActivity, boolean celsius) {

        this.focus = focus;
        this.time = time;
        this.labelCount = labelCount;
        context = mainActivity;
        this.mainActivity = mainActivity;
        this.miny = miny;
        this.maxy = maxy;
        this.celsius = celsius;
        /**
        try{
            celsius = mainActivity.isCelsius(mainActivity);

        }catch(Exception e){
            Log.d("BTWeather-GraphUtility", String.valueOf(e));
        }
        **/
    }



    public void grapher(Context context, GraphView graph, LineGraphSeries[] seriesArray){
        try{
            LineGraphSeries series = new LineGraphSeries();
            if(focus==0){
                for(int i = 0; i<seriesArray.length; i++){
                    // series = new LineGraphSeries();
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
                        series.setColor(Color.parseColor("#FF0008F0"));
                        series.setBackgroundColor(Color.parseColor("#FF0008F0"));
                    }

                    series.setDataPointsRadius(2);
                    series.setThickness(2);

                    graph.addSeries(series);
                }
            }
            if(focus == 1){
                series = seriesArray[0];
                series.setDrawBackground(true);
                series.setColor(Color.parseColor("#8d1007"));
                series.setBackgroundColor(Color.parseColor("#8d1007"));
            }
            if(focus == 2){
                series = seriesArray[1];
                series.setDrawBackground(true);
                series.setColor(Color.parseColor("#8d1007"));
                series.setBackgroundColor(Color.parseColor("#8d1007"));
            }
            if(focus == 3){
                series = seriesArray[2];
                series.setDrawBackground(true);
                series.setColor(Color.parseColor("#8d1007"));
                series.setBackgroundColor(Color.parseColor("#8d1007"));
            }
            series.setDataPointsRadius(2);
            series.setThickness(2);

            graph.addSeries(series);

            //graph.addSeries(series);
            graph.getGridLabelRenderer().setGridColor(graphColor);
            graph.getGridLabelRenderer().setHorizontalLabelsColor(graphColor);
            graph.getGridLabelRenderer().setVerticalLabelsColor(graphColor);
            graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);


            //Add 5 percent for easier readability
            if(maxy) {
                graph.getViewport().setYAxisBoundsManual(true);
                maxYBound= maxYBound + (maxYBound *.05);
                if(maxYBound ==0){
                    maxYBound=1;
                }

                graph.getViewport().setMaxY(maxYBound);
            }
            //Minus 5 percent
            if(miny) {
                graph.getViewport().setYAxisBoundsManual(true);
                minYBound = minYBound - (minYBound * .05);

                Log.d("BTWeather-minYval", String.valueOf(minYBound));
                graph.getViewport().setMinY(minYBound);
            }
            if(labelCount > 0){
                graph.getGridLabelRenderer().setNumHorizontalLabels(labelCount);
            }
            graph.getGridLabelRenderer().setHumanRounding(true);
            graph.getGridLabelRenderer().reloadStyles();
            java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(context);
            if(time==1) {
                graph.getGridLabelRenderer().setLabelFormatter(
                        new DateAsXAxisLabelFormatter(graph.getContext(),
                                dateTimeFormatter));
            }else{
                graph.getGridLabelRenderer().setLabelFormatter(
                        new DateAsXAxisLabelFormatter(graph.getContext()));
            }
        }catch(Exception e){
            Log.d("BTWeather-error21", e.toString());

        }

    }

    public LineGraphSeries[] seriesBuilder(List<Sensors> sensorsList){
        LineGraphSeries[] seriesArray = new LineGraphSeries[3];
        try{
            DataPoint d = null;
            DataPoint[] dataPoints = new DataPoint[sensorsList.size()];
            DataPoint[] dataPointsH = new DataPoint[sensorsList.size()];
            DataPoint[] dataPointsW = new DataPoint[sensorsList.size()];
            Date date1 = new Date();
            int i = 0;
            Log.d("BTWeather-seriesbuilder",
                    " Length of sensorlist: " + String.valueOf(sensorsList.size()));
            try{
                for(Sensors sensor: sensorsList){
                    findMaxY(sensor);
                    findMinY(sensor);
                    try {

                        date1 = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse(sensor.getmDate());

                        //Log.d("BTWeather-sensorlistFG",
                        //  String.valueOf(date1)+" - " + String.valueOf(sensor.getmTemp()) );
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if( isCelsius()){
                        d = new DataPoint(date1, Double.valueOf(sensor.getmTemp()));
                    }else{
                        double tmp = mainActivity.cToF(Double.valueOf(sensor.getmTemp()));

                        // Log.d("BTWeather-seriesdump",String.valueOf(tmp));
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

            }catch(Exception e){
                Log.d("BTWeather-error20", e.toString());
            }
        }catch (Exception e){
            Log.d("BTWeather-error22", e.toString());
        }


        return seriesArray;
    }
    public void findMaxY (Sensors sensor){
        try{
            //Focus passed from main activity on graph click
            if(focus ==1){
                if(  isCelsius()) {
                    if (Double.valueOf(sensor.getmTemp()) > maxYBound) {
                        maxYBound = Double.valueOf(sensor.getmTemp());
                    }
                }else if(mainActivity.cToF(Double.valueOf(sensor.getmTemp()))>maxYBound){
                    maxYBound=mainActivity.cToF(Double.valueOf(sensor.getmTemp()));
                }
            }

            else if(focus == 2){
                if( Double.valueOf(sensor.getmHumidity())> maxYBound){
                    maxYBound = Double.valueOf(sensor.getmHumidity());
                }
            }

            else if(focus == 3){
                if(Double.valueOf(sensor.getmWind())> maxYBound){
                    maxYBound = Double.valueOf(sensor.getmWind());
                }
            }}
        catch (Exception e){
            Log.d("BTWeather-error19", e.toString());
        }
    }
    public void findMinY (Sensors sensor){
        try{
            //Focus passed from main activity on graph click
            if(focus ==1){
                if(  isCelsius()) {
                    if (Double.valueOf(sensor.getmTemp()) < minYBound) {
                        minYBound = Double.valueOf(sensor.getmTemp());
                    }
                }else if(mainActivity.cToF(Double.valueOf(sensor.getmTemp()))< minYBound){
                    minYBound=mainActivity.cToF(Double.valueOf(sensor.getmTemp()));
                }
            }

            else if(focus == 2){
                if( Double.valueOf(sensor.getmHumidity())< minYBound){
                    minYBound = Double.valueOf(sensor.getmHumidity());
                }
            }

            else if(focus == 3){
                if(Double.valueOf(sensor.getmWind())< minYBound){
                    minYBound = Double.valueOf(sensor.getmWind());
                }
            }}
        catch(Exception e){
            Log.d("BTWeather-error18", e.toString());
        }
    }

    //Database
    public static String getYesterday(){
        //return new Date(System.currentTimeMillis()-24*60*60*1000);
        long  day = TimeUnit.DAYS.toMillis(1);
        String start= DateFormat.format("MM-dd-yyyy HH:mm:ss",
                new Date(System.currentTimeMillis() - day)).toString();
        return start;
    }
    public static String getWeek(){
        //return new Date(System.currentTimeMillis()-24*60*60*1000);
        long  week = TimeUnit.DAYS.toMillis(7);
        String start= DateFormat.format("MM-dd-yyyy HH:mm:ss",
                new Date(System.currentTimeMillis() - week)).toString();
        return start;
    }
    public static String getMonth(){
        //return new Date(System.currentTimeMillis()-24*60*60*1000);
        long  month = TimeUnit.DAYS.toMillis(30);
        String start= DateFormat.format("MM-dd-yyyy HH:mm:ss",
                new Date(System.currentTimeMillis() - month)).toString();
        return start;
    }
    public static Date getMeTomorrow(){
        return new Date(System.currentTimeMillis());
    }


    public List<Sensors> getTempData(String start){
        SensorsDatabase sDb = SensorsDatabase.getSensorsDatabase(context);
        List<Sensors> dataPoints = null;
        Date date1 = new Date();
        try {
            dataPoints = sDb.sensorsDao().findTempByDate(
                    start,
                    DateFormat.format("MM-dd-yyyy HH:mm:ss", getMeTomorrow()).toString());

        } catch (Exception e) {
            Log.d("BTWeather-error8", String.valueOf(e));
        }
        return dataPoints;
        }

    public boolean isCelsius() {
        return celsius;
    }
}
