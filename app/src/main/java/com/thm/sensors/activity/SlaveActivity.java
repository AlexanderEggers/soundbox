package com.thm.sensors.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.logic.AccelerationLogic;
import com.thm.sensors.logic.BeaconLogic;
import com.thm.sensors.logic.BeaconSlaveLogic;
import com.thm.sensors.logic.BluetoothLogic;

public final class SlaveActivity extends Activity {

    private AccelerationLogic mAcceleration;
    private BeaconLogic mBeaconLogic;
    private BluetoothLogic mBluetoothLogic;
    private Handler mHandler;

    @Override
    protected void onResume() {
        super.onResume();

        if (mAcceleration != null) {
            mAcceleration.onResume();
        }

        if (mBeaconLogic != null) {
            mBeaconLogic.onResume();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slave_activity);

        mAcceleration = new AccelerationLogic();
        mAcceleration.startLogic(this);

        mBeaconLogic = new BeaconSlaveLogic();
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

        if (data.contains("ERROR") || data.contains("LOGOUT_SLAVE")) {
            String[] values = data.split("%");

            if (values[1].equals(Util.connectedBeacon)) {
                Util.isLogin = false;
                Util.connectedBeacon = null;
            } else {
                Log.w(SlaveActivity.class.getName(), "Tried to disconnect an old connection. " +
                        "Beacon = " + values[1]);
            }
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
            } else if(!Util.isLogin) {
                Log.w(SlaveActivity.class.getName(), "This device is not in range of a beacon.");
            } else {
                Log.w(SlaveActivity.class.getName(), "Senor data could not be sent. " +
                        "Master = " + mBluetoothLogic.isConnectionAvailable());
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Back button of this activity; depending on the dev mode if the application
        //is going to be closed or only jumping back to the menu

        if (!Util.DEV_MODE) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mAcceleration != null) {
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
