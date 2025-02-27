package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;

public class SensorFrequencyPref extends LinearLayout {

    private final Context mContext;
    private final ProtoView preview;

    public SensorFrequencyPref(Context context, ProtoView preview) {
        super(context);
        mContext = context;
        this.preview = preview;
        init();
    }

    private void init() {
        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preference_sensor_frequency, this);

        EditText edit = findViewById(R.id.widget_sampling_rate_edit);

        if (preview.getAttrs().getMaxProgress() != 0) {
            edit.setText(String.valueOf((int) preview.getAttrs().getMaxProgress()));
        }

        edit.addTextChangedListener(new TextWatcher() {
            String before ="";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int rate;
                try {
                    rate = Integer.parseInt(s.toString());
                    /* Im using the MAX progress variable to hold the sampling rate,
                     to avoid adding more code and newer variables to the database */
                    preview.setMaxProgress(rate);
                } catch (NumberFormatException e) {
                    Toast.makeText(mContext, "Only decimal numbers allowed !",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
