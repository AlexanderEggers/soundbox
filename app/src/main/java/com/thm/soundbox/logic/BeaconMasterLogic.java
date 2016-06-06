package com.thm.soundbox.logic;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.thm.soundbox.R;
import com.thm.soundbox.Util;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.MessageFormat;
import java.util.Collection;

public final class BeaconMasterLogic extends BeaconLogic {

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
                        final String beaconAddress = fBeacon.getBluetoothAddress();
                        Util.connectedSettingsBeacon = beaconAddress;

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) mContext.findViewById(R.id.textView3))
                                        .setText(MessageFormat.format("Beacon {0}", beaconAddress));

                                if (Util.beaconColorMap.containsKey(beaconAddress)) {
                                    ((EditText) mContext.findViewById(R.id.editText))
                                            .setText(Util.beaconColorMap.get(beaconAddress));
                                    ((TextView) mContext.findViewById(R.id.textView))
                                            .setText(Util.beaconModeMap.get(beaconAddress));
                                }
                            }
                        });
                        Util.scanForBeacons = false;
                    }
                }
            }
        });
    }
}
