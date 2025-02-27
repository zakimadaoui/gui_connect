package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.zmdev.protoplus.CustomViews.ProtoView;

public class TemplatePref extends LinearLayout {

    private final Context mContext;
    private final ProtoView preview;

    public TemplatePref(Context context, ProtoView preview) {
        super(context);
        mContext = context;
        this.preview = preview;
        init();
    }

    private void init() {
        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.preference_item, this);
    }
}
