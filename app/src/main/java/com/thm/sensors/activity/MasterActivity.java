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

    private BluetoothLogic mBluetoothLogic;
    private Handler mHandler;
    private AudioLogic mAudioLogic;

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

        ((TextView) findViewById(R.id.textView3)).setText("Acceleration X: n/a");
        ((TextView) findViewById(R.id.textView4)).setText("Acceleration Y: n/a");
        ((TextView) findViewById(R.id.textView5)).setText("Acceleration Z: n/a");
        ((TextView) findViewById(R.id.textView6)).setText("Beacon: n/a");

        try {
            mAudioLogic = new AudioLogic(this);
            mAudioLogic.loadPDPatch();
            mAudioLogic.initPD();
            mAudioLogic.startAudio();
        } catch (IOException e) {
            e.getStackTrace();
            finish();
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

    private void handleData(Message msg) {
        byte[] aData = (byte[]) msg.obj;
        String data = new String(aData);
        String[] aSplitData = data.split("%");

        if (data.contains("Login")) {
            String beacon = aSplitData[1];
            String device = aSplitData[2];
            String color = "0xD53B3B"; //only testing, later via Util.beaconColorMap!!

            if (Util.beaconDeviceMap.get(beacon).equals("")) {
                Util.beaconDeviceMap.put(beacon, device);
                mBluetoothLogic.sendDataToSlave(device, color);
            } else {
                mBluetoothLogic.sendDataToSlave(device, "ERROR");
            }
        } else if (data.contains("Logout")) {
            String beacon = aSplitData[1];
            Util.beaconDeviceMap.put(beacon, "");
        } else {
            String device = aSplitData[0];
            String beacon = null;

            for (String key : Util.beaconDeviceMap.keySet()) {
                if (Util.beaconDeviceMap.get(key).equals(device)) {
                    beacon = key;
                    break;
                }
            }

            if (beacon != null) {
                int audioMode = Util.beaconModeMap.get(beacon);

                String[] values = aSplitData[1].split(";");
                float valueX = Float.parseFloat(values[0]);
                float valueY = Float.parseFloat(values[1]);
                float valueZ = Float.parseFloat(values[2]);

                mAudioLogic.processAudioAcceleration(audioMode, valueX, valueY, valueZ);
                ((TextView) findViewById(R.id.textView6)).setText(MessageFormat.format("Beacon: {0}", beacon));
            } else {
                Log.d(MasterActivity.class.getName(),
                        MessageFormat.format("Cannot find a beacon which is connected to this device = {0}", device));
            }
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
    }
}
