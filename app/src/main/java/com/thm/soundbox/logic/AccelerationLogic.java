package com.thm.soundbox.logic;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import com.thm.soundbox.R;
import com.thm.soundbox.Util;
import com.thm.soundbox.activity.SlaveActivity;

import java.text.MessageFormat;

public final class AccelerationLogic implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] mGravity = {0f, 0f, 0f}, mLinearAcceleration = new float[3];
    private Activity mContext;

    public void startLogic(Activity context) {

        if (Util.INTERPOLATION) {
            for (int i = 0; i < Util.lastSensorValues.length; i++) {
                for (int k = 0; i < 3; i++) {
                    Util.lastSensorValues[i][k] = 0.0f;
                }
            }
        }

        mContext = context;

        if (Util.DEV_MODE) {
            ((TextView) context.findViewById(R.id.textViewX)).setText("Acceleration Value X: ");
            ((TextView) context.findViewById(R.id.textViewY)).setText("Acceleration Value Y: ");
            ((TextView) context.findViewById(R.id.textViewZ)).setText("Acceleration Value Z: ");
            ((TextView) context.findViewById(R.id.beaconID)).setText("BeaconID: ");
        }

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && Util.isLogin) {
            final float alpha = 0.8f;

            if (Util.INTERPOLATION) {
                Util.lastSensorValues[Util.valueCounter][0] = (1 - alpha) * event.values[0];
                Util.lastSensorValues[Util.valueCounter][1] = (1 - alpha) * event.values[1];
                Util.lastSensorValues[Util.valueCounter][2] = (1 - alpha) * event.values[2];
                Util.valueCounter = (Util.valueCounter + 1) % Util.lastSensorValues.length;

                mLinearAcceleration[0] = Util.processArrayValues(0);
                mLinearAcceleration[1] = Util.processArrayValues(1);
                mLinearAcceleration[2] = Util.processArrayValues(2);
            } else {
                mLinearAcceleration[0] = event.values[0];
                mLinearAcceleration[1] = event.values[1];
                mLinearAcceleration[2] = event.values[2];
            }

            if (Util.gravity) {
                // Isolate the force of mGravity with the low-pass filter.
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];

                // Remove the mGravity contribution with the high-pass filter.
                mLinearAcceleration[0] = event.values[0] - mGravity[0];
                mLinearAcceleration[1] = event.values[1] - mGravity[1];
                mLinearAcceleration[2] = event.values[2] - mGravity[2];
            }

            String data = "Data%" + Util.connectedBeacon + "%" + mLinearAcceleration[0] + ";"
                    + mLinearAcceleration[1] + ";" + mLinearAcceleration[2] + "%";
            ((SlaveActivity) mContext).sendData("data", data);

            if (Util.DEV_MODE) {
                String textX = MessageFormat.format("Acceleration Value X: {0}", mLinearAcceleration[0]);
                String textY = MessageFormat.format("Acceleration Value Y: {0}", mLinearAcceleration[1]);
                String textZ = MessageFormat.format("Acceleration Value Z: {0}", mLinearAcceleration[2]);

                ((TextView) mContext.findViewById(R.id.textViewX)).setText(textX);
                ((TextView) mContext.findViewById(R.id.textViewY)).setText(textY);
                ((TextView) mContext.findViewById(R.id.textViewZ)).setText(textZ);
                ((TextView) mContext.findViewById(R.id.beaconID)).setText(MessageFormat.format(
                        "BeaconID: {0}", Util.connectedBeacon));

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
