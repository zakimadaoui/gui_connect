package com.zmdev.protoplus.Fragments;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zmdev.protoplus.Adapters.DeviceAdapter;
import com.zmdev.protoplus.Connections.BaseConnection;
import com.zmdev.protoplus.Connections.BluetoothConnection;
import com.zmdev.protoplus.Connections.ConnectionStateKeeper;
import com.zmdev.protoplus.Connections.TcpConnection;
import com.zmdev.protoplus.Connections.UARTconnection;
import com.zmdev.protoplus.Dialogs.BtRequirementsDialog;
import com.zmdev.protoplus.Dialogs.TcpDialog;
import com.zmdev.protoplus.Dialogs.UartConfigDialog;
import com.zmdev.protoplus.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ConnectionFragment extends Fragment implements DeviceAdapter.OnDeviceClickedListener {
    //todo: clean this class and the connection classes it depends on
    private static final String TAG = "BluetoothPageFragment";
    private Context mContext;
    List<BluetoothDevice> btDevices = new ArrayList<>();
    ConnectionStateKeeper stateKeeper;

    //views
    RecyclerView recycler;
    DeviceAdapter recyclerAdapter;
    TextView device_name_txt;
    TextView connection_status_txt;
    LinearLayout status_layout;
    LinearLayout devices_layout;
    MaterialButton disconnect_btn;
    private BluetoothConnection bluetoothConnection;
    private ProgressDialog progressDialog;

    //TODO: separate connections implementation from this class, this class gets only results and sends requests

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        stateKeeper = ConnectionStateKeeper.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //init recycler
        recyclerAdapter = new DeviceAdapter(this);
        recycler = view.findViewById(R.id.devices_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(recyclerAdapter);

        //init views & UI
        device_name_txt = view.findViewById(R.id.connected_to_txt);
        connection_status_txt = view.findViewById(R.id.connection_status_text);
        status_layout = view.findViewById(R.id.connection_details_layout);
        devices_layout = view.findViewById(R.id.connect_to_layout);
        disconnect_btn = view.findViewById(R.id.disconnect_btn);
        initUI();

        //BT button
        view.findViewById(R.id.connect_ble_btn).setOnClickListener(v -> {
            BtRequirementsDialog dialog = new BtRequirementsDialog();
            dialog.show(getChildFragmentManager(),"ReqsDialog");
            dialog.setOnFulfillCallback(new BtRequirementsDialog.OnReqsFulfilledCallback() {
                @Override
                public void onScanBtDevices() {
                    discoverBleDevices();
                }

                @Override
                public void onShowPairedDevices() {
                    bluetoothConnection = BluetoothConnection.getInstance();
                    btDevices.clear();// clear previous list !
                    recyclerAdapter.setBtDevices(btDevices); //clear recycler
                    btDevices.addAll(BluetoothAdapter.getDefaultAdapter().getBondedDevices());
                    recyclerAdapter.setBtDevices(btDevices);
                    showDevicesRecycler(true);
                }
            });

        });

        //UART button
        view.findViewById(R.id.usb_btn).setOnClickListener(v -> {
            showDevicesRecycler(true);
            discoverAndSetupUARTConnection();
        });

        //TCP button
        view.findViewById(R.id.tcp_btn).setOnClickListener(v -> {
            new TcpDialog((ip, port) -> {
                showProgressDialog("Connecting to " + ip+":"+port);
                TcpConnection connection = TcpConnection.getInstance();
                connection.setOnConnectionResultCallback(success -> {
                    hideProgressDialog();
                    if (success) {
                        stateKeeper.setStatus(BaseConnection.ConnectionType.TCP, ip);
                        setConnectionStatus(true, ip);
                    } else {
                        Snackbar.make(getView(),"Connection Failed!",Snackbar.LENGTH_LONG).show();
                    }
                });
                connection.connectTo(ip,port);
            }).show(getParentFragmentManager(),"tcp_tag");
        });

        //Disconnect button
        disconnect_btn.setOnClickListener(v -> {
            setConnectionStatus(false,"N/A");
            stateKeeper.disconnect();
        });

    }

    private void discoverAndSetupUARTConnection() {
        //init UsbConnection instance
        UARTconnection uartConnection = UARTconnection.init(getContext().getApplicationContext());
        //load list of devices
        HashMap<String, UsbDevice> usbDevices = uartConnection.getUsbDevicesMap();
        List<UsbDevice> usbDevicesList = new ArrayList<>();

        for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
            usbDevicesList.add(entry.getValue());
        }

        if (usbDevices.isEmpty())
            Toast.makeText(mContext, "no USB device found !", Toast.LENGTH_SHORT).show();
        else
            recyclerAdapter.setUsbDevices(usbDevicesList);

    }

    private void discoverBleDevices() {
        btDevices.clear();// clear previous list !
        recyclerAdapter.setBtDevices(btDevices); //clear recycler
        showDevicesRecycler(true);
        //start discovery
        bluetoothConnection = BluetoothConnection.getInstance();
        bluetoothConnection.setBluetoothDiscoveryCallback(device -> {
            btDevices.add(device);
            recyclerAdapter.setBtDevices(btDevices);
        });
        bluetoothConnection.startNewDiscovery(mContext, getView());
    }

    //======================== Fragment UI =========================

    private void initUI() {
        setConnectionStatus(stateKeeper.isConnected(), stateKeeper.getConnectedToWho());
        BaseConnection.ConnectionType type = stateKeeper.getConnectionType();
        //todo show which connection type
    }

    private void setConnectionStatus(boolean status,String who) {
        String text = "Status: ";
        int index = text.length();
        int color = status ? 0xFF4DB6AC: 0xFFE66868;
        if (status)
            text += "Connected";
         else
            text += "Disconnected";

        SpannableString spannableString = SpannableString.valueOf(text);
        spannableString.setSpan(new ForegroundColorSpan(color), index, text.length(), SpannableString.SPAN_COMPOSING);
        connection_status_txt.setText(spannableString);
        setConnectedTo(who);
    }

    private void setConnectedTo(String who) {
        String text = "Connected to: ";
        int index = text.length();
        text += who;

        SpannableString spannableString = SpannableString.valueOf(text);
        spannableString.setSpan(new ForegroundColorSpan(0xFF64B5F6), index, text.length(), SpannableString.SPAN_COMPOSING);
        device_name_txt.setText(spannableString);

    }

    void showDevicesRecycler(boolean show) {
        devices_layout.setVisibility(show ? View.VISIBLE :View.GONE);
    }

    void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    void hideProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    //========================== UI Events =========================

    @Override
    public void onBtDeviceClicked(BluetoothDevice bluetoothDevice) {

        String deviceName = bluetoothDevice.getName();
        showProgressDialog("Connecting to " + deviceName);

        bluetoothConnection.setConnectionResultCallback(success -> {
            if (success) {
                hideProgressDialog();
                Snackbar.make(getView(), "Connected to " + deviceName, Snackbar.LENGTH_LONG).show();
                setConnectionStatus(true, deviceName);
                showDevicesRecycler(false);
                stateKeeper.setStatus(BaseConnection.ConnectionType.BT,deviceName);
            } else {
                hideProgressDialog();
                Snackbar.make(getView(), "Oops! connection failed :(", Snackbar.LENGTH_LONG).show();
            }
        });
        bluetoothConnection.connectTo(bluetoothDevice);

    }

    @Override
    public void onUsbDeviceClicked(UsbDevice usbDevice) {

        //get an instance for UART connection
        UARTconnection uartConnection = UARTconnection.getInstance();
        // show config dialog before connecting
        UartConfigDialog dialog = new UartConfigDialog();
        dialog.setCancelable(false);
        dialog.addCallback(configs -> {
            uartConnection.connectTo(usbDevice,configs);
            Snackbar.make(getView(), "Connecting, please wait ...", Snackbar.LENGTH_SHORT).show();
            uartConnection.setConnectionResultCallback((success, details) -> {
                if (success) {
                    stateKeeper.setStatus(BaseConnection.ConnectionType.USB, usbDevice.getDeviceName());
                    setConnectionStatus(true, usbDevice.getDeviceName());
                    showDevicesRecycler(false);
                }
                Snackbar.make(getView(), details, Snackbar.LENGTH_LONG).show();
            });
        });
        dialog.show(getChildFragmentManager(),"uart_config");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       if(bluetoothConnection != null) bluetoothConnection.unregisterReceiver(mContext);
    }
}
