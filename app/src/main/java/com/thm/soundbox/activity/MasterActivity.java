package com.thm.soundbox.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.thm.soundbox.R;
import com.thm.soundbox.Util;
import com.thm.soundbox.logic.AudioLogic;
import com.thm.soundbox.logic.BluetoothLogic;

import org.puredata.android.io.PdAudio;

import java.io.IOException;
import java.text.MessageFormat;

public final class MasterActivity extends AppCompatActivity {

    private static final long MAX_INACTIVE_TIME = 2000;
    private BluetoothLogic mBluetoothLogic;
    private Handler mHandler;
    private AudioLogic mAudioLogic;
    private boolean mStopRunning;
    private int totalDevices;

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
        setSupportActionBar(toolbar);

        if (Util.INTERPOLATION) {
            for (int i = 0; i < Util.lastSensorValues.length; i++) {
                for (int k = 0; i < 3; i++) {
                    Util.lastSensorValues[i][k] = 0.0f;
                }
            }
        }

        for (String key : Util.beaconDeviceMap.keySet()) {
            Util.beaconDeviceMap.put(key, null);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                                Util.beaconLastData.put(beacon, 0L);
                                totalDevices--;
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
        Object content = ((Object[]) msg.obj)[0];
        String device = ((Object[]) msg.obj)[1] + "";
        byte[] aData = (byte[]) content;
        String data = new String(aData);
        String[] aSplitData = data.split("%");
        String identifier = aSplitData[0];
        String beacon = aSplitData[1];

        Log.i(MasterActivity.class.getName(), "Identifier: " + identifier);

        ((TextView) findViewById(R.id.textView8)).setText(
                MessageFormat.format("Total Devices: {0}", totalDevices));

        switch (identifier) {
            case "Login":
                if (Util.beaconDeviceMap.get(beacon) == null && Util.beaconDeviceMap.containsKey(beacon)) {
                    Util.beaconDeviceMap.put(beacon, device);
                    Util.beaconLastData.put(beacon, System.currentTimeMillis());
                    String color = Util.beaconColorMap.get(beacon);
                    Boolean gravity = Util.beaconGravity.get(beacon);
                    mBluetoothLogic.sendDataToSlave(device, "LOGIN_SLAVE%" + beacon + "%" + color + "%" + gravity + "%");
                    totalDevices++;
                    ((TextView) findViewById(R.id.textView8)).setText(
                            MessageFormat.format("Total Devices: {0}", totalDevices));
                } else {
                    Log.d(MasterActivity.class.getName(), "Slave Error Logout: Missing beacon or beacon already in use");
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
                    String[] values = aSplitData[2].split(";");
                    float valueX, valueY, valueZ;

                    if (Util.INTERPOLATION) {
                        Util.lastSensorValues[Util.valueCounter][0] = Float.parseFloat(values[0]);
                        Util.lastSensorValues[Util.valueCounter][1] = Float.parseFloat(values[1]);
                        Util.lastSensorValues[Util.valueCounter][2] = Float.parseFloat(values[2]);
                        Util.valueCounter = (Util.valueCounter + 1) % Util.lastSensorValues.length;

                        valueX = Util.processArrayValues(0);
                        valueY = Util.processArrayValues(1);
                        valueZ = Util.processArrayValues(2);
                    } else {
                        valueX = Float.parseFloat(values[0]);
                        valueY = Float.parseFloat(values[1]);
                        valueZ = Float.parseFloat(values[2]);
                    }

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
}
