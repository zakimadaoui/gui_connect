package com.zmdev.protoplus.Connections;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnection extends BaseConnection {

    public static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TAG = "BluetoothConnection";
    private static BluetoothConnection instance;
    private final BluetoothAdapter adapter;
    private BluetoothSocket socket;
    private BluetoothResultCallback bluetoothResultCallback;
    private BluetoothDiscoveryCallback btDiscoveryCallback;

    private BluetoothConnection() {
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static BluetoothConnection getInstance() {
        if (instance == null) instance = new BluetoothConnection();
        return instance;
    }

    public void connectTo(BluetoothDevice device) {
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(BluetoothConnection.MY_UUID);
            new Thread(() -> {
                /* After a client calls bluetoothSocket.connect() method
                 * the system performs an SDP lookup to find the remote device with the matching UUID.
                 * If the lookup is successful and the remote device accepts the connection, it shares the RFCOMM channel to
                 * use during the connection, and the connect() method returns. If the connection fails, or if the connect() method times out
                 * (after about 12 seconds), then the method throws an IOException.
                 */
                try {

                    //Try to connect , this is a blocking call !
                    socket.connect();
                    // No exception ? BT Connection Successful !
                    connectionInputStream =  socket.getInputStream();
                    connectionOutputStream = socket.getOutputStream();

                    //push result to UI
                    new Handler(Looper.getMainLooper()).post(() -> {
                        bluetoothResultCallback.onConnectionResult(true);
                    });

                } catch (IOException e) {
                    //push result to UI
                    new Handler(Looper.getMainLooper()).post(() -> {
                        bluetoothResultCallback.onConnectionResult(false);
                    });
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //start a broadcast receiver for discovering nearby bluetooth devices
    public void startNewDiscovery(Context context, View loadingSnackBarView) {
        //Start Discovery
        if (adapter.startDiscovery()) {
            //Show user loading....
            if (loadingSnackBarView != null) Snackbar.make(loadingSnackBarView, "Discovering Nearby BT devices ...", Snackbar.LENGTH_LONG).show();
            //Register the discovery filter for BT_Receiver
            context.registerReceiver(btReceiver,  new IntentFilter(BluetoothDevice.ACTION_FOUND));
            context.registerReceiver(btReceiver,  new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
        } else {
            if (adapter.isDiscovering() && loadingSnackBarView != null)
                Snackbar.make(loadingSnackBarView, "Please wait ...", Snackbar.LENGTH_LONG).show();
            else if (loadingSnackBarView != null)
                Snackbar.make(loadingSnackBarView, "Oops! these seems to be a problem !", Snackbar.LENGTH_SHORT).show();
        }
    }

    //this must be called after calling @startNewDiscovery(Context context, View loadingSnackBarView);
    public void unregisterReceiver(Context context) {
        try {
            context.unregisterReceiver(btReceiver);
        } catch (Exception ignored) { }
    }

    //========================== BaseConnection functionality =======================
    @Override
    public void write(String string) {
        new Thread(() -> {
            try {
                connectionOutputStream.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void disconnect() {

        unregisterFromCallbacks();
        if (receiveTextThread != null) receiveTextThread.stopReceiving();

        try {//close all resources (if null will produce exception)
            connectionInputStream.close();
            connectionOutputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = null;
    }
    //===============================================================================


    public interface BluetoothResultCallback {
        void onConnectionResult(boolean success);
    }

    public void setConnectionResultCallback(BluetoothResultCallback callback) {
        bluetoothResultCallback = callback;
    }

    public interface BluetoothDiscoveryCallback {
        void onDeviceFound(BluetoothDevice device);
    }
    public void setBluetoothDiscoveryCallback(BluetoothDiscoveryCallback callback) {
        btDiscoveryCallback = callback;
    }


    //========================== BT connection actions broadcast receiver ==========================
    private final BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Discovery has found a device. Get the BluetoothDevice
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDiscoveryCallback.onDeviceFound(device);
                //stop discovery after 15 seconds
                new CountDownTimer(15000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) { }
                    @Override
                    public void onFinish() { adapter.cancelDiscovery(); }
                }.start();
            }
            //todo: try to subscribe to the state keeper for such events; the keeper should keep a list of listeners
//            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())) {
//                setConnectionStatus(false, "N/A");
//            }
        }
    };
}

