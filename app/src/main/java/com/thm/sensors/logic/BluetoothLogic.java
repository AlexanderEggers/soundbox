package com.thm.sensors.logic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public final class BluetoothLogic {

    private final Handler mHandler;

    public BluetoothLogic(Handler handler) {
        mHandler = handler;
    }

    @Nullable
    public ConnectedThread getMaster() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
        ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());

        if (pairedDevices.size() > 0) {
            BluetoothDevice device = pairedDevices.get(0);
            BluetoothSocket socket = null;
            try {
                socket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                socket.connect();
                return new ConnectedThread(socket);
            } catch (IOException e) {
                e.printStackTrace();

                try {
                    if(socket != null) {
                        socket.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return null;
        } else {
            Log.d(BluetoothLogic.class.getName(), "No connected devices");
            return null;
        }
    }

    @Nullable
    public ArrayList<ConnectedThread> getSlaves() {
        ArrayList<ConnectedThread> threads = new ArrayList<>();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.cancelDiscovery();
        ArrayList<BluetoothDevice> pairedDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                BluetoothSocket socket = null;
                try {
                    socket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                    socket.connect();
                    threads.add(new ConnectedThread(socket));
                } catch (IOException e) {
                    e.printStackTrace();

                    try {
                        if(socket != null) {
                            socket.close();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            if(!threads.isEmpty()) {
                return threads;
            } else {
                return null;
            }
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

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            System.out.println("TEST");

            /*
            first 12 bytes are reserved for the data name (like 'Heartbeat')
            next 4 bytes are reserved for the beacon id (like '1')
            last 4 bytes are reserved for the data value (like '1.25')
            */

            byte[] buffer = new byte[20];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(0, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
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
