package com.thm.sensors.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.thm.sensors.Util;

public final class AppStartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Util.DEV_MODE) {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SlaveActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }
}
