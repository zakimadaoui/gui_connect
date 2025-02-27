package com.zmdev.protoplus.WidgetsPreferences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import static com.zmdev.protoplus.CustomViews.ProtoView.SIZE_WRAP;

public class SizePref extends LinearLayout {

    private final Context   mContext;
    private final ProtoView preview;
    private CheckBox        customSizeBox;
    private CheckBox        wrapWidthBox;
    private SeekBar         sizeSeekBar;

    public SizePref(Context context, ProtoView preview, boolean isCustomizable, boolean hasWrap) {
        super(context);
        mContext = context;
        this.preview = preview;
        init(isCustomizable, hasWrap);
    }

    private void init(boolean isCustomizable, boolean hasWrap) {
        //inflate the view with the layout
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preference_size, this);

        //get references to views
        sizeSeekBar     = findViewById(R.id.custom_size_seek);
        customSizeBox   = findViewById(R.id.custom_size_box);
        wrapWidthBox    = findViewById(R.id.wrap_content_box);

        //set seekbar on max size
        sizeSeekBar.setProgress(100);

        //disable unwanted views
        if (!isCustomizable) {
            sizeSeekBar.setVisibility(GONE);
            customSizeBox.setVisibility(GONE);
        }
        if (!hasWrap) wrapWidthBox.setVisibility(GONE);

        //decide which box to check
        sizeSeekBar.setEnabled(false);
        ProtoViewAttrs attrs = preview.getAttrs();
        if (attrs.getWidth() == SIZE_WRAP) wrapWidthBox.setChecked(true);
        else if (isCustomizable) {
            sizeSeekBar.setEnabled(true);
            sizeSeekBar.setProgress(preview.getAttrs().getWidth());
            customSizeBox.setChecked(true);
            customSizeBox.setText(String.valueOf(preview.getAttrs().getWidth()).concat(" %"));
        }
        setUpSizeBox();
    }

    private void setUpSizeBox() {

        //adjust max width limit
        sizeSeekBar.setMax(100);

        //fake checkChangeListener for checkboxes
        View.OnClickListener checkedChangeListener = (view) -> {
            //prevent unchecking the choice since this is a check box and not a radio !
            if (!((CheckBox) view).isChecked()) {
                ((CheckBox) view).setChecked(true);
                return;
            }

            if (view.getId() == R.id.wrap_content_box) {
                preview.setSize(SIZE_WRAP, SIZE_WRAP);
                customSizeBox.setChecked(false);
                sizeSeekBar.setEnabled(false);
            } else {// custom
                sizeSeekBar.setProgress(preview.getAttrs().getWidth());
                preview.setSize(sizeSeekBar.getProgress(),0);
                wrapWidthBox.setChecked(false);
                sizeSeekBar.setEnabled(true);
            }
        };

        //set the listener
        wrapWidthBox.setOnClickListener(checkedChangeListener);
        customSizeBox.setOnClickListener(checkedChangeListener);

        //listen to seekBar changes
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    preview.setSize(progress, 0);
                    customSizeBox.setText(String.valueOf(progress).concat(" %"));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
}
