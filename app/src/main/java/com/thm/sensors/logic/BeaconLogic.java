package com.thm.sensors.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.util.Log;
import android.widget.TextView;

import com.thm.sensors.R;
import com.thm.sensors.activity.SlaveActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.MessageFormat;
import java.util.Collection;

public class BeaconLogic implements BeaconConsumer, SlaveLogic {

    private BeaconManager mBeaconManager;
    private Activity mContext;

    public void startLogic(Activity context) {
        mContext = context;
        ((TextView) context.findViewById(R.id.textView2)).setText("Proximity Value: ");
        mBeaconManager = BeaconManager.getInstanceForApplication(context);
        mBeaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    double distance = beacons.iterator().next().getDistance();
                    if (distance < 2d) {
                        String text = MessageFormat.format("Proximity Value: {0}", (float) distance);
                        ((TextView) mContext.findViewById(R.id.textView2)).setText(text);
                        ((SlaveActivity) mContext).sendSensorData("Proximity", 1, (float) distance);
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
        return mContext;
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
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
