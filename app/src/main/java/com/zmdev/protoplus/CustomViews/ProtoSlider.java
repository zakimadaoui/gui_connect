package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.App;
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

public class ProtoSlider extends View implements ProtoView {

    //basics
    protected Command mCommand;
    protected ProtoViewAttrs mAttrs;
    protected Parameter[] parameters;
    protected OnCommandExecutedListener listener;
    protected int varIndex;

    protected int progress = 0;
    protected double realProgress = 0.0;
    protected int orientation = 0;
    protected int min;
    protected int max;
    protected int diff;
    protected String progressString = "0";

    protected int width;
    protected int max_width;
    protected int min_width;
    protected int wrap_height;
    protected int thumbRadius;
    protected int centerY;
    protected int sliderStartX;
    protected int sliderEndX;
    protected int sliderX;
    protected int offsetX;
    protected int paddingSides;
    protected int paddingTB;//top / bottom
    protected Paint paintSliderBackground;
    protected Paint paintSliderProgress;
    protected Paint paintThumb;
    protected boolean firstMeasure = true;

    protected Paint paintProgressText;
    protected float textOffsetedCenterY;
    protected int textHeight;
    protected boolean showProgress = false;

    protected static final String TAG = "ProtoSlider";
    protected String label = "";
    protected boolean noTouch = false;

    public ProtoSlider(@NonNull Context context, AttrsAndCommand specs) {
        super(context);

        mAttrs = specs.getAttrs();
        mCommand = specs.getCommand();

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
        } catch (NullPointerException ignored) {
        }

