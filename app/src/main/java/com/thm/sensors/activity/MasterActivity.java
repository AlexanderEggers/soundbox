package com.thm.sensors.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.logic.AudioLogic;
import com.thm.sensors.logic.BluetoothLogic;

import org.puredata.android.io.PdAudio;

import java.io.IOException;
import java.nio.ByteBuffer;
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

        ((TextView) findViewById(R.id.textView3)).setText("Proximity: n/a");
        ((TextView) findViewById(R.id.textView4)).setText("Heartbeat: n/a");
        ((TextView) findViewById(R.id.textView5)).setText("Acceleration: n/a");
        ((TextView) findViewById(R.id.textView6)).setText("Beacon-ID: n/a");

        mAudioLogic = new AudioLogic(this);

        try {
            mAudioLogic.loadPDPatch();
            mAudioLogic.initPD();
        } catch (IOException e) {
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
        byte[] aIdentifier = new byte[4];
        System.arraycopy(((byte[]) msg.obj), 0, aIdentifier, 0, 4);

        byte[] aID = new byte[8];
        System.arraycopy(((byte[]) msg.obj), 4, aID, 0, 8);

        byte[] aValue = new byte[4];
        System.arraycopy(((byte[]) msg.obj), 12, aValue, 0, 4);

        ByteBuffer wrapped = ByteBuffer.wrap(aIdentifier);
        int identifier = wrapped.getInt();

        wrapped = ByteBuffer.wrap(aID);
        long id = wrapped.getInt();

        wrapped = ByteBuffer.wrap(aValue);
        float value = wrapped.getFloat();

        switch (identifier) {
            case Util.PROXIMITY:
                ((TextView) findViewById(R.id.textView3)).setText(MessageFormat.format("Proximity: {0}", value));
                mAudioLogic.processAudioProximity(value);
                break;
            case Util.HEARTBEAT:
                ((TextView) findViewById(R.id.textView4)).setText(MessageFormat.format("Heartbeat: {0}", value));
                break;
            case Util.ACCELERATION_X:
            case Util.ACCELERATION_Y:
            case Util.ACCELERATION_Z:
                mAudioLogic.processAudioAcceleration(identifier, value, id);
                break;
        }

        ((TextView) findViewById(R.id.textView8)).setText(MessageFormat.format("ID: {0}", id));
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
