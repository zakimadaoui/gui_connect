package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class ColorPref extends LinearLayout {

    private final Context   mContext;
    private final ProtoView preview;
    private FloatingActionButton    primary_fab;
    private FloatingActionButton    secondary_fab;
    private TextView                secondary_label;
    private TextView                primary_label;


    public ColorPref(Context context, ProtoView preview, boolean hasSecondary, FragmentManager fm
            , String primary_text, String secondary_text) {
        super(context);
        mContext = context;
        this.preview = preview;
        init(hasSecondary,fm, primary_text, secondary_text);
    }

    private void init(boolean hasSecondary, FragmentManager fm,
                      String primary_text, String secondary_text) {
        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preference_color, this);

        //get references to views
        primary_fab     = findViewById(R.id.primary_color_fab);
        secondary_fab   = findViewById(R.id.secondary_color_fab);
        primary_label = findViewById(R.id.primary_color_label_text);
        secondary_label = findViewById(R.id.secondary_color_label_text);

        //labels
        if (primary_text != null) primary_label.setText(primary_text);
        if (secondary_text != null) secondary_label.setText(secondary_text);

        //hide/disable secondary
        if (hasSecondary) {
            secondary_fab.setOnClickListener(v -> {
                ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setAllowPresets(true)
                        .setDialogId(1)
                        .setColor(Color.BLACK)
                        .setShowAlphaSlider(true)
                        .create();
                dialog.setColorPickerDialogListener(pickerListener);
                dialog.show(fm, "colorPickerDialog2");
            });
        } else {
            secondary_fab.setVisibility(INVISIBLE);
            secondary_label.setVisibility(INVISIBLE);
        }

        if (preview.getAttrs().getPrimaryColor() != 0) primary_fab.setBackgroundTintList(ColorStateList.valueOf(preview.getAttrs().getPrimaryColor()));
        if (preview.getAttrs().getSecondaryColor() != 0) secondary_fab.setBackgroundTintList(ColorStateList.valueOf(preview.getAttrs().getSecondaryColor()));

        primary_fab.setOnClickListener(v -> {
            ColorPickerDialog dialog = ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowPresets(true)
                    .setDialogId(0)
                    .setColor(Color.BLACK)
                    .setShowAlphaSlider(true)
                    .create();
            dialog.setColorPickerDialogListener(pickerListener);
            dialog.show(fm, "colorPickerDialog1");
        });

    }


    private final ColorPickerDialogListener pickerListener = new ColorPickerDialogListener() {
        @Override
        public void onColorSelected(int dialogId, int color) {
            if (dialogId == 0) {
                preview.setPrimaryColor(color);
                primary_fab.setBackgroundTintList(ColorStateList.valueOf(color));
            } else {
                preview.setSecondaryColor(color);
                secondary_fab.setBackgroundTintList(ColorStateList.valueOf(color));
            }
        }

        @Override
        public void onDialogDismissed(int dialogId) {

        }
    };
}
