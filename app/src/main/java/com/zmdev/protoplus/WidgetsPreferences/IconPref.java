package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;

public class IconPref extends LinearLayout {

    private final Context mContext;
    private final ProtoView preview;
    private IconDialog dialog;

    public IconPref(Context context, ProtoView preview, FragmentManager fm) {
        super(context);
        mContext = context;
        this.preview = preview;
        dialog = createIconDialog(fm);
        init(fm);
    }

    private void init(FragmentManager fm) {
        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preference_icon, this);

        findViewById(R.id.choose_icon_btn).setOnClickListener(v -> {
            dialog.show(fm, "icon-dialog");
        });
    }

    private IconDialog createIconDialog(FragmentManager fm) {
        IconDialog dialog = (IconDialog) fm.findFragmentByTag("icon_dialog");
        return (dialog != null) ? dialog
                : IconDialog.newInstance(new IconDialogSettings.Builder().build());
    }

}
