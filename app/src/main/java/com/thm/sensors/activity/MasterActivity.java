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

    public void handleMessage(Message msg) {
        System.out.println(msg);
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
                activity.handleMessage(msg);
            }
        }
    }
}
