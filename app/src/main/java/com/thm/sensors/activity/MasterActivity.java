package com.thm.sensors.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
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

    private ArrayList<BluetoothLogic.ConnectedThread> threads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        BluetoothLogic logic = new BluetoothLogic(this, new IncomingHandler(this));
        threads = logic.getSlaves();

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

    public void handleData(Message msg) {
        byte[] name = new byte[12];
        System.arraycopy(((byte[]) msg.obj), 0, name, 0, 12);

        byte[] dataArray = new byte[4];
        System.arraycopy(((byte[]) msg.obj), 12, dataArray, 12, 4);

        String identifier = new String(name).replace(" ", "");

        ByteBuffer wrapped = ByteBuffer.wrap(dataArray);
        float data = wrapped.getFloat();

        switch (identifier) {
            case "Proximity":
                ((TextView) findViewById(R.id.textView3)).setText("Proximity: " + data);
                break;
            case "Heartbeat":
                ((TextView) findViewById(R.id.textView4)).setText("Heartbeat: " + data);
                break;
            case "Acceleration":
                ((TextView) findViewById(R.id.textView4)).setText("Heartbeat: " + data);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (threads != null) {
            for (BluetoothLogic.ConnectedThread thread : threads) {
                thread.cancel();
            }
        }
    }

    private static class IncomingHandler extends Handler {
        private final WeakReference<MasterActivity> mActivity;

        IncomingHandler(MasterActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MasterActivity activity = mActivity.get();
            if (activity != null) {
                activity.handleData(msg);
            }
        }
    }
}
