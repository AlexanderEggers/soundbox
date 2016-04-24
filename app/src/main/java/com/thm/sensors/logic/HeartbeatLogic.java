package com.thm.sensors.logic;

import android.app.Activity;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.thm.sensors.R;

public final class HeartbeatLogic implements SlaveLogic {

    public void startLogic(Activity context) {
        Button button = (Button) context.findViewById(R.id.button5);
        button.setOnTouchListener(new View.OnTouchListener() {

            private Handler mHandler;
            private MotionEvent event;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        this.event = event;
                        mHandler = new Handler();
                        mHandler.postDelayed(mAction, 500);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        this.event = null;
                        mHandler.removeCallbacks(mAction);
                        mHandler = null;
                        break;
                }
                return false;
            }

            Runnable mAction = new Runnable() {
                @Override
                public void run() {
                    System.out.println("Performing action...");
                    System.out.println(event.getPressure());
                    mHandler.postDelayed(this, 500);
                }
            };
        });
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }
}
