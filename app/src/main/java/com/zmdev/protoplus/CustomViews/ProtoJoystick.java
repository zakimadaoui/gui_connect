/*
 * Copyright 2020 Zakaria Madaoui. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zmdev.protoplus.CustomViews;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.App;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.ThemeUtils;
import com.zmdev.protoplus.WidgetsPreferences.ColorPref;
import com.zmdev.protoplus.WidgetsPreferences.OrientationPref;
import com.zmdev.protoplus.WidgetsPreferences.SizePref;
import com.zmdev.protoplus.WidgetsPreferences.TextPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.App.density_px;
import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_JOYSTICK;

public class ProtoJoystick extends View implements ProtoView, ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "Joystick";
    private Parameter[] parameters;
    private int[] paramIndex;
    private RectF rectF;
    private Paint circlePaint;
    private Paint ballPaint;
    private float joyX;
    private float joyY;
    private float ballRadius = 25f;
    private int width = 0;
    private int height = 0;
    private int centerX  = 0;
    private int centerY  = 0;
    private double padding = 0;
    private ProtoViewAttrs mAttrs;
    private OnCommandExecutedListener execListener;
    private Command mCommand;
    boolean isPreview = false;
    private boolean firstMeasure = true;
    private int max_width = 0;
    private Paint textPaint;
    private int textHeight = 0;
    private int xTextPos;
    private int yTextPos;
    private int textSpace;
    private float outerCircleThickness;
    private int orientation;
    private boolean noTouch = false;

    ValueAnimator animator;
    private boolean onMeasureRequestedByUser = true;
    private int currentWidthMeasureSpec;
    private int currentHeightMeasureSpec;

    public ProtoJoystick(Context context, AttrsAndCommand specs) {
        super(context);
        mAttrs = specs.getAttrs();
        mCommand = specs.getCommand();

        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();

        //store var params indices
        try {
            paramIndex = new int[2];
            parameters = mCommand.getParams();
            //store var params indices
            int count = 0;
            for (int i = 0; i < parameters.length ; i++) {
                if (parameters[i].getIsVariable() == 1) paramIndex[count++] = i;
                if (count == 2) break;
            }
        } catch (NullPointerException ignored) {}

        init(mAttrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        if (firstMeasure) {
            max_width = MeasureSpec.getSize(widthMeasureSpec);
            firstMeasure = false;
        }

        if (onMeasureRequestedByUser) {
            onMeasureRequestedByUser = false;
            calculateTextMeasures();
            width = getRealWidth();
            height = width + textHeight;

            centerX = width / 2;
            centerY = width / 2 + textHeight;
            joyX = centerX; //centering joystick ball
            joyY = centerY; //centering joystick ball
            ballRadius = width / 4.0f; //

            //outer circle position parameters
            rectF.left = (float) (padding);
            rectF.top = (float) (textHeight + padding);
            rectF.right = (float) (width - padding);
            rectF.bottom = (float) (height - padding);


            if (orientation == 0 || orientation == 180) {
                currentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
                currentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            } else {
                currentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
                currentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(currentWidthMeasureSpec, currentHeightMeasureSpec);

    }

    private int getRealWidth() {
        int w = (int) (mAttrs.getWidth() * max_width / 100.0);
        return Math.max(100,w);
    }

    private void calculateTextMeasures(){
        textHeight = 0;
        Rect xBounds     = new Rect();
        Rect yBounds     = new Rect();
        boolean showProgress = mAttrs.isShowProgress();
        if (showProgress) { //show XY pos
            String xText = String.valueOf((int) mAttrs.getMaxProgress());
            String yText = String.valueOf((int) mAttrs.getMaxProgress());
            textPaint.getTextBounds(xText,0,xText.length(),xBounds);
            textPaint.getTextBounds(yText,0,yText.length(),yBounds);
            textHeight = (int) (xBounds.height() + yBounds.height() + textSpace + padding);
            xTextPos = textHeight;
            yTextPos = xTextPos - xBounds.height() - textSpace;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Log.d(TAG, "onDraw: jx "+ joyX);

        float dx = 0;
        float dy = 0;
        if (orientation == 90) {
            dx = 0;
            dy = -height;
        } else if (orientation == 180) {
            dx = -width;
            dy = -height;
        } else if (orientation == 270) {
            dx = -width;
            dy = 0;
        }
        canvas.save();
        canvas.rotate(orientation, 0, 0);
        canvas.translate(dx, dy);


        super.onDraw(canvas);
        //drawing ball and outer circle
        canvas.drawArc(rectF,0,360,false, circlePaint);
        canvas.drawCircle(joyX, joyY, ballRadius, ballPaint);
        if (mAttrs.isShowProgress()) {
            canvas.drawText(String.valueOf(progressX), centerX,xTextPos,textPaint);
            canvas.drawText(String.valueOf(progressY), centerX,yTextPos,textPaint);
        }

        canvas.restore();
    }

    public void init(ProtoViewAttrs attrs){

        int joyColor  = attrs.getPrimaryColor();
        int ballColor = attrs.getSecondaryColor();
        float textSize = (float) (14 * App.density_sp);
        padding = 8 * density_px;
        outerCircleThickness = 15f; //20dp is the default size
        textSpace = (int) (2 * density_px);

        //instantiating shapes and paints
        rectF = new RectF();//container for outer circle
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // anti aliasing prevents pixelized shapes
        circlePaint.setColor(joyColor);
        circlePaint.setStrokeWidth(outerCircleThickness);
        circlePaint.setStyle(Paint.Style.STROKE);

        ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ballPaint.setColor(ballColor);
        ballPaint.setStyle(Paint.Style.FILL);
        ballPaint.setShadowLayer(15f,0f,5f, ThemeUtils.shadowColor);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.GRAY);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTypeface(Typeface.MONOSPACE);

        animator = ValueAnimator.ofFloat(0f,100.0f);
        animator.setDuration(300);
        animator.addUpdateListener(this);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setX(mAttrs.getX());
        setY(mAttrs.getY());
        orientation = mAttrs.getOrientation();
    }

    //================= ProtoView implementation ==============
    
    @Override
    public void setSize(int width, int height) {
        onMeasureRequestedByUser = true;
        mAttrs.setWidth(width);
        requestLayout();
    }

    @Override
    public void setSizeInPixels(int width, int height) {
        onMeasureRequestedByUser = true;
        mAttrs.setWidth((int) (width *100.0/max_width));
        requestLayout();
    }

    @Override
    public SizeMode getSizeMode() {
        return SizeMode.BOTH_ONLY;
    }

    @Override
    public void setOrientation(int angle) {
        mAttrs.setOrientation(angle);
        orientation = angle;
        onMeasureRequestedByUser = true;
        requestLayout();
    }

    @Override
    public void setPrimaryColor(int color) {
        mAttrs.setPrimaryColor(color);
        circlePaint.setColor(color);
        postInvalidate();
    }

    @Override
    public void setSecondaryColor(int color) {
        mAttrs.setSecondaryColor(color);
        ballPaint.setColor(color);
        postInvalidate();
    }

    @Override
    public void setShowPercentage(boolean show) {
        mAttrs.setShowProgress(show);
        requestLayout();
    }

    @Override
    public int getVarParamsNbr() {
        return 2;
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        execListener = listener;
    }

    @Override
    public void setViewInMode(int mode) {
        isPreview = true;
        noTouch = mode == MODE_NO_TOUCH;
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"joy",null,
                new Parameter[]{
                        new Parameter("0","xVal", 1,DEF_COMMAND),
                        new Parameter("0","yVal", 1,DEF_COMMAND)
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd;
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(VIEW_TYPE_JOYSTICK);
        attrs.setPrimaryColor(Color.rgb(207, 207, 207));
        attrs.setSecondaryColor(Color.rgb(255, 193, 7));
        attrs.setPreviewTitle("Joystick");
        attrs.setWidth(100);
        attrs.setMaxProgress(1024);
        attrs.setShowProgress(true);
        return attrs;
    }
    
    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        Context c = getContext();
        return new View[]{
                new TextPref(c, this, false, true, false)
                .setPercentageBoxText(getContext().getString(R.string.show_xy)),
                new SizePref(c, this, true,false),
                new OrientationPref(c, this),
                new ColorPref(c, this, true,fm,null,null),
        };
    }

    float shiftedX;
    float shiftedY;
    float maxRadius;
    float r;
    int progressX;
    int progressY;
    int newProgressX;
    int newProgressY;
    float cosJoy;
    float sinJoy;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (noTouch) return false;

        if (orientation == 0) {
            joyX = event.getX();
            joyY = event.getY();
        } else if (orientation == 90) {
            joyX =  event.getY();
            joyY = height - event.getX();
        }else if (orientation == 180) {
            joyX = width - event.getX();
            joyY = height - event.getY();
        }else { //270°
            joyX = width - event.getY();
            joyY = event.getX();
        }
        shiftedX = joyX - centerX;
        shiftedY = joyY - centerY;

        Log.d(TAG, "onTouchEvent: jx "+ joyX);

        r = (float) Math.sqrt(shiftedX*shiftedX + shiftedY*shiftedY);
        maxRadius = (float) (width / 2.0f - ballRadius - padding + outerCircleThickness);
        cosJoy = shiftedX / r;
        sinJoy = shiftedY / r;

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            //if the ball is released reset its position to the center with an animation
            animator.start();
        }
        // constrain ball movement to the perimeter of the
        // outer circle if the finger is touching outside the circle
        else if (r > maxRadius) {
            joyX = maxRadius * cosJoy + centerX;
            joyY = maxRadius * sinJoy + centerY;
        }
//
        newProgressX = (int) Math.round(map(cosJoy, -1.0, 1.0, mAttrs.getMinProgress(),
                mAttrs.getMaxProgress()));
        newProgressY = (int) Math.round(map(sinJoy, -1.0, 1.0, mAttrs.getMinProgress(),
                mAttrs.getMaxProgress()));

        //only push changes if one of the values changes
        if (newProgressX != progressX || newProgressY != progressY) {
            progressX = newProgressX;
            progressY = newProgressY;
            OnMove(progressX, (int) (joyY*100));
        }
        invalidate();
        return true;
    }

    private double map(double val, double oldFrom, double oldTo, double newFrom, double newTo) {
        return newFrom + (((val - oldFrom)*(newFrom- newTo))/(oldFrom - oldTo));
    }

    private void OnMove(int px, int py){
        if (isPreview) return;
        parameters[paramIndex[0]].setDefValue(String.valueOf(px));
        parameters[paramIndex[1]].setDefValue(String.valueOf(py));
        execListener.onCommandExecuted(mCommand);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float val = 1.0f - (Float)animation.getAnimatedValue()/100.0f;
        r = Math.min(r, maxRadius) * val;
        joyX = r * cosJoy + centerX;
        joyY = r * sinJoy + centerY;

        newProgressX = (int) Math.round(map(r* cosJoy/maxRadius, -1.0, 1.0, mAttrs.getMinProgress(),
                mAttrs.getMaxProgress()));
        newProgressY = (int) Math.round(map(r* sinJoy/maxRadius, -1.0, 1.0, mAttrs.getMinProgress(),
                mAttrs.getMaxProgress()));

        //only push changes if one of the values changes
        if (newProgressX != progressX || newProgressY != progressY) {
            progressX = newProgressX;
            progressY = newProgressY;
            OnMove(progressX, progressY);
            invalidate();
        }
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_joystick_desc,
                R.string.doc_joystick_reqs,
                R.string.doc_joystick_usage
        };
    }


}
