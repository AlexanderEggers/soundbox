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

public final class SlaveActivity extends Activity {

    private SlaveLogic logic;
    private BluetoothLogic.ConnectedThread thread;

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

    public void writeData(int data) {
        byte[] bytes = ByteBuffer.allocate(1024).putInt(data).array();
        thread.write(bytes);
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
