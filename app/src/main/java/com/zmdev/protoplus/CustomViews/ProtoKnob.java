package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.ThemeUtils;
import com.zmdev.protoplus.WidgetsPreferences.ColorPref;
import com.zmdev.protoplus.WidgetsPreferences.OrientationPref;
import com.zmdev.protoplus.WidgetsPreferences.RangePref;
import com.zmdev.protoplus.WidgetsPreferences.SizePref;
import com.zmdev.protoplus.WidgetsPreferences.TextPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_KNOB;

public class ProtoKnob extends Croller implements ProtoView {

    private static final String TAG = "ProtoKnob";
    private Parameter[] parameters;
    private int varIndex;
    ProtoViewAttrs mAttrs;
    Command mCommand;
    public static int DEFAULT_COLOR = 0xFF2196F3;
    private boolean isPreview = false;
    private boolean noTouch = false;
    private OnCommandExecutedListener callback;
    private boolean firstMeasure = true;
    private int max_width = 200;
    private int min_width = 200;
    private Rect textBounds = new Rect();

    public ProtoKnob(Context context,AttrsAndCommand specs) {
        super(context);
        mAttrs = specs.getAttrs();
        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();

        try {
            parameters = mCommand.getParams();
            //store var params indices
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getIsVariable() == 1) {
                    varIndex = i;
                    break;
                }
            }
        } catch (NullPointerException ignored) {}

        init();
    }

    private void init() {

        //position
        setX(mAttrs.getX());
        setY(mAttrs.getY());
        //color
        int color = mAttrs.getPrimaryColor();
        if (color == 0)  color = DEFAULT_COLOR;
        setPrimaryColor(color);
        //text
        String label = mAttrs.getText();
        if (label != null) setLabel(label);

        //range
        setMaxProgress(mAttrs.getMaxProgress()); //this resets the progress as well,
        setMinProgress(mAttrs.getMinProgress());
        //orientation
        setOrientation(mAttrs.getOrientation());
        //special view attrs
        setIsContinuous(mAttrs.getMaxProgress() >=100);
        setIndicatorWidth(10);
        setBackCircleColor(0xFFEDEDED);
        setProgressSecondaryColor(0xFFEEEEEE);
        setMainCircleColor(Color.WHITE);
        setLabelColor(Color.GRAY);
        setMainCircleColor(ThemeUtils.backgroundColor);
        setBackCircleColor(ThemeUtils.backgroundColorVariant);
        setProgressSecondaryColor(ThemeUtils.backgroundColorVariant);
//        setcolor(ThemeUtils.backgroundColorVariant);

        if (mAttrs.getStateData() != null && !mAttrs.getStateData().isEmpty()) {
            progress = Integer.parseInt(mAttrs.getStateData());
            setProgress(progress);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (firstMeasure) {
            max_width = MeasureSpec.getSize(widthMeasureSpec);
            firstMeasure = false;
        }

        //correct size for label
        textPaint.getTextBounds("proto+",0,5, textBounds);
        int textHeight = textBounds.height();
        int width = getRealWidth();
        int height = width + textHeight;

//        if (orientation == 0 || orientation == 180) {
        width  = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        height = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//        } else {
//            width  = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
//            height = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
//        }
        super.onMeasure(width, height);

    }

    //============================== Base view implementation =======================

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    @Override
    public void setPrimaryColor(int color) {
        mAttrs.setPrimaryColor(color);
        setIndicatorColor(color);
        setProgressPrimaryColor(color);
    }

    @Override
    public void setSecondaryColor(int color) {
    }

    @Override
    public void setPrimaryText(String text) {
        mAttrs.setText(text);
        setLabel(text);
    }

    @Override
    public void setOrientation(int angle) {
        mAttrs.setOrientation(angle);
        setRotation(angle);
    }

    @Override
    public void setDrawable(int id) {

    }

    @Override
    public void setMinProgress(double min) {
        mAttrs.setMinProgress(min);
        setMin((int) min);
    }

    @Override
    public void setMaxProgress(double max) {
        max = (max < 2) ? 2: max; //constrain to more than 2
        mAttrs.setMaxProgress(max);
        setMax((int) max);
        setIsContinuous(max > 70);
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
        noTouch = (mode == MODE_NO_TOUCH);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (noTouch) return false;
        return super.onTouchEvent(e);
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"knob",null,
                new Parameter[]{new Parameter("0","progress", 1,DEF_COMMAND)
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd;
    }

    @Override
    public void setSize(int width, int height) {
        mAttrs.setWidth(width); //save as percentage for having a normalized value
                                //then use getRealWidth() for width in pixels

        getLayoutParams().width = getRealWidth();
        getLayoutParams().height = getRealWidth();

        requestLayout(); //this triggers the onMeasure function
        setProgressRadius(-1);
        setBackCircleRadius(-1);
        setMainCircleRadius(-1);
    }

    @Override
    public void setSizeInPixels(int newWidth, int newHeight) {
        setSize((newWidth*100)/max_width,0);
    }

    @Override
    public SizeMode getSizeMode() {
        return SizeMode.BOTH_ONLY;
    }

    private int getRealWidth() {
        int w = (int) (mAttrs.getWidth() * max_width / 100.0);
        return Math.max(min_width,w);
    }



    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        Context c = getContext();
        return new View[]{
                new TextPref(c, this, true, false, false),
                new RangePref(c, this,false, 13),
                new SizePref(c, this, true,false),
                new OrientationPref(c, this),
                new ColorPref(c, this, false,fm,null,null),
        };
    }


    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        callback = listener;
    }

    @Override
    public void onProgressChanged(int progress) {
        super.onProgressChanged(progress);
        if (isPreview || mCommand == null || callback == null) return;

        parameters[varIndex].setDefValue(String.valueOf(progress));
        mCommand.setParams(parameters);
        callback.onCommandExecuted(mCommand);
        if (mAttrs.getMaxProgress() < 30){
            performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            );
        }
    }

    @Override
    public ProtoViewAttrs saveAndGetCurrentState() {
        mAttrs.setStateData(String.valueOf(progress));
        return mAttrs;
    }

    @Override
    public boolean needStateSave() {
        return (mAttrs.getStateData() != null);
    }


    //=====================================================
    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(VIEW_TYPE_KNOB);
        attrs.setPreviewTitle("Knob");
        attrs.setWidth(100);
        attrs.setOrientation(0);
        attrs.setMinProgress(0);
        attrs.setMaxProgress(35);
        return attrs;
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_knob_desc,
                R.string.doc_knob_reqs,
                R.string.doc_knob_usage
        };
    }


}


