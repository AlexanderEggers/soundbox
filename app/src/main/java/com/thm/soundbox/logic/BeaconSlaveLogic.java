package com.thm.soundbox.logic;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.thm.soundbox.Util;
import com.thm.soundbox.activity.SlaveActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public final class BeaconSlaveLogic extends BeaconLogic {

    @Override
    public void startLogic(Activity context) {
        super.startLogic(context);
        mBeaconManager.setForegroundScanPeriod(1000L);
    }

    @Override
    public void onBeaconServiceConnect() {
        super.onBeaconServiceConnect();

        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                boolean foundBeacon = false;

                for (Beacon beacon : beacons) {
                    double distance = beacon.getDistance();
                    Log.d(BeaconSlaveLogic.class.getName(), "Beacon: " + beacon + " " + distance + "");
                    if (distance <= Util.MIN_RANGE_IN_METERS) {
                        foundBeacon = true;

                        if (Util.connectedBeacon == null) {
                            Util.connectedBeacon = beacon.getBluetoothAddress();
                            String deviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                            String loginData = "Login%" + beacon.getBluetoothAddress() + "%" + deviceAddress + "%";
                            Log.i(BeaconSlaveLogic.class.getName(), "Device is trying to login");
                            ((SlaveActivity) mContext).sendSensorData("Login", loginData);
                        }
                        break;
                    }
                }

                if (!foundBeacon && Util.isLogin && !Util.isLoggingOut) {
                    if (Util.connectedBeacon != null) {
                        Util.isLogin = false;
                        Util.isLoggingOut = true;
                        String deviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                        String logoutData = "Logout%" + Util.connectedBeacon + "%" + deviceAddress + "%";
                        ((SlaveActivity) mContext).sendSensorData("Logout", logoutData);
                    }
                }
            }
        });
    }
}
