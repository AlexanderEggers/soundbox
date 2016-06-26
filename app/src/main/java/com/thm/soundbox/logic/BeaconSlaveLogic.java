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
                Beacon closestBeacon = null;
                long shortestDistance = Long.MAX_VALUE;

                for (Beacon beacon : beacons) {
                    long distance = Math.round(beacon.getDistance());
                    if (distance <= Util.MIN_RANGE) {
                        if (distance < shortestDistance) {
                            closestBeacon = beacon;
                            shortestDistance = distance;
                        }
                    }
                }

                if (closestBeacon != null) {
                    if (Util.connectedBeacon != null) {
                        if (!closestBeacon.getBluetoothAddress().equals(Util.connectedBeacon)) {
                            logout();
                            login(closestBeacon);
                        }
                    } else {
                        login(closestBeacon);
                    }
                } else if (Util.connectedBeacon != null && !Util.isLoggingOut) {
                    logout();
                }
            }
        });
    }

    private void login(Beacon beacon) {
        Util.connectedBeacon = beacon.getBluetoothAddress();
        String loginData = "Login%" + beacon.getBluetoothAddress() + "%";
        Log.i(BeaconSlaveLogic.class.getName(), "Device is trying to login.");
        ((SlaveActivity) mContext).sendData("Login", loginData);
    }

    private void logout() {
        Util.isLogin = false;
        Util.isLoggingOut = true;
        String logoutData = "Logout%" + Util.connectedBeacon + "%";
        Log.i(BeaconSlaveLogic.class.getName(), "Device is trying to logout.");
        ((SlaveActivity) mContext).sendData("Logout", logoutData);
    }
}
