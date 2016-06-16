package com.thm.soundbox.logic;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.thm.soundbox.Util;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public final class BeaconMasterLogic extends BeaconLogic {

    private Handler mHandler;

    public void startLogic(Activity context, Handler handler) {
        super.startLogic(context);
        mHandler = handler;
        mBeaconManager.setForegroundScanPeriod(250L);
    }

    @Override
    public void onBeaconServiceConnect() {
        super.onBeaconServiceConnect();

        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (Util.scanForBeacons) {
                    Beacon fBeacon = null;
                    boolean foundBeacon = false;

                    for (Beacon beacon : beacons) {
                        double distance = beacon.getDistance();
                        Log.d(BeaconSlaveLogic.class.getName(), "Beacon: " + beacon + " " + distance + "");
                        if (distance < Util.MIN_RANGE_IN_METERS) {
                            fBeacon = beacon;
                            foundBeacon = true;
                            break;
                        }
                    }

                    if (foundBeacon) {
                        Util.connectedSettingsBeacon = fBeacon.getBluetoothAddress();
                        mHandler.obtainMessage().sendToTarget();
                        Util.scanForBeacons = false;
                    }
                }
            }
        });
    }
}
