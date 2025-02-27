package com.zmdev.protoplus.Connections;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;

import java.util.HashMap;

public class UARTconnection extends BaseConnection {
    public final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    private static final String TAG = "UsbConnection";

    public static UARTconnection instance;
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbSerialDevice usbSerialDevice;
    private final Context appContext;
    private ConnectionResultCallback connectionResultCallback;
    private UartConfiguration uartConfigs;


    private UARTconnection(Context appContext) {
        this.appContext = appContext;
        usbManager = (UsbManager) appContext.getSystemService(Context.USB_SERVICE);

        //Set filters for, USB permission / Attach / Detach
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        appContext.registerReceiver(receiver, filter);
    }

    public static UARTconnection init(Context appContext) {
        instance = new UARTconnection(appContext);
        return instance;
    }

    public static UARTconnection getInstance() {
        return instance;
    }
    public void connectTo(UsbDevice device, UartConfiguration configs) {
        this.uartConfigs = configs;
        this.device = device;
        PendingIntent pi = PendingIntent.getBroadcast(appContext, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
        usbManager.requestPermission(device, pi);
    }
    public void setConnectionResultCallback(ConnectionResultCallback callback) {
        connectionResultCallback = callback;
    }
    public HashMap<String, UsbDevice> getUsbDevicesMap() {
        return usbManager.getDeviceList();
    }


    //Broadcast Receiver to  start and stop the Serial connection.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case ACTION_USB_PERMISSION:

                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        //start opened Device and create usbSerialDevice
                        usbDeviceConnection = usbManager.openDevice(device);
                        usbSerialDevice = UsbSerialDevice.createUsbSerialDevice(device, usbDeviceConnection);

                        if (usbSerialDevice != null) {
                            if (usbSerialDevice.open()) {

                                //Set Serial Connection Configuration and callback.
                                usbSerialDevice.setBaudRate(uartConfigs.getBaudRate());
                                usbSerialDevice.setDataBits(uartConfigs.getDataBitsValue());
                                usbSerialDevice.setParity(uartConfigs.getParityValue());
                                usbSerialDevice.setStopBits(uartConfigs.getStopBitValue());
                                usbSerialDevice.setFlowControl(uartConfigs.getFlowControlValue());

                                //init streams
                                connectionInputStream = usbSerialDevice.getInputStream();
                                connectionOutputStream = usbSerialDevice.getOutputStream();

                                connectionResultCallback.onConnectionResult(true,"Connection Successful !");
                            } else {
                                connectionResultCallback.onConnectionResult(false,"ERROR: PORT NOT OPEN");
                            }
                        } else {
                            connectionResultCallback.onConnectionResult(false,"ERROR: PORT IS NULL");
                        }
                    } else {
                        connectionResultCallback.onConnectionResult(false,"ERROR: PERM NOT GRANTED");
                    }
                    break;

                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                   //todo:
                    Log.d(TAG, "onReceive: ACTION_USB_DEVICE_ATTACHED");
                    break;

                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    //todo:
                    Log.d(TAG, "onReceive: ACTION_USB_DEVICE_DETACHED");
                    break;
            }
        }
    };


    //========================== BaseConnection functionality =======================
    @Override
    public void write(String message) {
        usbSerialDevice.write(message.getBytes());
    }

    @Override
    public void disconnect() {
        unregisterFromCallbacks();
        try {
            connectionInputStream.close();
            connectionOutputStream.close();
            usbDeviceConnection.close();
            usbSerialDevice.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = null;
    }

}

