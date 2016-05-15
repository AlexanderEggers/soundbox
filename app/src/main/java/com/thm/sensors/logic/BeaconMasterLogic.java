package com.thm.sensors.logic;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.thm.sensors.R;
import com.thm.sensors.Util;

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
                boolean foundBeacon = false;

                for (Beacon beacon : beacons) {
                    double distance = beacon.getDistance();
                    Log.d(BeaconMasterLogic.class.getName(), distance + "");
                    if (distance < MIN_RANGE_IN_METERS) {
                        foundBeacon = true;
                        if (!beacon.getBluetoothAddress().equals(Util.connectedSettingsBeacon)) {
                            final String beaconAddress = beacon.getBluetoothAddress();
                            Util.connectedSettingsBeacon = beaconAddress;

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((TextView) mContext.findViewById(R.id.textView3))
                                            .setText(MessageFormat.format("Beacon {0}", beaconAddress));

                                    if(Util.beaconColorMap.containsKey(beaconAddress)) {
                                        ((EditText) mContext.findViewById(R.id.editText))
                                                .setText(Util.beaconColorMap.get(beaconAddress));
                                        ((TextView) mContext.findViewById(R.id.textView))
                                                .setText(Util.beaconModeMap.get(beaconAddress));
                                    }
                                }
                            });
                        }
                        break;
                    }
                }

                if (!foundBeacon && Util.connectedSettingsBeacon != null) {
                    Util.connectedSettingsBeacon = null;
                    ((TextView) mContext.findViewById(R.id.textView3)).setText("Beacon");
                    ((EditText) mContext.findViewById(R.id.editText)).setText("");
                    ((TextView) mContext.findViewById(R.id.textView)).setText("");
                }
            }
        });
    }
}
