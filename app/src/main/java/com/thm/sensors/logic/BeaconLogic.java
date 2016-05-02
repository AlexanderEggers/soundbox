package com.thm.sensors.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.thm.sensors.R;
import com.thm.sensors.Util;
import com.thm.sensors.activity.SlaveActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
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
        mBeaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    double distance = beacons.iterator().next().getDistance();
                    Log.d(BeaconLogic.class.getName(), distance + "");
                    if (distance < MIN_RANGE_IN_METERS) {
                        int beaconID = beacons.iterator().next().getId1().toInt();
                        String text = MessageFormat.format("Proximity Value: {0}", (float) distance);
                        ((TextView) mContext.findViewById(R.id.textView2)).setText(text);
                        ((SlaveActivity) mContext).sendSensorData(Util.PROXIMITY, beaconID, (float) distance);
                        Log.i(BeaconLogic.class.getName(), text);
                    }
                }
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
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
