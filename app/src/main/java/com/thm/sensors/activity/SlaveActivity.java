package com.thm.sensors.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.ViewStub;

import com.thm.sensors.R;
import com.thm.sensors.logic.AccelerationLogic;
import com.thm.sensors.logic.BluetoothLogic;
import com.thm.sensors.logic.HeartbeatLogic;
import com.thm.sensors.logic.ProximityLogic;
import com.thm.sensors.logic.SlaveLogic;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public final class SlaveActivity extends Activity {

    private SlaveLogic mLogic;
    private BluetoothLogic.ConnectedThread mThread;
    private ArrayList<Byte> mDataArray = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();

        if (mLogic != null) {
            mLogic.onResume();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slave_activity);

        String sensor = getIntent().getStringExtra("sensor");
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        switch (sensor) {
            case "Proximity":
                stub.setLayoutResource(R.layout.slave_proximity_content);
                stub.inflate();
                mLogic = new ProximityLogic();
                mLogic.startLogic(this);
                break;
            case "Heartbeat":
                stub.setLayoutResource(R.layout.slave_heartbeat_content);
                stub.inflate();
                mLogic = new HeartbeatLogic();
                mLogic.startLogic(this);
                break;
            case "Acceleration":
                stub.setLayoutResource(R.layout.slave_acceleration_content);
                stub.inflate();
                mLogic = new AccelerationLogic();
                mLogic.startLogic(this);
                break;
        }

        BluetoothLogic bluetooth = new BluetoothLogic(this, new Handler());
        mThread = bluetooth.getMaster();
    }

    public void sendSensorData(String identifier, int beaconID, float value) {
        writeData(identifier);
        writeData(beaconID);
        writeData(value);
        sendData();
    }

    private void writeData(float value) {
        if (mThread != null) {
            byte[] bytes = ByteBuffer.allocate(4).putFloat(value).array();

            for (byte b : bytes) {
                mDataArray.add(b);
            }
        }
    }

    private void writeData(int beaconID) {
        if (mThread != null) {
            byte[] bytes = ByteBuffer.allocate(4).putInt(beaconID).array();

            for (byte b : bytes) {
                mDataArray.add(b);
            }
        }
    }

    private void writeData(String identifier) {
        if (mThread != null) {
            byte[] bytes = identifier.getBytes(StandardCharsets.UTF_8);

            for (byte b : bytes) {
                mDataArray.add(b);
            }
        }
    }

    private void sendData() {
        if (mThread != null) {
            byte[] streamD = new byte[mDataArray.size()];

            for (int i = 0; i < mDataArray.size(); i++) {
                streamD[i] = mDataArray.get(i);
            }

            mThread.write(streamD);
            mDataArray.clear();
        }
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

    @Override
    protected void onPause() {
        super.onPause();

        if (mLogic != null) {
            mLogic.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mThread != null) {
            mThread.cancel();
        }
    }
}
