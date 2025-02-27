package com.zmdev.protoplus.Adapters;

import android.bluetooth.BluetoothDevice;
import android.hardware.usb.UsbDevice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zmdev.protoplus.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "DeviceAdapter";
    List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    List<UsbDevice> usbDevices = new ArrayList<>();
    private final OnDeviceClickedListener listener;
    DeviceType deviceType =  DeviceType.BT_DEVICE;

    enum DeviceType{BT_DEVICE, USB_DEVICE,TCP_DEVICE}


    public DeviceAdapter(OnDeviceClickedListener listener) {
        this.listener = listener;
    }

    public void setBtDevices(List<BluetoothDevice> devices) {
        deviceType = DeviceType.BT_DEVICE;
        this.bluetoothDevices = devices;
        notifyDataSetChanged();
    }

    public void setUsbDevices(List<UsbDevice> usbDevices) {
        deviceType = DeviceType.USB_DEVICE;
        this.usbDevices = usbDevices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            //BT
            case 1: return new BtDeviceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble_device,parent, false));
            //USB
            case 2: return new UsbDeviceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usb_device, parent, false));
            //TCP
            case 3: return  null;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (deviceType == DeviceType.BT_DEVICE) {

            BtDeviceHolder holder = (BtDeviceHolder) viewHolder;
            BluetoothDevice device = bluetoothDevices.get(i);
            holder.ble_name.setText(device.getName());
            holder.ble_address.setText(device.getAddress());
        }

        if (deviceType == DeviceType.USB_DEVICE) {

            UsbDeviceHolder holder = (UsbDeviceHolder) viewHolder;
            UsbDevice device = usbDevices.get(i);
            holder.usb_name.setText(device.getDeviceName());
            holder.manufacturer_name.setText(device.getManufacturerName());
        }
    }

    @Override
    public int getItemCount() {
        switch (deviceType) {
            case BT_DEVICE: return bluetoothDevices.size();
            case USB_DEVICE: return usbDevices.size();
//            case TCP_DEVICE: return usbDevices.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        switch (deviceType) {
            case BT_DEVICE: return 1;
            case USB_DEVICE: return 2;
            case TCP_DEVICE: return 3;
            default: return 0;
        }
    }


//=============================== ViewHolders ===============================
    public class BtDeviceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView ble_name;
        TextView ble_address;
        public BtDeviceHolder(@NonNull View itemView) {
            super(itemView);
            ble_name = itemView.findViewById(R.id.ble_name);
            ble_address = itemView.findViewById(R.id.ble_address);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
           if (listener != null) listener.onBtDeviceClicked(bluetoothDevices.get(getAdapterPosition()));
        }
    }

    public class UsbDeviceHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView usb_name;
        TextView manufacturer_name;

        public UsbDeviceHolder(@NonNull View itemView) {
            super(itemView);
            usb_name = itemView.findViewById(R.id.usb_name);
            manufacturer_name = itemView.findViewById(R.id.usb_manufacturer_name);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            listener.onUsbDeviceClicked(usbDevices.get(getAdapterPosition()));
        }
    }

    public interface OnDeviceClickedListener {
        void onBtDeviceClicked(BluetoothDevice bluetoothDevice);
        void onUsbDeviceClicked(UsbDevice usbDevice);
    }


}
