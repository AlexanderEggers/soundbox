package com.thm.sensors.logic;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import com.thm.sensors.R;
import com.thm.sensors.activity.SlaveActivity;

import java.text.MessageFormat;

public final class ProximityLogic implements SensorEventListener, SlaveLogic {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Activity context;

    public void startLogic(Activity context) {
        this.context = context;
        ((TextView) context.findViewById(R.id.textView2)).setText("Proximity Value: ");
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String text = MessageFormat.format("Proximity Value: {0}", event.values[0]);
        ((TextView) context.findViewById(R.id.textView2)).setText(text);
        ((SlaveActivity) context).writeData("Proximity   ");
        ((SlaveActivity) context).writeData(event.values[0]);
        Log.i(ProximityLogic.class.getName(), text);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onResume() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
    }
}
