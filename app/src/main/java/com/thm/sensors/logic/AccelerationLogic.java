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

public final class AccelerationLogic implements SensorEventListener, SlaveLogic {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] gravity = {0f, 0f, 0f}, linear_acceleration = new float[3];
    private Activity context;

    public void startLogic(Activity context) {
        this.context = context;
        ((TextView) context.findViewById(R.id.textView)).setText("Acceleration Value: ");
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 0.8f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            String text = MessageFormat.format("Acceleration Value: {0}", linear_acceleration[2]);
            ((TextView) context.findViewById(R.id.textView)).setText(text);
            ((SlaveActivity) context).writeData("Acceleration");
            ((SlaveActivity) context).writeData((int) linear_acceleration[2]);
            ((SlaveActivity) context).writeData((int)((linear_acceleration[2] - (int) linear_acceleration[2]) * 100));
            Log.i(AccelerationLogic.class.getName(), text);
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
