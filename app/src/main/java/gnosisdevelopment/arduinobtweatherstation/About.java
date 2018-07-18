package gnosisdevelopment.arduinobtweatherstation;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;


public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
                "\t\t<p> Version " + BuildConfig.VERSION_NAME+"."+BuildConfig.VERSION_CODE+" </p>\n" +
                "\t\t<p> Pre release for testing. Current versions graph is very limited. Sensor readings are stored in database once per 30 minutes after the device connects to bluetooth and receives properly formatted input. </p>\n" +
                "\t\t<p> Next Release:<br />\n" +
                "\t\tInterval control for sensor storing. <br />\n" +
                "\t\tWill be adding a full page graph (day/week/month graph view), support for smaller screens (for now horizontal landscape is best on a small screen). <br />\n" +
                "\t\tExport for database of sensor readings. <br /><br />\n" +
                "\t\tDatabase can be reached from device at: <a href=\" http://localhost:8080\">access db http://localhost:8080)</a><br /><br /> or by visiting android device ip at <a href=\"http://"+ getDeviceIpAddress()+":8080\">http://"+ getDeviceIpAddress()+":8080</a> on a locally connected computer</p>\n" +
                "\t\t</p>\n" +
                "\t\t<p>\n" +
                "\t\t\n" +
                "\t\t<p>\n" +
                "\t\t\tThis app requires Arduino with a bluetooth adapter and a temperature, humidity, and wind (anemometer) sensor. Sketch:  <a href=\"https://github.com/JamesHarley/BTWeatherStation/blob/master/arduino/bt-weather-arduino/bt-weather-arduino.ino\">bt-weather-arduino.ino</a>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t</p>\n" +
                "\t\t\n" +
                "\t\t<p>OR </p>\n" +
                "\t\t<p>a compatible setup that sends formatted output through the bluetooth adapter serial in the format <i>@10.00;20.00;30.00@</i> </p>\n" +
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
                "\t\t<p>Contact us with any comments and suggestions. Please submit any bugs, errors, or crashes to gnosisdevelop@gmail.com</p>\n" +
                "\t\t<div class=\"gnos\"><a href=\"https://github.com/JamesHarley/BTWeatherStation/\">Github repo</a> - <a href=\"http://gnosisdevelopment.com\"> Developed by James Harley</a> </div> \n" +
                "\t\n" +
                "\t</div>\n" +
                "</body\n" +
                "</html>";

        aboutWebView.getSettings().setLoadsImagesAutomatically(true);
        aboutWebView.getSettings().setJavaScriptEnabled(true);
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

    @NonNull
    private String getDeviceIpAddress() {
        String actualConnectedToNetwork = null;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWifi.isConnected()) {
                actualConnectedToNetwork = getWifiIp();
            }
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = getNetworkInterfaceIpAddress();
        }
        if (TextUtils.isEmpty(actualConnectedToNetwork)) {
            actualConnectedToNetwork = "127.0.0.1";
        }
        return actualConnectedToNetwork;
    }

    @Nullable
    private String getWifiIp() {
        final WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            int ip = mWifiManager.getConnectionInfo().getIpAddress();
            return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "."
                    + ((ip >> 24) & 0xFF);
        }
        return null;
    }


    @Nullable
    public String getNetworkInterfaceIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        String host = inetAddress.getHostAddress();
                        if (!TextUtils.isEmpty(host)) {
                            return host;
                        }
                    }
                }

            }
        } catch (Exception ex) {
            Log.e("IP Address", "getLocalIpAddress", ex);
        }
        return null;
    }
}
