package com.zmdev.protoplus.CustomViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.App;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.ColorPref;
import com.zmdev.protoplus.WidgetsPreferences.OrientationPref;
import com.zmdev.protoplus.WidgetsPreferences.SizePref;
import com.zmdev.protoplus.WidgetsPreferences.TextPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

public class ProtoTouchPad extends View implements ProtoView{

    //---------------- PView basics -----------------
    private Context mContext;
    private Command mCommand;
    private ProtoViewAttrs mAttrs;
    private Parameter[] parameters;
    private int varIndex1 = 0, varIndex2 = 0;
    private OnCommandExecutedListener listener;
    //------------------------------------------------
    private boolean firstMeasure =true;
    private int max_width;
    private int width;
    //pain & rect
    private String label;
    private RectF padRect;
    private Rect textBounds;
    private Paint rect_paint;
    private Paint text_paint;
    private int textY;
    private int textX;
    private float margin;
    private boolean isPreview = false;
    private float tempX = 0, tempY = 0;
    private float to_percentage = 0;
    private final String format = "%.2f";


    public ProtoTouchPad(@NonNull Context context, AttrsAndCommand specs) {
        super(context);
        mContext = context;
        mAttrs = specs.getAttrs();
        mCommand = specs.getCommand();

        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();

        try {
            parameters = mCommand.getParams();
            //store var params indices
            int varParamsCount = 0;
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getIsVariable() == 1 && varParamsCount == 0) {
                    varIndex1 = i;
                    varParamsCount++;
                } else if (parameters[i].getIsVariable() == 1 && varParamsCount == 1) {
                    varIndex2 = i;
                    break;
                }
            }
        } catch (NullPointerException ignored) {
            //this block will fail when a widget is added from the catalog
            //since no real command object will be passed as the attrs have not been
            //added yet to the DB (until user saves in editor)
            isPreview = true;
        }
        init();
    }

    private void init() {
        margin = (float) (App.density_px * 0);
        label = mAttrs.getText();

        //paint and stuff
        padRect = new RectF();
        rect_paint = new Paint(Paint.ANTI_ALIAS_FLAG); // anti aliasing prevents pixelated shapes
        rect_paint.setStyle(Paint.Style.FILL);
        rect_paint.setColor(mAttrs.getPrimaryColor());
        rect_paint.setAlpha(75);

        textBounds = new Rect();
        text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        text_paint.setTextAlign(Paint.Align.CENTER);
        text_paint.setColor(Color.GRAY);
        text_paint.setTextSize((float) (App.density_sp*18));
        //set specs
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setX(mAttrs.getX());
        setY(mAttrs.getY());
        setRotation(mAttrs.getOrientation());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (firstMeasure) {
            max_width = MeasureSpec.getSize(widthMeasureSpec);
            firstMeasure = false;
        }
        width = getRealWidth();
        to_percentage = 100.0f / width;


        //update text measures
        text_paint.getTextBounds(label, 0, label.length(), textBounds);
        textY = (width >> 1) + textBounds.centerY() - textBounds.bottom;
        textX = (width >> 1);

        padRect.set(margin, margin, width-margin,width-margin);

        int newWidth = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int newHeight = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        super.onMeasure(newWidth, newHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(padRect,10,10,rect_paint);
        canvas.drawText(label,textX,textY,text_paint);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        tempX = event.getX() * to_percentage;
        tempY = event.getY() * to_percentage;
        if (isPreview || tempX < 0 || tempY < 0 || tempX > 100 || tempY > 100) return false;
        parameters[varIndex1].setDefValue(String.format(format,tempX));
        parameters[varIndex2].setDefValue(String.format(format,tempY));
        mCommand.setParams(parameters);
        listener.onCommandExecuted(mCommand);
        return true;
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.listener = listener;
    }

    private int getRealWidth() {
        return (int) (mAttrs.getWidth()*max_width/100.0);
    }

    @Override
    public void setSize(int width, int height) {
        mAttrs.setWidth(width);
        requestLayout();
    }

    @Override
    public void setSizeInPixels(int newWidth, int newHeight) {
        mAttrs.setWidth(newWidth*100/max_width);
        requestLayout();
    }

    @Override
    public SizeMode getSizeMode() {
        return SizeMode.BOTH_ONLY;
    }

    @Override
    public void setOrientation(int angle) {
        mAttrs.setOrientation(angle);
        setRotation(angle);
    }

    @Override
    public void setPrimaryText(String text) {
        label = text;
        mAttrs.setText(text);
        invalidate();
    }

    @Override
    public void setPrimaryColor(int color) {
        mAttrs.setPrimaryColor(color);
        rect_paint.setColor(color);
        rect_paint.setAlpha(75);
        invalidate();
    }

    @Override
    public void setSecondaryColor(int color) {

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
        return 2;
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"pad",null,
                new Parameter[]{
                        new Parameter("0","X", 1,DEF_COMMAND),
                        new Parameter("0","Y", 1,DEF_COMMAND)
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd ;
    }

    @Override
    public void setViewInMode(int mode) {
        isPreview  = true ;
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(Type.VIEW_TYPE_TOUCHPAD);
        attrs.setPrimaryColor(0xFF64B5F6);
        attrs.setText("TouchPad");
        attrs.setPreviewTitle("TouchPad");
        attrs.setWidth(75);
        attrs.setLinkedCommandID(DEF_COMMAND);
        attrs.setProWidget(true);
        return attrs;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[]{
                new SizePref(mContext,this,true,false),
                new TextPref(mContext,this,true,false,false),
                new OrientationPref(mContext, this),
                new ColorPref(mContext, this, false, fm, null, null),
        };
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_touchpad_desc,
                R.string.doc_touchpad_reqs,
                R.string.doc_touchpad_usage
        };
    }

}
