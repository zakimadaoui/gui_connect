package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;

public class OrientationPref extends LinearLayout {

    private final Context mContext;
    private final ProtoView preview;
    private TextView orientation_txt;

    public OrientationPref(Context context, ProtoView preview) {
        super(context);
        mContext = context;
        this.preview = preview;
        init();
    }

    private void init() {
        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preference_orientation, this);

        //reference views
        orientation_txt = findViewById(R.id.select_widget_orientation_btn);
        //load prev orientation
        orientation_txt.setText(String.valueOf(preview.getAttrs().getOrientation()));


        setUpOrientationMenu();
    }

    private void setUpOrientationMenu() {

        String[] orientations = {"0°", "90°", "180°", "270°"};
        int[] angles = {0, 90, 180,270};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, orientations);
        ListPopupWindow listPopupWindow = new ListPopupWindow(mContext);
        listPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.card_bg));
        listPopupWindow.setAdapter(spinnerAdapter);

        orientation_txt.setOnClickListener(v -> {
            listPopupWindow.setAnchorView(v);
            listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
                preview.setOrientation(angles[position]);
                orientation_txt.setText(orientations[position]);
                listPopupWindow.dismiss();
            });
            listPopupWindow.show();
        });
    }
}
