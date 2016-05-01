package com.thm.sensors.logic;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thm.sensors.R;
import com.thm.sensors.activity.SlaveActivity;

import java.text.MessageFormat;

public final class HeartbeatLogic implements SlaveLogic {

    private Activity mContext;

    public void startLogic(Activity context) {
        mContext = context;
        ((TextView) context.findViewById(R.id.textView6)).setText("Heartbeat Value: ");
        Button button = (Button) context.findViewById(R.id.button5);
        button.setOnTouchListener(new HeartbeatButton());
    }

    private void executeHeartbeat(MotionEvent event) {
        String text = MessageFormat.format("Heartbeat Value: {0}", event.getPressure());
        ((TextView) mContext.findViewById(R.id.textView6)).setText(text);
        ((SlaveActivity) mContext).sendSensorData("Heartbeat", 1, event.getPressure());
        Log.d(HeartbeatLogic.class.getName(), text);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    private class HeartbeatButton implements View.OnTouchListener {

        private Handler mmHandler;
        private MotionEvent mmEvent;

        private Runnable mmAction = new Runnable() {
            @Override
            public void run() {
                executeHeartbeat(mmEvent);
                mmHandler.postDelayed(this, 500);
            }
        };

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mmHandler != null) return true;
                    mmEvent = event;
                    mmHandler = new Handler();
                    mmHandler.postDelayed(mmAction, 500);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mmHandler == null) return true;
                    mmEvent = null;
                    mmHandler.removeCallbacks(mmAction);
                    mmHandler = null;
                    break;
            }
            return false;
        }
    }
}
