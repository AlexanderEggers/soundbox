package com.thm.sensors.logic;

import android.app.Activity;

public interface SlaveLogic {
    void onResume();

    void onPause();

    void startLogic(Activity context);
}
