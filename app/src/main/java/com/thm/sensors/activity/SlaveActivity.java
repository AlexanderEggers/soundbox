package com.thm.sensors.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewStub;
import android.widget.Toolbar;

import com.thm.sensors.R;
import com.thm.sensors.logic.AccelerationLogic;
import com.thm.sensors.logic.HeartbeatLogic;
import com.thm.sensors.logic.ProximityLogic;
import com.thm.sensors.logic.SlaveLogic;

public final class SlaveActivity extends Activity {

    private String sensor;
    private SlaveLogic logic;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sensor = getIntent().getStringExtra("sensor");
        ViewStub stub = (ViewStub) findViewById(R.id.stub);
        switch (sensor) {
            case "Proximity":
                logic = new ProximityLogic();
                logic.startLogic(this);
                stub.setLayoutResource(R.layout.slave_proximity_content);
                break;
            case "Heartbeat":
                logic = new HeartbeatLogic();
                stub.setLayoutResource(R.layout.slave_heartbeat_content);
                break;
            case "Acceleration":
                logic = new AccelerationLogic();
                logic.startLogic(this);
                stub.setLayoutResource(R.layout.slave_acceleration_content);
                break;
        }
        stub.inflate();
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
    public boolean onTouchEvent(MotionEvent me) {
        if (sensor.equals("Heartbeat")) {
            me.getPressure();
        }
        return true;
    }
}
