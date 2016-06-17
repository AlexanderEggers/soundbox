package com.thm.soundbox.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.thm.soundbox.R;
import com.thm.soundbox.Util;

public final class AppStartActivity extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 0;
    private boolean mRequestingPermission, mOpenLocationActivity, mOnResumeFix;

    @Override
    protected void onResume() {
        super.onResume();

        if (mOpenLocationActivity && mOnResumeFix) {
            moveToNextActivity();
        }

        if (!mOnResumeFix) {
            mOnResumeFix = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_start_activity);

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            checkLocation();
        } else {
            mRequestingPermission = true;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (!bluetooth.isEnabled()) {
            bluetooth.enable();
        }

        if (!mRequestingPermission && !mOpenLocationActivity) {
            moveToNextActivity();
        }
    }

    private void moveToNextActivity() {
        if (Util.DEV_MODE) {
            Intent intent = new Intent(this, MenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SlaveActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
        finish();
        overridePendingTransition(0, 0);
    }

    private void checkLocation() {
        LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!location.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mOpenLocationActivity = true;
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocation();
                    moveToNextActivity();
                } else {
                    finish();
                }
                break;
        }
    }
}
