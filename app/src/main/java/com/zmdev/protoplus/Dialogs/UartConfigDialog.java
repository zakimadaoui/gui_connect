package com.zmdev.protoplus.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Connections.UartConfiguration;
import com.zmdev.protoplus.Utils.ThemeUtils;

import org.jetbrains.annotations.NotNull;

public class UartConfigDialog extends DialogFragment {

    private Context mContext;
    private OnUartConfigConfirmedCallback callback;

    private enum Config {DATA_BITS, STOP_BITS, FLOW_CONTROL, PARITY}

    private int baudRate = 9600;

    private final String[] flowControlOptions = {"OFF","RTS_CTS","DSR_DTR", "XON_XOFF"};
    private final int[] flowControlValues ={0,1,2,3};
    private int flowControlValue = 0;

    private final String[] parityOptions = {"NONE","EVEN","ODD", "MARK", "SPACE"};
    private final int[] parityValues ={0,1,2,3,4};
    private int parityValue = 0;

    private final String[] stopBitOptions = {"1","1.5","2"};
    private final int[] stopBitValues ={1,3,2};
    private int stopBitValue = 1;

    private final String[] dataBitsOptions = {"5","6","7","8"};
    private final int[] dataBitsValues ={5,6,7,8};
    private int dataBitsValue = 8;

    ListPopupWindow listPopupWindow;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        setStyle(STYLE_NORMAL, ThemeUtils.dialogThemeID);
        listPopupWindow = new ListPopupWindow(mContext);
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.card_bg));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_uart_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText baud_edit = view.findViewById(R.id.uart_baudrate_edit);
        TextView parity_edit = view.findViewById(R.id.uart_parity_edit);
        TextView stop_bits_edit = view.findViewById(R.id.uart_stop_bits_edit);
        TextView data_bits_edit = view.findViewById(R.id.uart_data_bits_edit);
        TextView flow_control_edit = view.findViewById(R.id.uart_flow_control_edit);

        parity_edit.setOnClickListener(v ->{
            showMenuForView(parity_edit, parityOptions, parityValues, Config.PARITY);
        });
        stop_bits_edit.setOnClickListener(v ->{
            showMenuForView(stop_bits_edit, stopBitOptions, stopBitValues, Config.STOP_BITS);
        });
        data_bits_edit.setOnClickListener(v ->{
            showMenuForView(data_bits_edit,dataBitsOptions , dataBitsValues, Config.DATA_BITS);
        });
        flow_control_edit.setOnClickListener(v ->{
            showMenuForView(flow_control_edit, flowControlOptions, flowControlValues, Config.FLOW_CONTROL);
        });

        view.findViewById(R.id.uart_config_confirm).setOnClickListener(v -> {
            String baudText = baud_edit.getText().toString();

            try {
                baudRate = Integer.parseInt(baudText);
            } catch (Exception ignored) {}

            callback.OnConfirm(new UartConfiguration(baudRate, dataBitsValue,parityValue,stopBitValue,flowControlValue));
            dismiss();
        });

        view.findViewById(R.id.uart_config_close).setOnClickListener(v -> dismiss());
    }

    private void showMenuForView(TextView v, String[] options, int[] values, Config config) {

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, options);
        listPopupWindow.setAdapter(spinnerAdapter);
        listPopupWindow.setAnchorView(v);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            switch (config) {
                case PARITY:
                    parityValue = values[position];
                    break;
                case DATA_BITS:
                    dataBitsValue = values[position];
                    break;
                case STOP_BITS:
                    stopBitValue = values[position];
                    break;
                case FLOW_CONTROL:
                    flowControlValue = values[position];
                    break;
            }
            v.setText(options[position]);
            listPopupWindow.dismiss();
        });
        listPopupWindow.show();
    }

    public interface OnUartConfigConfirmedCallback {
        void OnConfirm(UartConfiguration configs);
    }

    public void addCallback(OnUartConfigConfirmedCallback callback) {
        this.callback = callback;
    }

}
