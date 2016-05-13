package com.thm.sensors.logic;

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
                        if(!beacon.getBluetoothAddress().equals(Util.connectedSettingsBeacon)) {
                            String beaconAddress = beacon.getBluetoothAddress();
                            Util.connectedSettingsBeacon = beaconAddress;

                            ((TextView) mContext.findViewById(R.id.textView3))
                                    .setText(MessageFormat.format("Beacon:{0}", beaconAddress));
                            ((EditText) mContext.findViewById(R.id.editText))
                                    .setText(Util.beaconColorMap.get(beaconAddress));
                            ((EditText) mContext.findViewById(R.id.editText2))
                                    .setText(Util.beaconModeMap.get(beaconAddress));
                        }
                        break;
                    }
                }

                if(!foundBeacon && Util.connectedSettingsBeacon != null) {
                    Util.connectedSettingsBeacon = null;
                    ((TextView) mContext.findViewById(R.id.textView3)).setText("Beacon:");
                    ((EditText) mContext.findViewById(R.id.editText)).setText("");
                    ((EditText) mContext.findViewById(R.id.editText2)).setText("");
                }
            }
        });
    }
}
