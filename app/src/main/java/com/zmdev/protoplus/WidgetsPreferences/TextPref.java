package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zmdev.protoplus.CustomViews.ProtoSwitch;
import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;

public class TextPref extends LinearLayout {

    private final Context mContext;
    private final ProtoView preview;
    private CheckBox percentage_box;
    private EditText label_edit;

    public TextPref(Context context, ProtoView preview, boolean hasLabelEdit, boolean hasPercentage, boolean showRtlBox) {
        super(context);
        mContext = context;
        this.preview = preview;
        init(hasLabelEdit,hasPercentage,showRtlBox);
    }


    private void init(boolean hasLabelEdit, boolean hasPercentage, boolean showRtlBox) {
        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preference_text, this);

        //reference views
        label_edit = findViewById(R.id.widget_label_textview);
        percentage_box = findViewById(R.id.show_percentage_box);
        CheckBox show_rtl_box = findViewById(R.id.show_rtl_box);

        if (hasLabelEdit) {
            label_edit.setText(preview.getAttrs().getText());
            label_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    preview.setPrimaryText(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });
        }
        else{ label_edit.setVisibility(GONE);}


        if (hasPercentage) {
            percentage_box.setChecked(preview.getAttrs().isShowProgress());
            percentage_box.setOnCheckedChangeListener((buttonView, isChecked) -> {
                preview.setShowPercentage(isChecked);
            });
        } else {
            percentage_box.setVisibility(GONE);
        }

        if (showRtlBox) {
            show_rtl_box.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (preview instanceof ProtoSwitch) preview.setOrientation(isChecked ? 180 : 0);
            });
            if (preview instanceof ProtoSwitch) {
                show_rtl_box.setChecked(preview.getAttrs().getOrientation() == 180);
            }
        } else {
            show_rtl_box.setVisibility(GONE);
        }

    }

    public TextPref setPercentageBoxText(String text) {
        this.percentage_box.setText(text);
        return this;
    }

    public TextPref setLabelHint(String hint) {
        label_edit.setHint(hint);
        return this;
    }
}

