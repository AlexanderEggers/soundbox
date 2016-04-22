package com.thm.sensors.logic;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public final class ProximityLogic implements SensorEventListener, SlaveLogic {

    private SensorManager mSensorManager;
    private Sensor mSensor;

    public void startLogic(Activity context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];

        if (distance == 0) {
            Log.i(ProximityLogic.class.getName(), "Found proximity event!");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onResume() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
    }
}
