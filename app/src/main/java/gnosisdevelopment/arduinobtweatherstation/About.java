package gnosisdevelopment.arduinobtweatherstation;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        WebView aboutWebView = (WebView) findViewById(R.id.aboutWebView);
        aboutWebView.setWebViewClient(new About.MyBrowser());

        String summary = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                ".main{\n" +
                "margin:20px;\n" +
                "\n" +
                "text-align:center;\n" +
                "color:dark-gray\n" +
                "}\n" +
                ".gnos a{\n" +
                "color:purple;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body\n" +
                "\n" +
                "\t<div class=\"main\">\n" +
                "\t\t<h1>ArduinoBT Weather Station</h1>\n" +
                "\t\t<p> Version 1.0-Alpha</p>\n" +
                "\t\t<p> Pre release for testing. Current versions graph is very limited. Sensor readings are stored in database once per minute after the device connects to bluetooth and receives properly formated input. </p>\n" +
                "\t\t<p> Next Release:<br />\n" +
                "\t\tInterval control for sensor storing. <br />\n" +
                "\t\tWill be adding a full page graph (day/week/month grapgh view), support for smaller screens (for now horizontal landscape is best on a small screen). <br />\n" +
                "\t\tExport for database of sensor readings. <br /><br />\n" +
                "\t\tDatabase can be reached at from device at: <a href=\" http://localhost:8080\">access db http://localhost:8080)</a><br /><br /> or by http://android:ip:8080 on a locally connected computer</p>\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\t\n" +
                "\t\t<p>\n" +
                "\t\t\tThis app requires arduino with a bluetooth adapter and a temperature, humidity, and wind (anemometer) sensor. Sketch:  <a href=\"https://github.com/JamesHarley/BTWeatherStation/blob/master/arduino/bt-weather-arduino/bt-weather-arduino.ino\">bt-weather-arduino.ino</a>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t</p>\n" +
                "\t\t\n" +
                "\t\t<p>OR </p>\n" +
                "\t\t<p>a compatiable setup that sends formatted output through the bluetooth adapater serial in the format <i>@10.00;20.00;30.00@</i> </p>\n" +
                "\t\t\n" +
                "\t\t<p>\n" +
                "\t\t\t\tParts used: \n" +
                "\t\t\t\t<ul style=\"text-align:left\">\n" +
                "\t\t\t\t<li>Arduino (any variant)</li>\n" +
                "\t\t\t\t<li>HC-06 Bluetooth serial adapter</li>\n" +
                "\t\t\t\t<li>DHT11 - humidity and temperature sensor</li>\n" +
                "\t\t\t\t<li>Anemometer -- I included the code to allow for a wind sensor  that I'll be adding later</li>\n" +
                "\t\t\t</p>\n" +
                "\t\t\t\n" +
                "\t\t<p>Contact us with any comments and suggestions. Please submit and bugs, errors, or crashes to gnosisdevelop@gmail.com</p>\n" +
                "\t\t<div class=\"gnos\"><a href=\"https://github.com/JamesHarley/BTWeatherStation/\">Github repo</a> - <a href=\"http://gnosisdevelopment.com\"> Developed by James Harley</a> </div> \n" +
                "\t\n" +
                "\t</div>\n" +
                "</body\n" +
                "</html>";

        aboutWebView.getSettings().setLoadsImagesAutomatically(true);
        aboutWebView.getSettings().setJavaScriptEnabled(true);
        aboutWebView.getSettings().setBuiltInZoomControls(true);
        aboutWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        aboutWebView.loadData(summary, "text/html", null);

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        if(findViewById(R.id.aboutWebView) != null){
            if(findViewById(R.id.aboutWebView).getVisibility() == View.VISIBLE){

                startActivity(intent);

                return;
            }else{
                super.onBackPressed();
                return;
            }

        }
        else{

            super.onBackPressed();
        }

    }

}
