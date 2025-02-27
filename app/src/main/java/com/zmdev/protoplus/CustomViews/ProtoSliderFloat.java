package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

public class ProtoSliderFloat extends ProtoSlider {

    private double progressDouble = 0;

    public ProtoSliderFloat(@NonNull @NotNull Context context, AttrsAndCommand specs) {
        super(context, specs);
    }

    @Override
    protected void init() {
        super.init();
        //load previous data if possible
        if (mAttrs.getStateData() != null && !mAttrs.getStateData().isEmpty()) {
            progressDouble = 0.001 * Integer.parseInt(mAttrs.getStateData());
            progressString = String.format("%.3f", progressDouble);
            realProgress = 1.0 * (progressDouble - min) / diff;
            sliderX = (int) (realProgress * (sliderEndX - sliderStartX) + sliderStartX); //adjust thumb

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (noTouch) return false;

        float posX;
        if (orientation == 0) {
            posX = event.getX();
        } else if (orientation == 90) {
            posX = event.getY();
        }else if (orientation == 180) {
            posX = getWidth() - event.getX();
        }else { //270°
            posX = getHeight() - event.getY();
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                posX = Math.min(Math.max(sliderStartX, posX), sliderEndX);
                sliderX = (int) posX;
                realProgress = ((posX - sliderStartX) * 1.0) / (sliderEndX - sliderStartX);
                double tempProgress =
                        realProgress*diff + min;
                if (tempProgress != progressDouble) {
                    progressDouble = tempProgress;
                    progressChanged();
                }
                invalidate();
                break;
        }

        return true;
    }

    @Override
    protected void progressChanged() {
        if (listener == null) return;
        progressString = String.format("%.3f", progressDouble);
        parameters[varIndex].setDefValue(progressString);
        mCommand.setParams(parameters);
        listener.onCommandExecuted(mCommand);
    }

    @Override
    public ProtoViewAttrs saveAndGetCurrentState() {
        mAttrs.setStateData(String.valueOf((int)(progressDouble*1000.0)));
        return mAttrs;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(Type.VIEW_TYPE_SLIDER_FLOAT);
        attrs.setPrimaryColor(Color.rgb(255, 213, 0));
        attrs.setPreviewTitle("Slider (float)");
        attrs.setMaxProgress(100);
        attrs.setWidth(100);
        return attrs;
    }
}
