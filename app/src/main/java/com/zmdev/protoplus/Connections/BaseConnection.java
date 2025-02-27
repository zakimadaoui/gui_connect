package com.zmdev.protoplus.Connections;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.CallSuper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Abstract class for connection classes
 * Each class inits :
 *      - input and output stream
 * Each class implements :
 *      - write()
 *      - disconnect() -> MUST CLOSE STREAMS AND SOCKETS + unregisterCallbacks
 *      - registerXXXCallback()
 *      - unregisterCallbacks()
 *
 *  RULES:
 *  one receiving thread can run at a time
 *  unregisterCallbacks() must called after finishing with the receiving thread
 *
 */
public abstract class BaseConnection {

    public enum ConnectionType {BT,USB, TCP,NO_CONNECTION}

    private static final String TAG = "BaseConnection";
    protected IncomingTextCallback textInCallback;
    protected IncomingCommandCallback commandInCallback;
    protected InputStream connectionInputStream;
    protected OutputStream connectionOutputStream;
    protected ReceiveTextThread receiveTextThread;
    protected ReceiveCommandThread receiveCommandThread;
    private boolean isInUse = false;

    //====================== To handle in specific case ====================

    abstract public void write(String data);
    abstract public void disconnect(); //close connection

    //============================= handle here ============================
    @CallSuper
    public void registerToIncomingTextCallback(IncomingTextCallback callback)throws ConnectionClassInUseException{
        if(isInUse) throw new ConnectionClassInUseException("class already registered to another listener");
        isInUse = true;
        this.textInCallback = callback;
        receiveTextThread = new ReceiveTextThread(connectionInputStream);
        receiveTextThread.start();
    }


    @CallSuper
    public void registerToIncomingCommandCallback(IncomingCommandCallback callback) throws ConnectionClassInUseException{
        if(isInUse) throw new ConnectionClassInUseException("class already registered to another listener");
        isInUse = true;
        this.commandInCallback = callback;
        receiveCommandThread = new ReceiveCommandThread(connectionInputStream);
        receiveCommandThread.start();
    }

    /* This must be called after getting done with receiving data. */
    @CallSuper
    public void unregisterFromCallbacks(){
        isInUse = false;
        if(receiveTextThread != null)  receiveTextThread.stopReceiving();
        if(receiveCommandThread != null)  receiveCommandThread.stopReceiving();
    }


    // ================================= Interfaces ==============================
    public interface IncomingTextCallback {
        void onReceive(String message);

    }

    public interface IncomingCommandCallback {
        void onReceive(IncomingCommand command);
    }

    public interface ConnectionResultCallback {
        void onConnectionResult(boolean success, String details);
    }

    // ================================= Threads ==============================
    class ReceiveTextThread extends Thread {
        private final InputStream in;
        private boolean receiveEnabled = true;
        private final StringBuilder buffer = new StringBuilder();

        public ReceiveTextThread(InputStream in) {
            this.in = in;
        }
        @Override
        public void run() {

            while (receiveEnabled) { // Keep listening to the InputStream until an exception occurs.
                try {
                    //receive a packet
                    while (in.available() > 0) buffer.append((char) in.read());
                    //send data to UI
                    if (buffer.length() > 0) {
                        String msg = buffer.toString();
                        new Handler(Looper.getMainLooper()).post(() ->
                                textInCallback.onReceive(msg));
                        //reset buffer
                        buffer.delete(0, buffer.length());
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
        void stopReceiving() {
            receiveEnabled = false;
        }
    }

    private enum FsmState{STATE_INIT, STATE_APPEND_IDENTIFIER, STATE_APPEND_DATA}
    class ReceiveCommandThread extends Thread {
        private FsmState state = FsmState.STATE_INIT;
        private IncomingCommand incomingCommand = new IncomingCommand();
        private final InputStream in;
        private volatile boolean receiveEnabled = true;
        private StringBuilder data_buffer = new StringBuilder();

        public ReceiveCommandThread(InputStream in) {
            this.in = in;
        }

        @Override
        public void run() {
            while (receiveEnabled) { // Keep listening to the InputStream until an exception occurs.
                try {
                    //receive and run FSM here
                    while (in.available() > 0) {
                        runFSM((char) in.read());
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    break;
                }
            }

        }

        private void runFSM(char in){
            switch (state){
                case STATE_INIT:
                    if(in == '>') state = FsmState.STATE_APPEND_IDENTIFIER;
                    break;
                case STATE_APPEND_IDENTIFIER:
                    if(in != ','){
                        data_buffer.append(in);
                    }else {
                        incomingCommand.setIdentifier(data_buffer.toString());
                        data_buffer.delete(0, data_buffer.length());
                        state = FsmState.STATE_APPEND_DATA;
                    }
                    break;
                case STATE_APPEND_DATA:
                    if(in != '<'){
                        data_buffer.append(in);
                    }else {
                        incomingCommand.setData(data_buffer.toString());
                        data_buffer.delete(0, data_buffer.length());
                        state = FsmState.STATE_INIT;

                        new Handler(Looper.getMainLooper()).post(() ->
                                commandInCallback.onReceive(incomingCommand));
//                        incomingCommand = new IncomingCommand(); //need this re-init ?
                    }
                    break;
            }
        }

        void stopReceiving() {
            receiveEnabled = false;
        }
    }


}


