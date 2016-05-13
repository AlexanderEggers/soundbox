package com.thm.sensors.logic;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;

import com.thm.sensors.Util;
import com.thm.sensors.activity.SlaveActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public final class BeaconLogic implements BeaconConsumer {

    private static final double MIN_RANGE_IN_METERS = 2;
    private BeaconManager mBeaconManager;
    private Activity mContext;

    public void startLogic(Activity context) {
        mContext = context;
        mBeaconManager = BeaconManager.getInstanceForApplication(mContext);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);
        mBeaconManager.setForegroundScanPeriod(100L);
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                boolean foundBeacon = false;

                for (Beacon beacon : beacons) {
                    double distance = beacon.getDistance();
                    Log.d(BeaconLogic.class.getName(), distance + "");
                    if (distance < MIN_RANGE_IN_METERS) {
                        if(beacon.getBluetoothAddress().equals(Util.connectedBeacon)) {
                            foundBeacon = true;
                            break;
                        } else if(Util.connectedBeacon == null) {
                            String deviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                            String loginData = "Login%" + beacon.getBluetoothAddress() + "%" + deviceAddress;
                            ((SlaveActivity) mContext).sendSensorData(loginData);
                            Util.connectedBeacon = beacon.getBluetoothAddress();
                            foundBeacon = true;
                            break;
                        }
                    }
                }

                if(!foundBeacon) {
                    if(Util.connectedBeacon != null) {
                        String deviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
                        String logoutData = "Logout%" + Util.connectedBeacon + "%" + deviceAddress;
                        ((SlaveActivity) mContext).sendSensorData(logoutData);
                        Util.connectedBeacon = null;
                        Util.isLogin = false;
                    }
                }
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.getStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return mContext.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        mContext.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return mContext.bindService(intent, serviceConnection, i);
    }

    public void onResume() {
        mBeaconManager.bind(this);
    }

    public void onPause() {
        mBeaconManager.unbind(this);
    }
}
