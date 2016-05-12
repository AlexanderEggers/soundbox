package com.thm.sensors.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.activity.SlaveActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.MessageFormat;
import java.util.Collection;

public final class BeaconLogic implements BeaconConsumer, SlaveLogic {

    private static final double MIN_RANGE_IN_METERS = 2;
    private BeaconManager mBeaconManager;
    private Activity mContext;

    public void startLogic(Activity context) {
        mContext = context;
        ((TextView) mContext.findViewById(R.id.textView2)).setText("Proximity Value: ");
        mBeaconManager = BeaconManager.getInstanceForApplication(mContext);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        mBeaconManager.bind(this);
        mBeaconManager.setForegroundScanPeriod(200L);
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for (Beacon beacon : beacons) {
                    double distance = beacon.getDistance();
                    Log.d(BeaconLogic.class.getName(), distance + "");
                    if (distance < MIN_RANGE_IN_METERS) {
                        String bluetoothName = "Platzhalter";

                        int beaconID;
                        switch (bluetoothName) {
                            default:
                                beaconID = 0;
                                break;
                        }

                        final String text = MessageFormat.format("Proximity Value: {0} and ID: {1}",
                                (float) distance, beaconID);
                        ((SlaveActivity) mContext).sendSensorData(Util.PROXIMITY, beaconID, (float) distance);
                        Log.i(BeaconLogic.class.getName(), text);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                ((TextView) mContext.findViewById(R.id.textView2)).setText(text);
                            }
                        });
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

    @Override
    public void onResume() {
        mBeaconManager.bind(this);
    }

    @Override
    public void onPause() {
        mBeaconManager.unbind(this);
    }
}
