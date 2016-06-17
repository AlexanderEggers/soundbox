package com.thm.soundbox.logic;

import android.app.Activity;
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
                for (Beacon beacon : beacons) {
                    if (beacon.getDistance() <= Util.MIN_RANGE_IN_METERS) {
                        if(Util.connectedBeacon == null) {
                            Util.connectedBeacon = beacon.getBluetoothAddress();
                            String loginData = "Login%" + beacon.getBluetoothAddress() + "%";
                            Log.i(BeaconSlaveLogic.class.getName(), "Device is trying to login");
                            ((SlaveActivity) mContext).sendSensorData("Login", loginData);
                            break;
                        }
                    } else if (Util.connectedBeacon != null &&
                            beacon.getBluetoothAddress().equals(Util.connectedBeacon) && !Util.isLoggingOut) {
                        Util.isLogin = false;
                        Util.isLoggingOut = true;
                        String logoutData = "Logout%" + Util.connectedBeacon + "%";
                        ((SlaveActivity) mContext).sendSensorData("Logout", logoutData);
                    }
                }
            }
        });
    }
}
