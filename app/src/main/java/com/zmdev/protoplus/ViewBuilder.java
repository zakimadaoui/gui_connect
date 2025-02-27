package com.zmdev.protoplus;

import android.content.Context;
import android.view.View;

import com.zmdev.protoplus.CustomViews.AccBlock;
import com.zmdev.protoplus.CustomViews.FluxBlock;
import com.zmdev.protoplus.CustomViews.GravityBlock;
import com.zmdev.protoplus.CustomViews.GyroBlock;
import com.zmdev.protoplus.CustomViews.OrientationBlock;
import com.zmdev.protoplus.CustomViews.ProtoColorPicker;
import com.zmdev.protoplus.CustomViews.ProtoJoystick;
import com.zmdev.protoplus.CustomViews.LightBlock;
import com.zmdev.protoplus.CustomViews.ProtoButton;
import com.zmdev.protoplus.CustomViews.ProtoFab;
import com.zmdev.protoplus.CustomViews.ProtoKeyPad;
import com.zmdev.protoplus.CustomViews.ProtoKnob;
import com.zmdev.protoplus.CustomViews.ProtoQuadJoy;
import com.zmdev.protoplus.CustomViews.ProtoSlider;
import com.zmdev.protoplus.CustomViews.ProtoSliderFloat;
import com.zmdev.protoplus.CustomViews.ProtoSwitch;
import com.zmdev.protoplus.CustomViews.ProtoTextEdit;
import com.zmdev.protoplus.CustomViews.ProtoTouchPad;
import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.CustomViews.ProximityBlock;
import com.zmdev.protoplus.CustomViews.TextDisplayBlock;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;


public class ViewBuilder {

    public static View generateViewFromAttrs(AttrsAndCommand specs, Context context) {

        switch (specs.getAttrs().getViewType()) {
            case ProtoView.Type.VIEW_TYPE_BUTTON:
                return new ProtoButton(context, specs);
            case ProtoView.Type.VIEW_TYPE_SLIDER:
                return new ProtoSlider(context, specs);
            case ProtoView.Type.VIEW_TYPE_SLIDER_FLOAT:
                return new ProtoSliderFloat(context, specs);
            case ProtoView.Type.VIEW_TYPE_FAB:
                return new ProtoFab(context, specs);
            case ProtoView.Type.VIEW_TYPE_JOYSTICK:
                return new ProtoJoystick(context, specs);
            case ProtoView.Type.VIEW_TYPE_KNOB:
                return new ProtoKnob(context, specs);
            case ProtoView.Type.VIEW_TYPE_GYRO:
                return new GyroBlock(context, specs);
            case ProtoView.Type.VIEW_TYPE_ORIENTATION:
                return new OrientationBlock(context, specs);
            case ProtoView.Type.VIEW_TYPE_KEYPAD:
                return new ProtoKeyPad(context, specs);
            case ProtoView.Type.VIEW_TYPE_SWITCH:
                return new ProtoSwitch(context, specs);
            case ProtoView.Type.VIEW_TYPE_TOUCHPAD:
                return new ProtoTouchPad(context, specs);
            case ProtoView.Type.VIEW_TYPE_QUAD_JOY:
                return new ProtoQuadJoy(context, specs);
            case ProtoView.Type.VIEW_TYPE_ACC:
                return new AccBlock(context, specs);
            case ProtoView.Type.VIEW_TYPE_GRAVITY:
                return new GravityBlock(context, specs);
            case ProtoView.Type.VIEW_TYPE_FLUX:
                return new FluxBlock(context, specs);
            case ProtoView.Type.VIEW_TYPE_LIGHT:
                return new LightBlock(context, specs);
            case ProtoView.Type.VIEW_TYPE_PROXIMITY:
                return new ProximityBlock(context, specs);
            case ProtoView.Type.VIEW_TYPE_TEXT_OUTPUT:
                return new TextDisplayBlock(context, specs);
            case ProtoView.Type.VIEW_TYPE_TEXT_BOX:
                return new ProtoTextEdit(context, specs);
            case ProtoView.Type.VIEW_TYPE_COLOR_PICKER:
                return new ProtoColorPicker(context, specs);
        }

        return null;
    }

    public static View generateViewFromAttrs(ProtoViewAttrs attrs, Context context) {
        AttrsAndCommand specs = new AttrsAndCommand();
        specs.setAttrs(attrs);
        return generateViewFromAttrs (specs, context);
    }
}