        init();
    }

    protected void init() {

        thumbRadius = (int) (10 * App.density_px);
        int sliderThickness = (int) (3 * App.density_px);
        paddingTB = (int) (8 * App.density_px);
        paddingSides = (int) (8 * App.density_px);
        float textSize = (float) (14 * App.density_sp);


        sliderStartX = offsetX = paddingSides + thumbRadius;
        min_width = 2 * paddingSides + 2 * thumbRadius + 77;
        wrap_height = 2 * (paddingTB + thumbRadius);
        centerY = paddingTB + thumbRadius;
        sliderX = sliderStartX;

        min = (int) mAttrs.getMinProgress();
        max = (int) mAttrs.getMaxProgress();
        if (max == 0 && min == 0) { //handle the case where the range is not set
            max = 100;
            mAttrs.setMaxProgress(100);
        }
        diff = max - min;

        //load previous data if possible
        if (mAttrs.getStateData() != null && !mAttrs.getStateData().isEmpty()) {
            progressString = mAttrs.getStateData();
            progress = Integer.parseInt(mAttrs.getStateData());
            realProgress = 1.0 * (progress - min) / diff;
            sliderX = (int) (realProgress * (sliderEndX - sliderStartX) + sliderStartX); //adjust thumb

        }

        paintThumb = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintThumb.setStyle(Paint.Style.FILL);
        paintThumb.setColor(Color.WHITE);
        paintThumb.setShadowLayer(7, 0f, 3f, ThemeUtils.shadowColor);

        paintSliderBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSliderBackground.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSliderBackground.setStrokeCap(Paint.Cap.ROUND);
        paintSliderBackground.setStrokeWidth(sliderThickness);
        paintSliderBackground.setColor(ThemeUtils.backgroundColorVariant);

        paintSliderProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSliderProgress.setStyle(Paint.Style.FILL_AND_STROKE);
        paintSliderProgress.setStrokeCap(Paint.Cap.ROUND);
        paintSliderProgress.setStrokeWidth(sliderThickness);
        paintSliderProgress.setColor(mAttrs.getPrimaryColor());

        paintProgressText = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintProgressText.setTextSize(textSize);
        paintProgressText.setTextAlign(Paint.Align.CENTER);
        paintProgressText.setColor(Color.GRAY);
        paintProgressText.setStyle(Paint.Style.FILL);
        paintProgressText.setTypeface(Typeface.MONOSPACE);

        //adjust measures for showing percentage
        showProgress = mAttrs.isShowProgress();
        label = mAttrs.getText();
        if (showProgress | !label.isEmpty()) {
            //measure max text bounds
            String text = String.valueOf((int) mAttrs.getMaxProgress());
            Rect textBounds = new Rect();
            paintProgressText.getTextBounds(text, 0, text.length(), textBounds);
            textHeight = textBounds.height();
            wrap_height += textHeight;
            centerY = paddingTB + thumbRadius + textHeight;
            textOffsetedCenterY = centerY - (textBounds.height() - textBounds.bottom);
        }


        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setX(mAttrs.getX());
        setY(mAttrs.getY());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (firstMeasure) { //get the max width
            max_width = MeasureSpec.getSize(widthMeasureSpec);
            firstMeasure = false;
            setSize(mAttrs.getWidth(), 0);
            setOrientation(mAttrs.getOrientation());
        }
        width = Math.max(min_width, getRealWidth());//calculate width based on user data and limit it
        sliderEndX = width - offsetX;//calculate start of slider
        sliderX = (int) (realProgress * (sliderEndX - sliderStartX) + sliderStartX); //adjust thumb

        int widthSpec;
        int heightSpec;
        if (orientation == 90 || orientation == 270) { //flip specs
            widthSpec = MeasureSpec.makeMeasureSpec(wrap_height, MeasureSpec.EXACTLY);
            heightSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        } else { //keep as is
            widthSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightSpec = MeasureSpec.makeMeasureSpec(wrap_height, MeasureSpec.EXACTLY);
        }

        //publish measures to parent
        super.onMeasure(widthSpec, heightSpec);
    }

    protected int getRealWidth() {
        return (int) (mAttrs.getWidth() * max_width / 100.0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float dx = 0;
        float dy = 0;
        if (orientation == 90) {
            dx = 0;
            dy = -wrap_height;
        } else if (orientation == 180) {
            dx = -width;
            dy = -wrap_height;
        } else if (orientation == 270) {
            dx = -width;
            dy = 0;
        }

        canvas.save();
        canvas.rotate(orientation, 0, 0);
        canvas.translate(dx, dy);
        super.onDraw(canvas);
        //draw the background
        canvas.drawLine(sliderStartX, centerY, sliderEndX, centerY, paintSliderBackground);
        //draw progress line on top
        canvas.drawLine(sliderStartX, centerY, sliderX, centerY, paintSliderProgress);
        //draw thumb
        canvas.drawCircle(sliderX, centerY, thumbRadius, paintThumb);
        //draw progress test
        if (showProgress) {
            if (label.isEmpty())
                canvas.drawText(progressString, width / 2.0f, textOffsetedCenterY,
                        paintProgressText);
            else
                canvas.drawText(label.concat(" : ").concat(progressString), width / 2.0f, textOffsetedCenterY,
                        paintProgressText);

        } else if (!label.isEmpty()) {
            canvas.drawText(label, width / 2.0f, textOffsetedCenterY,
                    paintProgressText);
        }
        canvas.restore();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (noTouch) return false;

        float posX;
        if (orientation == 0) {
            posX = event.getX();
        } else if (orientation == 90) {
            posX = event.getY();
        } else if (orientation == 180) {
            posX = getWidth() - event.getX();
        } else { //270°
            posX = getHeight() - event.getY();
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                posX = Math.min(Math.max(sliderStartX, posX), sliderEndX);
                sliderX = (int) posX;
                realProgress = ((posX - sliderStartX) * 1.0) / (sliderEndX - sliderStartX);
                int tempProgress =
                        (int) (Math.round(realProgress * diff) + min);
                if (tempProgress != progress) {
                    progress = tempProgress;
                    progressChanged();
                }
                invalidate();
                break;
        }

        return true;
    }

    protected void progressChanged() {
        if (listener == null) return;
        progressString = String.valueOf(progress);
        parameters[varIndex].setDefValue(progressString);
        mCommand.setParams(parameters);
        listener.onCommandExecuted(mCommand);
    }

    @Override
    public void setPrimaryColor(int color) {
        mAttrs.setPrimaryColor(color);
        paintSliderProgress.setColor(color);
        invalidate();
    }

    @Override
    public void setSecondaryColor(int color) {

    }

    @Override
    public void setPrimaryText(String text) {
        if (text.isEmpty()) {
            adjustForText(false);
        } else if (label.isEmpty()) {
            adjustForText(true);
        }
        mAttrs.setText(text);
        this.label = text;
        invalidate();
    }

    @Override
    public void setOrientation(int angle) {
        mAttrs.setOrientation(angle);
        this.orientation = angle;
        requestLayout();
    }

    @Override
    public void setDrawable(int id) {

    }

    @Override
    public void setMinProgress(double min) {
        this.min = (int) min;
        diff = (int) (max - min);
        mAttrs.setMinProgress(min);
    }

    @Override
    public void setMaxProgress(double max) {
        this.max = (int) max;
        diff = (int) (max - min);
        mAttrs.setMaxProgress(max);
    }

    @Override
    public void setShowPercentage(boolean show) {
        mAttrs.setShowProgress(show);
        showProgress = show;
        adjustForText(show);
    }

    private void adjustForText(boolean showText) {
        if (showText) {
            //measure max text bounds
            String text = String.valueOf((int) mAttrs.getMaxProgress());
            Rect textBounds = new Rect();
            paintProgressText.getTextBounds(text, 0, text.length(), textBounds);
            textHeight = textBounds.height();
            wrap_height += textHeight;
            centerY = paddingTB + thumbRadius + textHeight;
            textOffsetedCenterY = centerY - (textBounds.height() - textBounds.bottom);
        } else {
            wrap_height = 2 * (paddingTB + thumbRadius);
            centerY = paddingTB + thumbRadius;
        }
        requestLayout();
    }

    @Override
    public int getVarParamsNbr() {
        return 1;
    }

    @Override
    public void setViewInMode(int mode) {
        if (mode == MODE_NO_TOUCH) {
            noTouch = true;
        } else {
            int padding = (int) (App.density_px * 16);
            setPadding(0, padding, 0, padding);
        }
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0, "sl", null,
                new Parameter[]{
//                        new Parameter("1", "which", 0, DEF_COMMAND),
                        new Parameter("0", "sliderVal", 1, DEF_COMMAND)
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd;
    }

    @Override
    public void setSize(int width, int height) {
        mAttrs.setWidth(width);
        requestLayout();
    }

    @Override
    public void setSizeInPixels(int newWidth, int newHeight) {

        if(orientation == 90 || orientation == 270) setSize((newHeight*100)/max_width,0);
        else setSize((newWidth*100)/max_width,0);

    }

    @Override
    public SizeMode getSizeMode() {
        if(orientation == 90 || orientation == 270) return SizeMode.HEIGHT_ONLY;
        else return SizeMode.WIDTH_ONLY;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        Context c = getContext();
        return new View[]{
                new TextPref(c, this, true, true, false),
                new RangePref(c, this, true, 0),
                new SizePref(c, this, true, false),
                new OrientationPref(c, this),
                new ColorPref(c, this, false, fm, null, null),
        };
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

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.listener = listener;
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(Type.VIEW_TYPE_SLIDER);
        attrs.setPrimaryColor(Color.rgb(255, 213, 0));
        attrs.setPreviewTitle("Slider");
        attrs.setMaxProgress(100);
        attrs.setWidth(100);
        return attrs;
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_slider_desc,
                R.string.doc_slider_reqs,
                R.string.doc_slider_usage
        };
    }

}
