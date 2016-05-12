package com.thm.sensors.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.logic.AccelerationLogic;
import com.thm.sensors.logic.BeaconLogic;
import com.thm.sensors.logic.BluetoothLogic;
import com.thm.sensors.logic.SlaveLogic;

public final class SlaveActivity extends Activity {

    private SlaveLogic mAcceleration, mBeaconLogic;
    private BluetoothLogic mBluetoothLogic;
    private Handler mHandler;

    @Override
    protected void onResume() {
        super.onResume();

        if (mAcceleration != null) {
            mAcceleration.onResume();
        }

        if(mBeaconLogic != null) {
            mBeaconLogic.onResume();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slave_activity);

        mAcceleration = new AccelerationLogic();
        mAcceleration.startLogic(this);

        mBeaconLogic = new BeaconLogic();
        mBeaconLogic.startLogic(this);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                handleData(msg);
            }
        };

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mBluetoothLogic = new BluetoothLogic(mHandler);
                mBluetoothLogic.startConnection(Util.SLAVE);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void handleData(Message msg) {
        byte[] aData = (byte[]) msg.obj;
        String data = new String(aData);

        if(data.equals("ERROR")) {
            Util.isLogin = false;
            Util.connectedBeacon = null;
        } else {
            int color = Integer.parseInt(data);
            findViewById(R.id.slave_parent_layout).setBackgroundColor(color);
            Util.isLogin = true;
        }
    }

    public void sendSensorData(String value) {
        if (mBluetoothLogic != null && mBluetoothLogic.isConnectionAvailable() && Util.isLogin) {
            mBluetoothLogic.sendDataToMaster(value);
        } else {
            if (mBluetoothLogic == null) {
                Log.w(SlaveActivity.class.getName(), "Senor data could not be sent. Logic = " + mBluetoothLogic);
            } else {
                Log.w(SlaveActivity.class.getName(), "Senor data could not be sent. " +
                        "Master = " + mBluetoothLogic.isConnectionAvailable());
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

        if(mAcceleration != null) {
            mAcceleration.onPause();
        }

        if (mBeaconLogic != null) {
            mBeaconLogic.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothLogic.close();
    }
}
