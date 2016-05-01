package com.thm.sensors.logic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public final class BluetoothLogic {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final int MASTER_THREAD = 0;
    private final Handler mHandler;
    private final BluetoothAdapter mBluetoothAdapter;
    private ArrayList<ConnectedThread> mThreads;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private boolean keepAcceptAlive = true;

    public BluetoothLogic(Handler handler) {
        mHandler = handler;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mThreads = new ArrayList<>();
    }

    public void startConnection(String type) {
        if (type.equals("Master")) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.run();
        } else {
            mConnectThread = new ConnectThread(new ArrayList<>(mBluetoothAdapter.getBondedDevices()).get(0));
            mConnectThread.run();
        }
    }

    private class ConnectedThread extends Thread {
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

            if (tmpOut != null) {
                mmOutStream = new DataOutputStream(tmpOut);
            } else {
                mmOutStream = null;
            }

            mmInStream = tmpIn;
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

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Bluetooth Service", MY_UUID);
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned
            while (keepAcceptAlive) {
                socket = null;

                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    if (socket.isConnected()) {
                        ConnectedThread thread = new ConnectedThread(socket);
                        thread.run();
                        mThreads.add(thread);
                    }
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            if (mmSocket.isConnected()) {
                ConnectedThread thread = new ConnectedThread(mmSocket);
                thread.run();
                mThreads.add(thread);
            }
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public boolean isMasterConnectionAvailable() {
        return !mThreads.isEmpty();
    }

    public void writeDataToMaster(byte[] data) {
        mThreads.get(MASTER_THREAD).write(data);
    }

    public void close() {
        keepAcceptAlive = false;

        for (ConnectedThread thread : mThreads) {
            thread.cancel();
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
        }

        if (mConnectThread != null) {
            mConnectThread.cancel();
        }
    }
}
