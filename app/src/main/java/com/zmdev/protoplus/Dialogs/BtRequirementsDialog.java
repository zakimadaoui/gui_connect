package com.zmdev.protoplus.Dialogs;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import com.zmdev.protoplus.R;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.Task;
import com.zmdev.protoplus.Utils.ThemeUtils;

import static android.app.Activity.RESULT_OK;

public class BtRequirementsDialog extends DialogFragment {

    private static final String TAG = "BtRequirementsDialog";
    private BluetoothAdapter adapter;

    private boolean pairClicked = false;

    private Context mContext;
    private OnReqsFulfilledCallback callback;
    private boolean bt_enabled = false;
    private boolean gps_enabled = false;
    private boolean loc_permitted = false;
    private View scanBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //configure dialog look
        setStyle(STYLE_NORMAL, ThemeUtils.dialogThemeID);

        //init needed objects
        mContext = getContext();
        adapter = BluetoothAdapter.getDefaultAdapter();
        //init required settings booleans
        bt_enabled = adapter.isEnabled();
        checkGpsSetting();
        loc_permitted = checkLocalisationPermission();

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bt_requirements, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scanBtn = view.findViewById(R.id.scan_bt_btn);
        scanBtn.setOnClickListener(v -> {
            pairClicked = false;
            if (bt_enabled && loc_permitted && gps_enabled) {
                callback.onScanBtDevices();
                dismiss();
            } else if (bt_enabled && loc_permitted) {
                createLocationRequest();
            } else if (bt_enabled) {
                requestLocationPermission();
            } else {
                startBtPermissionsSequence();
            }

        });

        view.findViewById(R.id.bt_paired_btn).setOnClickListener(v -> {
            pairClicked = true;
            if (bt_enabled) {
                callback.onShowPairedDevices();
                dismiss();
            } else {
                startBtPermissionsSequence();
            }

        });
    }


    // -------------------------------------------- BT --------------------------------------------
    private void startBtPermissionsSequence(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        btResultLauncher.launch(enableBtIntent);
        //start intent and wait for the callback to continue the requirements sequence
    }

    ActivityResultLauncher<Intent> btResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                boolean resultOk = result.getResultCode() == RESULT_OK;
                if (resultOk) {
                    bt_enabled = true ;
                    if (pairClicked) {
                        callback.onShowPairedDevices();
                        dismiss();
                    } else {
                        requestLocationPermission();
                    }
                } else {
                    Toast.makeText(mContext, "Please enable BT to continue", Toast.LENGTH_SHORT).show();
                }
            });

    // --------------------------------- LOCATION PERMISSIONS  ------------------------------
    private void requestLocationPermission() {
        locPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean checkLocalisationPermission() {
        return ContextCompat.checkSelfPermission(
                mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private final ActivityResultLauncher<String> locPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    loc_permitted = true;
                    createLocationRequest();
                }
                else {
                    Toast.makeText(mContext, "Error: Location permission required !", Toast.LENGTH_SHORT).show();
                }
            });


    // ------------------------------------------- GPS --------------------------------------------
    private void checkGpsSetting() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);

        //Check the location settings and act upon the result
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());

        result.addOnSuccessListener(locationSettingsResponse -> {
            gps_enabled = true;
        });
    }

    protected void createLocationRequest() {
        //create a location request
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);


        //Check the location settings and act upon the result
        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());

        result.addOnSuccessListener(locationSettingsResponse -> {
            callback.onScanBtDevices();
            dismiss();
        });

        result.addOnFailureListener(e -> {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            ResolvableApiException resolvable = (ResolvableApiException) e;
            try {
                resolvable.startResolutionForResult(
                        getActivity(),
                        LocationRequest.PRIORITY_HIGH_ACCURACY);
            } catch (IntentSender.SendIntentException sendIntentException) {
                //ignore
            }
        });



    }

    //========================================================================
    public interface OnReqsFulfilledCallback {
        void onScanBtDevices();
        void onShowPairedDevices();
    }

    public void setOnFulfillCallback(OnReqsFulfilledCallback callback) {
        this.callback = callback;
    }
}

