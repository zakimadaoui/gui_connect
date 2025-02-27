package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.jaredrummler.android.colorpicker.ColorPickerView;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.SizePref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_COLOR_PICKER;

public class ProtoColorPicker extends ColorPickerView implements ProtoView , ColorPickerView.OnColorChangedListener {

    //------------------ basics -------------------
    private Command mCommand;
    private Parameter[] parameters;
    private int varStartIndex = 0; //if needed
    private ProtoViewAttrs mAttrs;
    private OnCommandExecutedListener listener;
    private boolean firstMeasure = true;
    private int max_width;
    private float size_ratio;
    private float pWidth;

    //---------------------------------------------
    public ProtoColorPicker(@NonNull Context context, AttrsAndCommand specs) {
        super(context);
        mAttrs = specs.getAttrs();
        mCommand = specs.getCommand();

        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();

        try {
            parameters = mCommand.getParams();
            varStartIndex = (mCommand.getParamsNbr() > getVarParamsNbr()) ? 1 : 0;
        } catch (NullPointerException ignored) {
            //this block will fail when a widget is added from the catalog
            //since no real command object will be passed as the attrs have not been
            //added yet to the DB (until user saves in editor)
        }

        init();
    }

    private void init() {
        setX(mAttrs.getX());
        setY(mAttrs.getY());

        pWidth = mAttrs.getWidth() /100.0f; //width percentage

        //set size and the listener
        setOnColorChangedListener(this);
//        setLayoutParams(new RelativeLayout.LayoutParams(400, 400));
        // setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onColorChanged(int newColor) {
         parameters[varStartIndex].setDefValue(String.valueOf(newColor));
         mCommand.setParams(parameters);
         listener.onCommandExecuted(mCommand);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (firstMeasure) {
            max_width = MeasureSpec.getSize(widthMeasureSpec);
            size_ratio = 100.0f/max_width;
            firstMeasure = false;
        }

        int newWidth  = MeasureSpec.makeMeasureSpec((int) (pWidth * max_width), MeasureSpec.EXACTLY);
        int newHeight = MeasureSpec.makeMeasureSpec((int) (pWidth * max_width), MeasureSpec.EXACTLY);
        super.onMeasure(newWidth, newHeight);
    }

    @Override
    public void setSize(int width, int height) {
        if (width < 30) width = 30;
        mAttrs.setWidth(width);
        pWidth = width / 100.0f;
        requestLayout();
    }

    @Override
    public void setSizeInPixels(int newWidth, int newHeight) {
        if (newWidth < 0.3 * max_width) newWidth = (int) (0.3 * max_width);
        mAttrs.setWidth((int) (newWidth * size_ratio));
        pWidth = (float) newWidth / max_width;
        requestLayout();
    }

    @Override
    public SizeMode getSizeMode() {
        return SizeMode.BOTH_ONLY;
    }


    @Override
    public void setViewInMode(int mode) {
        //implement and add flags for touch if needed
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    @Override
    public void setOrientation(int angle) {
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getVarParamsNbr() {
        return 1;
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"rgb",null,
                new Parameter[]{
                        new Parameter("","r", 1,DEF_COMMAND),
                        new Parameter("","g", 1,DEF_COMMAND),
                        new Parameter("","b", 1,DEF_COMMAND),
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd ;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(VIEW_TYPE_COLOR_PICKER);
        attrs.setPreviewTitle("Color Picker");
        attrs.setLinkedCommandID(DEF_COMMAND);
        attrs.setWidth(70);
        attrs.setProWidget(true);
        return attrs;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[]{
                new SizePref(getContext(), this , true, false)
        };
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_color_picker_desc,
                R.string.doc_color_picker_reqs,
                R.string.doc_color_picker_usage
        };
    }

}
