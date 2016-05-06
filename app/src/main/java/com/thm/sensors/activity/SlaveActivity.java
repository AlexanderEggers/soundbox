package com.thm.sensors.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewStub;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.logic.AccelerationLogic;
import com.thm.sensors.logic.BeaconLogic;
import com.thm.sensors.logic.BluetoothLogic;
import com.thm.sensors.logic.HeartbeatLogic;
import com.thm.sensors.logic.SlaveLogic;

public final class SlaveActivity extends Activity {

    private SlaveLogic mLogic;
    private BluetoothLogic mBluetoothLogic;

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

        int sensor = getIntent().getIntExtra("sensor", -1);
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        switch (sensor) {
            case Util.PROXIMITY:
                stub.setLayoutResource(R.layout.slave_proximity_content);
                stub.inflate();
                mLogic = new BeaconLogic();
                mLogic.startLogic(this);
                break;
            case Util.HEARTBEAT:
                stub.setLayoutResource(R.layout.slave_heartbeat_content);
                stub.inflate();
                mLogic = new HeartbeatLogic();
                mLogic.startLogic(this);
                break;
            case Util.ACCELERATION:
                stub.setLayoutResource(R.layout.slave_acceleration_content);
                stub.inflate();
                mLogic = new AccelerationLogic();
                mLogic.startLogic(this);
                break;
            default:
                finish();
                return;
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mBluetoothLogic = new BluetoothLogic(null);
                mBluetoothLogic.startConnection(Util.SLAVE);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void sendSensorData(int identifier, int beaconID, float value) {
        if (mBluetoothLogic != null && mBluetoothLogic.isMasterConnectionAvailable()) {
            mBluetoothLogic.prepareData(identifier, beaconID, value);
        } else {
            if (mBluetoothLogic == null) {
                Log.w(SlaveActivity.class.getName(), "Senor data could not be sent. Logic = " + mBluetoothLogic);
            } else {
                Log.w(SlaveActivity.class.getName(), "Senor data could not be sent. " +
                        "Master = " + mBluetoothLogic.isMasterConnectionAvailable());
            }
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
        mBluetoothLogic.close();
    }
}
