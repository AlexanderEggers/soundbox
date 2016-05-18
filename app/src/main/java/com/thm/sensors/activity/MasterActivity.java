package com.thm.sensors.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.logic.AudioLogic;
import com.thm.sensors.logic.BluetoothLogic;

import org.puredata.android.io.PdAudio;

import java.io.IOException;
import java.text.MessageFormat;

public final class MasterActivity extends Activity {

    private static final long MAX_INACTIVE_TIME = 3000;
    private BluetoothLogic mBluetoothLogic;
    private Handler mHandler;
    private AudioLogic mAudioLogic;
    private boolean mStopRunning = false;
    private int totalDevices;

    //Niki added code
    private final int savedValueAmount = 7;
    private int valueCounter = 0;
    private float[][] lastSensorValues = new float[savedValueAmount][3];
    //end code niki

    @Override
    protected void onResume() {
        super.onResume();
        PdAudio.startAudio(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        //niki added code (fill array with empty vectors)
        for (int i = 0; i < lastSensorValues.length; i++) {
            for (int k = 0; i < 3; i++) {
                lastSensorValues[i][k] = 0.0f;
            }
        }
        //end code niki

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                handleData(msg);
            }
        };

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mBluetoothLogic = new BluetoothLogic(mHandler);
                mBluetoothLogic.startConnection(Util.MASTER);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        try {
            mAudioLogic = new AudioLogic(this);
            mAudioLogic.loadPDPatch();
            mAudioLogic.initPD();
            mAudioLogic.startAudio();
        } catch (IOException e) {
            e.getStackTrace();
            finish();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (!mStopRunning) {
                    for (String beacon : Util.beaconLastData.keySet()) {
                        if (Util.beaconDeviceMap.get(beacon) != null) {
                            long diff = System.currentTimeMillis() - Util.beaconLastData.get(beacon);

                            if (diff > MAX_INACTIVE_TIME) {
                                String device = Util.beaconDeviceMap.get(beacon);
                                Util.beaconDeviceMap.put(beacon, null);
                                mBluetoothLogic.sendDataToSlave(device, "LOGOUT_SLAVE%" + beacon + "%");
                            }
                        }
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleData(Message msg) {
        byte[] aData = (byte[]) msg.obj;
        String data = new String(aData);
        String[] aSplitData = data.split("%");
        String identifier = aSplitData[0];
        String beacon = aSplitData[1];
        String device = aSplitData[2];

        Log.i(MasterActivity.class.getName(), "Identifier: " + identifier);

        switch (identifier) {
            case "Login":
                String color = Util.beaconColorMap.get(beacon);
                System.out.println("KEY EXISTS: " + Util.beaconDeviceMap.containsKey(beacon));
                System.out.println("BEACON: " + beacon);
                if (Util.beaconDeviceMap.get(beacon) == null && Util.beaconDeviceMap.containsKey(beacon)) {
                    Util.beaconDeviceMap.put(beacon, device);
                    Util.beaconLastData.put(beacon, System.currentTimeMillis());
                    mBluetoothLogic.sendDataToSlave(device, "LOGIN_SLAVE%" + beacon + "%" + color + "%");
                    totalDevices++;
                    ((TextView) findViewById(R.id.textView8)).setText(
                            MessageFormat.format("Total Devices: {0}", totalDevices));
                } else {
                    mBluetoothLogic.sendDataToSlave(device, "ERROR%" + beacon + "%");
                }
                break;
            case "Logout":
                Util.beaconDeviceMap.put(beacon, null);
                mBluetoothLogic.sendDataToSlave(device, "LOGOUT_SLAVE%" + beacon + "%");
                totalDevices--;
                ((TextView) findViewById(R.id.textView8)).setText(
                        MessageFormat.format("Total Devices: {0}", totalDevices));
                break;
            case "Data":
                boolean foundBeaconDevice = false;
                System.out.println(Util.beaconDeviceMap.keySet().toString());
                for (String key : Util.beaconDeviceMap.keySet()) {
                    if (Util.beaconDeviceMap.get(key) != null &&
                            Util.beaconDeviceMap.get(key).equals(device) && key.equals(beacon)) {
                        foundBeaconDevice = true;
                        break;
                    }
                }

                if (foundBeaconDevice) {
                    Util.beaconLastData.put(beacon, System.currentTimeMillis());
                    int audioMode = Util.beaconModeMap.get(beacon);

                    String[] values = aSplitData[3].split(";");

                    /* Auskommentiert, wegen Array-einspeisung
                    float valueX = Float.parseFloat(values[0]);
                    float valueY = Float.parseFloat(values[1]);
                    float valueZ = Float.parseFloat(values[2]);
                    */

                    //niki: werte ins array einspeisen
                    lastSensorValues[valueCounter][0] = Float.parseFloat(values[0]);
                    lastSensorValues[valueCounter][1] = Float.parseFloat(values[1]);
                    lastSensorValues[valueCounter][2] = Float.parseFloat(values[2]);
                    //iterate through the array
                    valueCounter = (valueCounter + 1) % lastSensorValues.length;
                    // ende niki

                    float valueX = processArrayValues(0);

                    float valueY = processArrayValues(1);
                    float valueZ = processArrayValues(2);


                            ((TextView) findViewById(R.id.textView3)).setText(MessageFormat.format("X: {0}", valueX));
                    ((TextView) findViewById(R.id.textView4)).setText(MessageFormat.format("Y: {0}", valueY));
                    ((TextView) findViewById(R.id.textView5)).setText(MessageFormat.format("Z: {0}", valueZ));

                    mAudioLogic.processAudioAcceleration(audioMode, valueX, valueY, valueZ);
                    ((TextView) findViewById(R.id.textView6)).setText(MessageFormat.format("Beacon: {0}", beacon));
                } else {
                    Log.d(MasterActivity.class.getName(),
                            MessageFormat.format("Cannot find a beacon which is connected to this device = {0}", device));
                    mBluetoothLogic.sendDataToSlave(device, "ERROR%" + beacon + "%");
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PdAudio.stopAudio();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothLogic.close();
        mStopRunning = true;
    }

    //funktion, die für einen parameter den average aus dem array holt
    private float processArrayValues(int k) {
        float sum = 0;
        float avg = 0;

        for (int i = 0; i < savedValueAmount; i++) {
            sum += lastSensorValues[i][k];
        }

        avg = sum / savedValueAmount;

        return avg;
    }
    //ende niki
}
