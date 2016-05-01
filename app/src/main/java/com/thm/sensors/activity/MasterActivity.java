package com.thm.sensors.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.logic.BluetoothLogic;

import java.nio.ByteBuffer;
import java.text.MessageFormat;

public final class MasterActivity extends Activity {

    private BluetoothLogic mBluetoothLogic;
    private Handler mHandler;

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
                Looper.prepare();
                mBluetoothLogic = new BluetoothLogic(mHandler);
                mBluetoothLogic.startConnection(Util.MASTER);
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ((TextView) findViewById(R.id.textView3)).setText("Proximity: n/a");
        ((TextView) findViewById(R.id.textView4)).setText("Heartbeat: n/a");
        ((TextView) findViewById(R.id.textView5)).setText("Acceleration: n/a");
        ((TextView) findViewById(R.id.textView6)).setText("Beacon-ID: n/a");
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

        byte[] aBeaconID = new byte[4];
        System.arraycopy(((byte[]) msg.obj), 4, aBeaconID, 0, 4);

        byte[] aValue = new byte[4];
        System.arraycopy(((byte[]) msg.obj), 8, aValue, 0, 4);

        ByteBuffer wrapped = ByteBuffer.wrap(aIdentifier);
        int identifier = wrapped.getInt();

        wrapped = ByteBuffer.wrap(aBeaconID);
        float beaconID = wrapped.getInt();

        wrapped = ByteBuffer.wrap(aValue);
        float value = wrapped.getFloat();

        //---Audio logic part could start here---

        switch (identifier) {
            case Util.PROXIMITY:
                ((TextView) findViewById(R.id.textView3)).setText(MessageFormat.format("Proximity: {0}", value));
                break;
            case Util.HEARTBEAT:
                ((TextView) findViewById(R.id.textView4)).setText(MessageFormat.format("Heartbeat: {0}", value));
                break;
            case Util.ACCELERATION:
                ((TextView) findViewById(R.id.textView5)).setText(MessageFormat.format("Acceleration: {0}", value));
                break;
        }

        ((TextView) findViewById(R.id.textView6)).setText(MessageFormat.format("Beacon-ID: {0}", beaconID));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBluetoothLogic.close();
    }
}
