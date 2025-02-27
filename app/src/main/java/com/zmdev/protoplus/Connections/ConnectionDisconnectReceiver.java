package com.zmdev.protoplus.Connections;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

public class ConnectionDisconnectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(intent.getAction())
        ||   UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
            ConnectionStateKeeper connectionModel = ConnectionStateKeeper.getInstance();
            if (connectionModel.isConnected()) {
                connectionModel.disconnect();
                Toast.makeText(context, "Connection was lost !", Toast.LENGTH_SHORT).show();
            }
        }
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) {
            Toast.makeText(context, "USB device detected", Toast.LENGTH_SHORT).show();
        }
    }
}