package com.zmdev.protoplus.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.LayoutRes;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.ThemeUtils;

public class ProtoDialog extends AlertDialog {

    public ProtoDialog(Context context) {
        super(context, ThemeUtils.dialogThemeID);
    }

    public ProtoDialog(Context context, String title,String message) {
        super(context, ThemeUtils.dialogThemeID);
        setMessage(message);
        setTitle(title);
    }

    public ProtoDialog(Context context, String title, @LayoutRes int id) {
        super(context, ThemeUtils.dialogThemeID);
        setView(LayoutInflater.from(context).inflate(id,null));
        setTitle(title);
    }

    public void addNegativeButton(String name) {
        setButton(BUTTON_NEGATIVE, name, (dialog, which) -> {});
    }

    public void addNegativeButton(String name, OnClickListener listener) {
        setButton(BUTTON_NEGATIVE, name, listener);
    }

    public void addPositiveButton(String name, OnClickListener listener) {
        setButton(BUTTON_POSITIVE, name, listener);
    }


}
