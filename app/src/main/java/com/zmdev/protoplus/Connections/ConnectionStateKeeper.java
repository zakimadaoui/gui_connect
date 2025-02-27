package com.zmdev.protoplus.Connections;

import static com.zmdev.protoplus.Connections.BaseConnection.ConnectionType.NO_CONNECTION;

public class ConnectionStateKeeper {

    private static final String TAG = "ConnectionStateKeeper";
    private static ConnectionStateKeeper instance;
    private boolean connected = false;
    private BaseConnection.ConnectionType connectionType = NO_CONNECTION;
    private String connectedToWho = "N/A";

    public static ConnectionStateKeeper getInstance() {
        if (instance == null) instance = new ConnectionStateKeeper();
        return instance;
    }

    public void setStatus(BaseConnection.ConnectionType type, String connectedToWho) {
        this.connectionType = type;
        this.connectedToWho = connectedToWho;
        connected = true;
    }

    public boolean isConnected() {
        return connected;
    }

    public BaseConnection.ConnectionType getConnectionType() {
        return connectionType;
    }

    public String getConnectedToWho() {
        return connectedToWho;
    }

    public void disconnect() {
        BaseConnection connection = getConnection();
        if (connection != null)  connection.disconnect();
        connected = false;
        connectionType = null;
        connectedToWho = "N/A";
    }

    public BaseConnection getConnection() {
        if(connectionType == null) return null;
        switch (connectionType) {
            case BT:
                return BluetoothConnection.getInstance();
            case USB:
                return UARTconnection.getInstance();
            case TCP:
                return TcpConnection.getInstance();
        }
        return null;
    }
}
