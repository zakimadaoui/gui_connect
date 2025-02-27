package com.zmdev.protoplus.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.ThemeUtils;

import org.jetbrains.annotations.NotNull;

public class TcpDialog extends DialogFragment {

    private final Callback callback;

    public TcpDialog(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, ThemeUtils.dialogThemeID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_tcp,container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText ip_edit = view.findViewById(R.id.ip_address_edit);
        EditText port_edit = view.findViewById(R.id.port_nbr_edit);

        view.findViewById(R.id.conect_tcp_btn).setOnClickListener(v -> {
            String ip = ip_edit.getText().toString();
            String port = port_edit.getText().toString();

            if (ip.isEmpty()) {
                ip_edit.setError("Oops, you forgot this !");
            } else if (port.isEmpty()) {
                port_edit.setError("Oops, you forgot this !");
            } else {
                try {
                    callback.onFinish(ip, Integer.parseInt(port));
                    dismiss();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface Callback {
        void onFinish(String ip, int port);
    }
}
