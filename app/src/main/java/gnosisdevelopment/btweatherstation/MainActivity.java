package gnosisdevelopment.btweatherstation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private Intent weatherPanelIntent;
    private TextView mTextMessage;
    private TextView tempText;
    private TextView humidityText;
    private TextView windText;

    private ViewGroup weatherView;
    private View tempLayout;
    private View windLayout;
    private View humidityLayout;
    private View controlLayout;

    private boolean tempState = true;
    private boolean humidityState = true;
    private boolean windState = true;
    private boolean celsius = true;


    final String DEGREE  = "\u00b0";
    private Double temp = 27.00;
    private Double humidity = 50.10;
    private Double wind = 15.5;

    private LayoutInflater inflater;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    weatherPanelInflator();
                    controlPanelDeflator();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    weatherPanelDeflator();
                    controlPanelInflator();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
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
        setContentView(R.layout.activity_main);
        //Child layout
        inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        weatherPanelInflator();
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        weatherPanelIntent = new Intent(this, WeatherPanel.class);
    }

    protected  void setTemp (){
        tempText.setText(String.valueOf(temp) +DEGREE);
    }
    protected  void setHumidity (){
        humidityText.setText(String.valueOf(humidity) + "%" );
    }
    protected  void setWind (){
        windText.setText(String.valueOf(wind) );
    }
    // convert C to F
    protected Double cToF(Double cTemp){
        return  (temp * 9/5.0) +32;
    }
    protected void weatherPanelInflator(){
        weatherPanelDeflator();
        if ( tempState == true){
            tempLayout = inflater.inflate(R.layout.weather_temp,
                    (ViewGroup) findViewById(R.id.weatherPanelTemp));

            weatherView = findViewById(R.id.container);
            weatherView.addView(tempLayout);

            tempText = (TextView) findViewById(R.id.temp);
        }
        if(humidityState == true){
            humidityLayout = inflater.inflate(R.layout.weather_humidity,
                    (ViewGroup) findViewById(R.id.weatherPanelHumidity));

            weatherView = findViewById(R.id.container);
            weatherView.addView(humidityLayout);

            humidityText = (TextView) findViewById(R.id.humidity);
        }
        if(windState == true){
            windLayout = inflater.inflate(R.layout.weather_wind,
                    (ViewGroup) findViewById(R.id.weatherPanelWind));

            weatherView = findViewById(R.id.container);
            weatherView.addView(windLayout);

            windText = (TextView) findViewById(R.id.wind);
        }
        if(temp != null && tempText != null){
            setTemp();
        }
        if (humidity != null && humidityText != null ){
            setHumidity();
        }
        if (wind != null && windText != null){
            setWind();
        }
    }
    protected void weatherPanelDeflator() {
        if(tempLayout != null)weatherView.removeView(tempLayout);
        if(humidityLayout != null)weatherView.removeView(humidityLayout);
        if( windLayout != null) weatherView.removeView(windLayout);
    }
    protected void controlPanelInflator(){
        controlPanelDeflator();
        controlLayout = inflater.inflate(R.layout.control_panel,
                (ViewGroup) findViewById(R.id.controlPanel));
        weatherView = findViewById(R.id.container);
        weatherView.addView(controlLayout);
    }
    protected void controlPanelDeflator() {
        if(controlLayout != null) weatherView.removeView(controlLayout);
    }
}
