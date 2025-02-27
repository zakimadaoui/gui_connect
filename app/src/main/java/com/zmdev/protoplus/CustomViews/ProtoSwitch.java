package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.TextPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

public class ProtoSwitch extends SwitchCompat implements ProtoView, CompoundButton.OnCheckedChangeListener {

    //basics
    private Context mContext;
    private Command mCommand;
    private ProtoViewAttrs mAttrs;
    private OnCommandExecutedListener listener;
    private boolean isPreview = false;
    private int varIndex;

    public ProtoSwitch(@NonNull Context context, AttrsAndCommand specs) {
        super(context);
        mContext = context;
        mAttrs = specs.getAttrs();
        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();

        try {
            Parameter[] parameters = mCommand.getParams();
            //store var params indices
            for (int i = 0; i < parameters.length ; i++) {
                if (parameters[i].getIsVariable() == 1) {
                    varIndex = i;
                    break;
                }
            }
        } catch (NullPointerException ignored) {}

        init();
    }

    private void init() {
        setOnCheckedChangeListener(this);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        setText(mAttrs.getText());
        setX(mAttrs.getX());
        setY(mAttrs.getY());
        if (mAttrs.getOrientation() == 180 )setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(Type.VIEW_TYPE_SWITCH);
        attrs.setPrimaryColor(Color.rgb(15, 122, 255));
        attrs.setPreviewTitle("Switch");
        return attrs;
    }


    @Override
    public void setPrimaryColor(int color) {
//        mAttrs.setPrimaryColor(color);
//        setThumbTintList(new ColorStateList());
//        setTrackTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void setSecondaryColor(int color) {

    }

    @Override
    public void setPrimaryText(String text) {
        mAttrs.setText(text);
        setText(text);
    }

    @Override
    public void setOrientation(int angle) {
        mAttrs.setOrientation(angle);
        if (angle == 180) setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        else setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
    }

    @Override
    public void setDrawable(int id) {

    }

    @Override
    public void setMinProgress(double min) {

    }

    @Override
    public void setMaxProgress(double max) {

    }

    @Override
    public void setShowPercentage(boolean show) {

    }

    @Override
    public int getVarParamsNbr() {
        return 1;
    }

    @Override
    public void setViewInMode(int mode) {
        isPreview = true;
        setClickable(false);
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"sw",null,
                new Parameter[]{new Parameter("0","switchVal", 1,DEF_COMMAND)
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd;
    }

    @Override
    public void setSize(int width, int height) {

    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[]{
                new TextPref(mContext,this,true,false, true),
        };
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isPreview || listener == null) return;
        mCommand.getParams()[varIndex].setDefValue(isChecked ? "1":"0");
        listener.onCommandExecuted(mCommand);

    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_switch_desc,
                R.string.doc_switch_reqs,
                R.string.doc_switch_usage
        };
    }

}
