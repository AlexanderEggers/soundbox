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
import com.thm.sensors.logic.BluetoothLogic;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class MasterActivity extends Activity {

    private ArrayList<BluetoothLogic.ConnectedThread> mThreads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final MasterActivity context = this;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Looper.prepare();
                BluetoothLogic bluetooth = new BluetoothLogic(new IncomingHandler(context));
                mThreads = bluetooth.getSlaves();

                for(BluetoothLogic.ConnectedThread thread : mThreads) {
                    thread.run();
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ((TextView) findViewById(R.id.textView3)).setText("Proximity: n/a");
        ((TextView) findViewById(R.id.textView4)).setText("Heartbeat: n/a");
        ((TextView) findViewById(R.id.textView5)).setText("Acceleration: n/a");
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
        byte[] aIdentifier = new byte[12];
        System.arraycopy(((byte[]) msg.obj), 0, aIdentifier, 0, 12);

        byte[] aBeaconID = new byte[4];
        System.arraycopy(((byte[]) msg.obj), 12, aBeaconID, 16, 4);

        byte[] aValue = new byte[4];
        System.arraycopy(((byte[]) msg.obj), 16, aValue, 20, 4);

        String identifier = new String(aIdentifier).replace(" ", "");

        ByteBuffer wrapped = ByteBuffer.wrap(aBeaconID);
        float beaconID = wrapped.getFloat();

        wrapped = ByteBuffer.wrap(aValue);
        float value = wrapped.getFloat();

        switch (identifier) {
            case "Proximity":
                ((TextView) findViewById(R.id.textView3)).setText("Proximity: " + value + " Beacon-ID: " + beaconID);
                break;
            case "Heartbeat":
                ((TextView) findViewById(R.id.textView4)).setText("Heartbeat: " + value + " Beacon-ID: " + beaconID);
                break;
            case "Acceleration":
                ((TextView) findViewById(R.id.textView4)).setText("Heartbeat: " + value + " Beacon-ID: " + beaconID);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mThreads != null) {
            for (BluetoothLogic.ConnectedThread thread : mThreads) {
                thread.cancel();
            }
        }
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<MasterActivity> mmActivity;

        IncomingHandler(MasterActivity activity) {
            mmActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MasterActivity activity = mmActivity.get();
            if (activity != null) {
                activity.handleData(msg);
            }
        }
    }
}
