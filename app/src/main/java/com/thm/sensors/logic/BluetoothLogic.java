package com.thm.sensors.logic;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public final class BluetoothLogic {

    private final BluetoothManager mBluetoothManager;
    private final Handler mHandler;
    private final UUID uuid = UUID.fromString("0000110E-0000-1000-8000-00805F9B34FB"); //TODO: get UUID of device

    public BluetoothLogic(Activity context, Handler mHandler) {
        this.mHandler = mHandler;
        mBluetoothManager = (BluetoothManager) context.getSystemService(Activity.BLUETOOTH_SERVICE);
    }

    public ConnectedThread getMaster() {
        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() > 0) {
            BluetoothDevice device = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).get(0);
            BluetoothSocket socket = null;
            try {
                socket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new ConnectedThread(socket);
        } else {
            Log.d(BluetoothLogic.class.getName(), "No connected devices");
            return null;
        }
    }

    public ArrayList<ConnectedThread> getSlaves() {
        if (mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT).size() > 0) {
            ArrayList<ConnectedThread> threads = new ArrayList<>();

            for (BluetoothDevice device : mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT)) {
                BluetoothSocket socket = null;
                try {
                    socket = device.createRfcommSocketToServiceRecord(uuid);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                threads.add(new ConnectedThread(socket));
            }
            return threads;
        } else {
            Log.d(BluetoothLogic.class.getName(), "No connected devices");
            return null;
        }
    }

    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(0, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
