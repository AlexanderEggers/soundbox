package com.haw.soundbox;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public final class AccelerationLogic implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Activity mContext;
    private boolean isActive;

    public void startLogic(Activity context) {
        mContext = context;

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            Log.i(AccelerationLogic.class.getName(), "x: " + x);
            Log.i(AccelerationLogic.class.getName(), "y: " + y);
            Log.i(AccelerationLogic.class.getName(), "z: " + z);

            if((x > 1 || x < -1) || (y > 1 || y < -1) || (z > 10 || z < 9) && !isActive) {
                isActive = true;
                mContext.findViewById(R.id.content_main_body).setBackgroundColor(
                        mContext.getResources().getColor(R.color.colorActive));
            } else if (isActive) {
                isActive = false;
                mContext.findViewById(R.id.content_main_body).setBackgroundColor(
                        mContext.getResources().getColor(R.color.colorNotActive));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onResume() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        mSensorManager.unregisterListener(this);
    }
}
