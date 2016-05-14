package com.thm.sensors.logic;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.activity.SlaveActivity;

import java.text.MessageFormat;

public final class AccelerationLogic implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] mGravity = {0f, 0f, 0f}, mLinearAcceleration = new float[3];
    private Activity mContext;

    public void startLogic(Activity context) {
        mContext = context;

        if (Util.DEV_MODE) {
            ((TextView) context.findViewById(R.id.textViewX)).setText("Acceleration Value X: ");
            ((TextView) context.findViewById(R.id.textViewY)).setText("Acceleration Value Y: ");
            ((TextView) context.findViewById(R.id.textViewZ)).setText("Acceleration Value Z: ");
        }

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 0.8f;

            // Isolate the force of mGravity with the low-pass filter.
            mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
            mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
            mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];

            // Remove the mGravity contribution with the high-pass filter.
            mLinearAcceleration[0] = event.values[0] - mGravity[0];
            mLinearAcceleration[1] = event.values[1] - mGravity[1];
            mLinearAcceleration[2] = event.values[2] - mGravity[2];

            String deviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
            String data = "Data%" + deviceAddress + "%" + Util.connectedBeacon + "%" + mLinearAcceleration[0] + ";"
                    + mLinearAcceleration[1] + ";" + mLinearAcceleration[2];
            ((SlaveActivity) mContext).sendSensorData(data);

            if (Util.DEV_MODE) {
                String textX = MessageFormat.format("Acceleration Value X: {0}", mLinearAcceleration[0]);
                String textY = MessageFormat.format("Acceleration Value Y: {0}", mLinearAcceleration[1]);
                String textZ = MessageFormat.format("Acceleration Value Z: {0}", mLinearAcceleration[2]);

                ((TextView) mContext.findViewById(R.id.textViewX)).setText(textX);
                ((TextView) mContext.findViewById(R.id.textViewY)).setText(textY);
                ((TextView) mContext.findViewById(R.id.textViewZ)).setText(textZ);

                Log.i(AccelerationLogic.class.getName(), textX);
                Log.i(AccelerationLogic.class.getName(), textY);
                Log.i(AccelerationLogic.class.getName(), textZ);
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
