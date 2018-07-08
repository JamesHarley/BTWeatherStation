package gnosisdevelopment.btweatherstation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Intent weatherPanelIntent;
    private TextView mTextMessage;
    private TextView tempText;
    private TextView humidityText;
    private Double temp = 27.00;
    private Double humidity = 50.10;
    private ViewGroup weatherView;
    private View childLayout;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);

                    weatherView.addView(childLayout);
                    setTemp();
                    //startActivity(weatherPanelIntent);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    weatherView.removeView(childLayout);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    weatherView.removeView(childLayout);
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
        LayoutInflater inflater = (LayoutInflater)      this.getSystemService(LAYOUT_INFLATER_SERVICE);
        childLayout = inflater.inflate(R.layout.weather,
                (ViewGroup) findViewById(R.id.weatherPanel));

        weatherView = findViewById(R.id.container);
        weatherView.addView(childLayout);

        tempText = (TextView) findViewById(R.id.temp);
        humidityText = (TextView) findViewById(R.id.humidity);


        if(temp != null && tempText != null){
            setTemp();
        }
        if (humidity != null && humidityText != null){
            setHumidity();
        }

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        weatherPanelIntent = new Intent(this, WeatherPanel.class);
    }
    protected  void setTemp (){
        tempText.setText(String.valueOf(temp));
    }
    protected  void setHumidity (){
        humidityText.setText(String.valueOf(humidity));
    }
    // convert C to F
    protected Double cToF(Double cTemp){
        return  (temp * 9/5.0) +32;
    }

}
