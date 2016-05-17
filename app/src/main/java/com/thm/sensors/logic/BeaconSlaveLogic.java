package com.thm.sensors.logic;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import com.thm.sensors.Util;
import com.thm.sensors.activity.SlaveActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public final class BeaconSlaveLogic extends BeaconLogic {

    @Override
    public void onBeaconServiceConnect() {
        super.onBeaconServiceConnect();

        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                boolean foundBeacon = false;

                for (Beacon beacon : beacons) {
                    double distance = beacon.getDistance();
                    Log.d(BeaconSlaveLogic.class.getName(), distance + "");
                    if (distance < MIN_RANGE_IN_METERS) {
                        if (beacon.getBluetoothAddress().equals(Util.connectedBeacon)) {
                            foundBeacon = true;
                            break;
                        } else if (Util.connectedBeacon == null) {
                            String deviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                            String loginData = "Login%" + beacon.getBluetoothAddress() + "%" + deviceAddress + "%";
                            ((SlaveActivity) mContext).sendSensorData("Login", loginData);
                            Util.connectedBeacon = beacon.getBluetoothAddress();
                            foundBeacon = true;
                            break;
                        }
                    }
                }

                if (!foundBeacon && Util.isLogin) {
                    if (Util.connectedBeacon != null) {
                        String deviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                        String logoutData = "Logout%" + Util.connectedBeacon + "%" + deviceAddress + "%";
                        ((SlaveActivity) mContext).sendSensorData("Logout", logoutData);
                    }
                }
            }
        });
    }
}
