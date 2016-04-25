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

    private SlaveLogic logic;
    private BluetoothLogic.ConnectedThread thread;
    private ArrayList<Byte> dataArray = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();

        if (logic != null) {
            logic.onResume();
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
                logic = new ProximityLogic();
                logic.startLogic(this);
                break;
            case "Heartbeat":
                stub.setLayoutResource(R.layout.slave_heartbeat_content);
                stub.inflate();
                logic = new HeartbeatLogic();
                logic.startLogic(this);
                break;
            case "Acceleration":
                stub.setLayoutResource(R.layout.slave_acceleration_content);
                stub.inflate();
                logic = new AccelerationLogic();
                logic.startLogic(this);
                break;
        }

        BluetoothLogic bluetooth = new BluetoothLogic(this, new Handler());
        thread = bluetooth.getMaster();
    }

    public void writeData(float data) {
        if(thread != null) {
            byte[] bytes = ByteBuffer.allocate(4).putFloat(data).array();

            for (byte b : bytes) {
                dataArray.add(b);
            }
        }
    }

    public void writeData(String identifier) {
        if(thread != null) {
            byte[] bytes = identifier.getBytes(StandardCharsets.UTF_8);

            for (byte b : bytes) {
                dataArray.add(b);
            }
        }
    }

    public void sendData() {
        byte[] streamD = new byte[dataArray.size()];

        for (int i = 0; i < dataArray.size(); i++) {
            streamD[i] = dataArray.get(i);
        }

        thread.write(streamD);
        dataArray.clear();
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

        if (logic != null) {
            logic.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (thread != null) {
            thread.cancel();
        }
    }
}
