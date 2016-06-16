package com.thm.soundbox.logic;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.thm.soundbox.R;
import com.thm.soundbox.Util;
import com.thm.soundbox.activity.SettingsActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.MessageFormat;
import java.util.Collection;

public final class BeaconMasterLogic extends BeaconLogic {

    private Handler mHandler;

    public void startLogic(Activity context, Handler handler) {
        super.startLogic(context);
        mHandler = handler;
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
                        Log.d(BeaconMasterLogic.class.getName(), "Distance: " + distance);
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
