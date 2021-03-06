/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gnosisdevelopment.arduinobtweatherstation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;


/**
 * This fragment controls Bluetooth to communicate with other devices.
 */
public class BluetoothChatFragment extends Fragment {
    private static final String TAG = "BluetoothChatFragment";
    Timer timer = new Timer();
    // Intent request codes
    private final Handler reconnectHandler = new Handler();
    private boolean reconnectTimerState = false;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private int reconnectAttempts = 0;
    Button connectBT;
    //Used for tokenizer
    private String buildOutput ="";

    private int timeInMilliseconds = 1000;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;


    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }



    }


    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
            if((MainActivity) getActivity() != null) {
                String bt = ((MainActivity) getActivity()).getBT();
                if (!(bt.equals("")) && !(bt.equals("empty"))) {
                    connectDevice(bt, false);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
            if((MainActivity) getActivity() != null) {
                ((MainActivity) getActivity()).setBtConnectedState(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bluetooth_chat, container, false);
    }


    /**
     * Set up the UI and background operations for chat.
     */
    public void setupChat() {
        Log.d(TAG, "setupChat()");
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(getActivity(), mHandler);
    }
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new  Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {

                case Constants.MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //send it to the tokenizer to be processed
                    if((MainActivity) getActivity() != null) {
                        try {
                            ((MainActivity) getActivity()).setBtConnectedState(true);
                            //reset counter after connection
                            reconnectAttempts = 0;
                            reconnectTimerState = false;
                            //end the timer if running

                        } catch (Exception e) {
                            Log.d(Constants.LOG_TAGBTCF + "error-3", String.valueOf(e));
                        }
                    }
                    //Log.d(Constants.LOG_TAG,"callTokenizer");
                    tokenizer(readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if((MainActivity) getActivity() != null) {
                        try {
                            ((MainActivity) getActivity()).setBtConnectedState(false);
                            if (!reconnectTimerState)
                                setRepeatingAsyncTask();
                        } catch (Exception e) {
                            Log.d(Constants.LOG_TAGBTCF + "error-4", String.valueOf(e));
                        }
                    }
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if((MainActivity) getActivity() != null) {
            try {
                ((MainActivity) getActivity()).btDeviceSave(device.getAddress());
            } catch (Exception e) {
                Log.d(Constants.LOG_TAGBTCF+"error-6", String.valueOf(e));
            }
            // Attempt to connect to the device
        }
        mChatService.connect(device, secure);
    }
    public void connectDevice(String mac, boolean secure){
        BluetoothDevice device =  mBluetoothAdapter.getRemoteDevice(mac);
        mChatService.connect(device, secure);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.bluetooth_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.btButton: {
                // Launch the DeviceListActivity to see devices and do scan
                if((MainActivity) getActivity() != null) {
                    ((MainActivity) getActivity()).removeBtDevice();
                }
                return true;
            }
            case R.id.aboutButton:{
                Intent serverIntent = new Intent(getActivity(), About.class);
                startActivity(serverIntent);
                return true;
            }
            case R.id.home:{
                getActivity().onBackPressed();
                return true;
            }
        }
        return false;
    }

    public void tokenizer(String input) {
        String output ="";

        char en = '@';


        for(int i=0;i< input.length();i++){
            char t = input.charAt(i);
            //
            //if(t.equals(start)) {
            if(t == en) {
                //Log.d(Constants.LOG_TAGBTCF,"Found @ start");
                while(i<input.length()) {

                    t = input.charAt(i);
                    //Log.d(Constants.LOG_TAGBTCF,"token: " + t);
                    //if(!(t.equals(end))) {
                    if(t != en){

                        buildOutput = buildOutput + t;
                        //Log.d(Constants.LOG_TAGBTCF, "buildoutput: " + buildOutput);
                    }
                    //if(t.equals(end)){
                    i++;
                    if(t == en){
                        //Log.d(Constants.LOG_TAGBTCF,"Found # end");

                        int l = buildOutput.length();
                        //Log.d(Constants.LOG_TAGBTCF,"length: " +String.valueOf(l));

                        //Ignore incomplete buffer by checking length
                        if(buildOutput.length() > 13){
                            output=buildOutput;

                            String [] a =output.split(";");
                            if((MainActivity) getActivity() != null) {
                                try {

                                    ((MainActivity) getActivity()).gaugeUpdater(a);
                                } catch (Exception e) {
                                    Log.d(Constants.LOG_TAGBTCF + "error-4", String.valueOf(e));
                                }
                            }
                            //create object and send to mainactivity
                        }

                        buildOutput="";

                    }

                }
            }

        }


    }


    private void setRepeatingAsyncTask() {
        try{
            reconnectTimerState = true;
            //TODO Timertask won't restart a second time after disconnect
             TimerTask task = new TimerTask() {

                @Override
                public void run() {

                    reconnectHandler.post(new Runnable() {
                        public void run() {
                            Log.d(Constants.LOG_TAGBTCF,"reconnect timer run ");
                            try{
                                if((MainActivity)getActivity() != null) {
                                    if (((MainActivity) getActivity()).getBtConnectedState()) {
                                        timer.cancel();
                                        Log.d(Constants.LOG_TAGBTCF, "reconnect timer stopped ");
                                    } else {
                                        try {
                                            String bt = ((MainActivity) getActivity()).getBT();
                                            if (!(bt.equals("")) && !(bt.equals("empty"))) {
                                                if (reconnectAttempts < 10) {
                                                    Log.d(Constants.LOG_TAG, "reconnect attempt: " + String.valueOf(reconnectAttempts));
                                                    connectDevice(bt, false);
                                                    reconnectAttempts++;
                                                } else {
                                                    //kill attempt
                                                    timer.cancel();

                                                }
                                            }
                                        } catch (Exception e) {
                                            Log.d(Constants.LOG_TAGBTCF, "error2 " + String.valueOf(e));
                                        }
                                    }
                                }
                            }catch(Exception e){
                                Log.d(Constants.LOG_TAGBTCF+"error-5", String.valueOf(e));
                            }
                        }
                    });
                };
            };
            if(reconnectTimerState){
                try{
                    timer.schedule(task, 0, 60 * timeInMilliseconds);
                    Log.d("BTWeather-storeAsync", "TimerStart");
                }catch (Exception e) {
                    Log.d("BTWeather-error11", e.toString());
                }
            }else{
                task.cancel();
            }

        }catch (Exception e){
            Log.d(Constants.LOG_TAGBTCF,"error1 " +String.valueOf(e));
        }
    }

}
