package com.haw.soundbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private AccelerationLogic logic;

    @Override
    protected void onResume() {
        super.onResume();

        if (logic != null) {
            logic.onResume();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        logic = new AccelerationLogic();
        logic.startLogic(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (logic != null) {
            logic.onPause();
        }
    }
}
