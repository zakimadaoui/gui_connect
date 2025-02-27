package com.zmdev.protoplus.Connections;

import java.io.IOException;
import java.net.Socket;

public class TcpConnection extends BaseConnection {

    private static final String TAG = "TcpConnection";
    private OnConnectionResultCallback connectionResultCallback;
    private static TcpConnection instance;
    private Socket socket;

    private TcpConnection() {
    }

    public static TcpConnection getInstance() {
        if (instance == null) instance = new TcpConnection();
        return instance;
    }

    public void connectTo(String ip, int port) {
        new Thread(() -> {
            try {
                //create a socket and attempt to connect to the endpoint wit port and ip
                socket = new Socket(ip, port);
                //connection successful
                if (connectionResultCallback != null)
                    connectionResultCallback.onConnectionResult(true);
                connectionInputStream = socket.getInputStream();
                connectionOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                if (connectionResultCallback !=null)
                    connectionResultCallback.onConnectionResult(false);
                e.printStackTrace();
            }
        }).start();
    }

    //================================ base class functions ========================================
    @Override
    public void write(String data) {
        new Thread(() -> {
            try {
                connectionOutputStream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void disconnect() {

        unregisterFromCallbacks();
        if (receiveTextThread != null) receiveTextThread.stopReceiving();

        try {//close all resources
            connectionInputStream.close();
            connectionOutputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = null;
    }

    //================================= Callbacks interfaces ===================================

    public interface OnConnectionResultCallback {
        void onConnectionResult(boolean success);
    }

    public void setOnConnectionResultCallback(OnConnectionResultCallback callback) {
        connectionResultCallback = callback;
    }

}
