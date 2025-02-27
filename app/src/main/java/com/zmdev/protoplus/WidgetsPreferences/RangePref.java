package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

public class RangePref extends LinearLayout {

    private final Context mContext;
    private final ProtoView preview;
    private double ABS_MIN = 0;

    public RangePref(Context context, ProtoView preview, boolean hasMin, double MIN) {
        super(context);
        mContext = context;
        this.preview = preview;
        ABS_MIN = MIN;
        init(hasMin);
    }

    private void init(boolean hasMinRange) {
        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preference_range, this);

        EditText min_edit = findViewById(R.id.widget_min_range_edit);
        EditText max_edit = findViewById(R.id.widget_max_range_edit);
        CheckBox last_value_box = findViewById(R.id.last_value_box);

        ProtoViewAttrs attrs = preview.getAttrs();

        min_edit.setText(String.valueOf((int)attrs.getMinProgress()));
        max_edit.setText(String.valueOf((int)attrs.getMaxProgress()));
        last_value_box.setChecked(attrs.getStateData() != null);
        last_value_box.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attrs.setStateData("0");
            } else {
                attrs.setStateData(null);

            }
        });

        max_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double d = Double.parseDouble(s.toString());
                    if (d < ABS_MIN) {
                        d = ABS_MIN;
                        max_edit.setError("can't be less than " + ABS_MIN);
                    }else if (d > 9999999999L) {
                        d = 9999999999L;
                        max_edit.setError("can't be more than 9999999999");
                    }
                    else {
                        max_edit.setError(null);
                    }

//                    max_edit.setText(String.valueOf(ABS_MIN));
                    preview.setMaxProgress(d);
                }catch (Exception ignored){}
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (!hasMinRange || Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            min_edit.setEnabled(false);
        else
            min_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        preview.setMinProgress(Double.parseDouble(s.toString()));
                    }catch (Exception ignored){}
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
    }
}