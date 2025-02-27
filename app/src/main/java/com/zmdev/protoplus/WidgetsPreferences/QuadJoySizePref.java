package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;

public class QuadJoySizePref extends LinearLayout {

    private final Context   mContext;
    private final ProtoView preview;


    public QuadJoySizePref(Context context, ProtoView preview) {
        super(context);
        mContext = context;
        this.preview = preview;

        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preference_quad_joy_size, this);

        RadioGroup radioGroup = findViewById(R.id.quad_joy_radiogroup);
        RadioButton small = findViewById(R.id.joy_small_radio);
        RadioButton medium = findViewById(R.id.joy_medium_radio);

        //load prev size
        if (preview.getAttrs().getWidth() == 0)  small.setChecked(true);
        else medium.setChecked(true);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == small.getId()) preview.setSize(0, 0);
            else preview.setSize(1, 0);
        });

    }


}
