package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.CallSuper;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.Utils;

public class Croller extends View {

    private float midx, midy;
    protected Paint textPaint, circlePaint, circlePaint2, linePaint;
    private float currdeg = 0, deg = 3, downdeg = 0;
    int progress = 0, previousProgress = 0;

    private boolean isContinuous = false;

    private int backCircleColor = Color.parseColor("#222222");
    private int mainCircleColor = Color.parseColor("#000000");
    private int indicatorColor = Color.parseColor("#FFA036");
    private int progressPrimaryColor = Color.parseColor("#FFA036");
    private int progressSecondaryColor = Color.parseColor("#111111");

    private float progressPrimaryCircleSize = -1;
    private float progressSecondaryCircleSize = -1;

    private float progressPrimaryStrokeWidth = 25;
    private float progressSecondaryStrokeWidth = 10;

    private float mainCircleRadius = -1;
    private float backCircleRadius = -1;
    private float progressRadius = -1;

    private int max = 25;
    private int min = 1;

    private float indicatorWidth = 7;

    private String label = "Label";
    private int labelSize = 40;
    private int labelColor = Color.WHITE;

    private int startOffset = 30;
    private int startOffset2 = 0;
    private int sweepAngle = -1;
    private boolean isAntiClockwise = false;

    RectF oval;

    public Croller(Context context) {
        super(context);
        init();
    }

    private void init() {
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(labelColor);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(labelSize);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.CENTER);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(progressSecondaryColor);
        circlePaint.setStrokeWidth(progressSecondaryStrokeWidth);
        circlePaint.setStyle(Paint.Style.FILL);

        circlePaint2 = new Paint();
        circlePaint2.setAntiAlias(true);
        circlePaint2.setColor(progressPrimaryColor);
        circlePaint2.setStrokeWidth(progressPrimaryStrokeWidth);
        circlePaint2.setStyle(Paint.Style.FILL);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setColor(indicatorColor);
        linePaint.setStrokeWidth(indicatorWidth);

        oval = new RectF();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minWidth = (int) Utils.convertDpToPixel(160, getContext());
        int minHeight = (int) Utils.convertDpToPixel(160, getContext());

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(minWidth, widthSize);
        } else {
            // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
            // If width is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make width equal to height
            width = heightSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(minHeight, heightSize);
        } else {
            // only in case of ScrollViews, otherwise MeasureSpec.UNSPECIFIED is never triggered
            // If height is wrap_content i.e. MeasureSpec.UNSPECIFIED, then make height equal to width
            height = widthSize;
        }

        if (widthMode == MeasureSpec.UNSPECIFIED && heightMode == MeasureSpec.UNSPECIFIED) {
            width = minWidth;
            height = minHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        midx = canvas.getWidth() / 2;
        midy = canvas.getHeight() / 2;

        if (!isContinuous) {

            startOffset2 = startOffset - 15;

            circlePaint.setColor(progressSecondaryColor);
            circlePaint2.setColor(progressPrimaryColor);
            circlePaint2.setStyle(Paint.Style.FILL);
            linePaint.setStrokeWidth(indicatorWidth);
            linePaint.setColor(indicatorColor);
            textPaint.setColor(labelColor);
            textPaint.setTextSize(labelSize);

            int radius = (int) (Math.min(midx, midy) * ((float) 14.5 / 16));

            if (sweepAngle == -1) {
                sweepAngle = 360 - (2 * startOffset2);
            }

            if (mainCircleRadius == -1) {
                mainCircleRadius = radius * ((float) 11 / 15);
            }
            if (backCircleRadius == -1) {
                backCircleRadius = radius * ((float) 13 / 15);
            }
            if (progressRadius == -1) {
                progressRadius = radius;
            }

            float x, y;
            float deg2 = Math.max(3, deg);
            float deg3 = Math.min(deg, max + 2);
            for (int i = (int) (deg2); i < max + 3; i++) {
                float tmp = ((float) startOffset2 / 360) + ((float) sweepAngle / 360) * (float) i / (max + 5);

                if (isAntiClockwise) {
                    tmp = 1.0f - tmp;
                }

                x = midx + (float) (progressRadius * Math.sin(2 * Math.PI * (1.0 - tmp)));
                y = midy + (float) (progressRadius * Math.cos(2 * Math.PI * (1.0 - tmp)));
                circlePaint.setColor(progressSecondaryColor);
                if (progressSecondaryCircleSize == -1)
                    canvas.drawCircle(x, y, ((float) radius / 30 * ((float) 20 / max) * ((float) sweepAngle / 270)), circlePaint);
                else
                    canvas.drawCircle(x, y, progressSecondaryCircleSize, circlePaint);
            }
            for (int i = 3; i <= deg3; i++) {
                float tmp = ((float) startOffset2 / 360) + ((float) sweepAngle / 360) * (float) i / (max + 5);

                if (isAntiClockwise) {
                    tmp = 1.0f - tmp;
                }

                x = midx + (float) (progressRadius * Math.sin(2 * Math.PI * (1.0 - tmp)));
                y = midy + (float) (progressRadius * Math.cos(2 * Math.PI * (1.0 - tmp)));
                if (progressPrimaryCircleSize == -1)
                    canvas.drawCircle(x, y, (progressRadius / 15 * ((float) 20 / max) * ((float) sweepAngle / 270)), circlePaint2);
                else
                    canvas.drawCircle(x, y, progressPrimaryCircleSize, circlePaint2);
            }

            float tmp2 = ((float) startOffset2 / 360) + ((float) sweepAngle / 360) * deg / (max + 5);

            if (isAntiClockwise) {
                tmp2 = 1.0f - tmp2;
            }

            float x1 = midx + (float) (radius * ((float) 2 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
            float y1 = midy + (float) (radius * ((float) 2 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
            float x2 = midx + (float) (radius * ((float) 3 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
            float y2 = midy + (float) (radius * ((float) 3 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));

            circlePaint.setColor(backCircleColor);
            canvas.drawCircle(midx, midy, backCircleRadius, circlePaint);
            circlePaint.setColor(mainCircleColor);
            canvas.drawCircle(midx, midy, mainCircleRadius, circlePaint);
            canvas.drawText(label, midx, midy + (float) (radius * 1.1), textPaint);
            canvas.drawLine(x1, y1, x2, y2, linePaint);

        } else {

            int radius = (int) (Math.min(midx, midy) * ((float) 14.5 / 16));

            if (sweepAngle == -1) {
                sweepAngle = 360 - (2 * startOffset);
            }

            if (mainCircleRadius == -1) {
                mainCircleRadius = radius * ((float) 11 / 15);
            }
            if (backCircleRadius == -1) {
                backCircleRadius = radius * ((float) 13 / 15);
            }
            if (progressRadius == -1) {
                progressRadius = radius;
            }

            circlePaint.setColor(progressSecondaryColor);
            circlePaint.setStrokeWidth(progressSecondaryStrokeWidth);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint2.setColor(progressPrimaryColor);
            circlePaint2.setStrokeWidth(progressPrimaryStrokeWidth);
            circlePaint2.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(indicatorWidth);
            linePaint.setColor(indicatorColor);
            textPaint.setColor(labelColor);
            textPaint.setTextSize(labelSize);

            float deg3 = Math.min(deg, max + 2);

            oval.set(midx - progressRadius, midy - progressRadius, midx + progressRadius, midy + progressRadius);

            canvas.drawArc(oval, (float) 90 + startOffset, (float) sweepAngle, false, circlePaint);
            if (isAntiClockwise) {
                canvas.drawArc(oval, (float) 90 - startOffset, -1 * ((deg3 - 2) * ((float) sweepAngle / max)), false, circlePaint2);
            } else {
                canvas.drawArc(oval, (float) 90 + startOffset, ((deg3 - 2) * ((float) sweepAngle / max)), false, circlePaint2);
            }

            float tmp2 = ((float) startOffset / 360) + (((float) sweepAngle / 360) * ((deg - 2) / (max)));

            if (isAntiClockwise) {
                tmp2 = 1.0f - tmp2;
            }

            float x1 = midx + (float) (radius * ((float) 2 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
            float y1 = midy + (float) (radius * ((float) 2 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));
            float x2 = midx + (float) (radius * ((float) 3 / 5) * Math.sin(2 * Math.PI * (1.0 - tmp2)));
            float y2 = midy + (float) (radius * ((float) 3 / 5) * Math.cos(2 * Math.PI * (1.0 - tmp2)));

            circlePaint.setStyle(Paint.Style.FILL);

            circlePaint.setColor(backCircleColor);
            canvas.drawCircle(midx, midy, backCircleRadius, circlePaint);
            circlePaint.setColor(mainCircleColor);
            canvas.drawCircle(midx, midy, mainCircleRadius, circlePaint);
            canvas.drawText(label, midx, midy + (float) (radius * 1.1), textPaint);
            canvas.drawLine(x1, y1, x2, y2, linePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            downdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            downdeg -= 90;
            if (downdeg < 0) {
                downdeg += 360;
            }
            downdeg = (float) Math.floor((downdeg / 360) * (max + 5));

            return true;
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = e.getX() - midx;
            float dy = e.getY() - midy;
            currdeg = (float) ((Math.atan2(dy, dx) * 180) / Math.PI);
            currdeg -= 90;
            if (currdeg < 0) {
                currdeg += 360;
            }
            currdeg = (float) Math.floor((currdeg / 360) * (max + 5));

            if ((currdeg / (max + 4)) > 0.75f && ((downdeg - 0) / (max + 4)) < 0.25f) {
                if (isAntiClockwise) {
                    deg++;
                    if (deg > max + 2) {
                        deg = max + 2;
                    }
                } else {
                    deg--;
                    if (deg < (min + 2)) {
                        deg = (min + 2);
                    }
                }
            } else if ((downdeg / (max + 4)) > 0.75f && ((currdeg - 0) / (max + 4)) < 0.25f) {
                if (isAntiClockwise) {
                    deg--;
                    if (deg < (min + 2)) {
                        deg = (min + 2);
                    }
                } else {
                    deg++;
                    if (deg > max + 2) {
                        deg = max + 2;
                    }
                }
            } else {
                if (isAntiClockwise) {
                    deg -= (currdeg - downdeg);
                } else {
                    deg += (currdeg - downdeg);
                }
                if (deg > max + 2) {
                    deg = max + 2;
                }
                if (deg < (min + 2)) {
                    deg = (min + 2);
                }
            }

            downdeg = currdeg;
            invalidate();
            progress = (int) (deg-2);
            if(progress != previousProgress) {
                onProgressChanged(progress);
                previousProgress = progress;
            }
            return true;

        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (getParent() != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(event);
    }

    //to be implemented by child or here if needed
    @CallSuper
    protected void onProgressChanged(int progress){
    }

    public int getProgress() {
        return (int) (deg - 2);
    }

    public void setProgress(int x) {
        deg = x + 2;
        invalidate();
    }

    public void setLabel(String txt) {
        label = txt;
        invalidate();
    }

    public void setBackCircleColor(int backCircleColor) {
        this.backCircleColor = backCircleColor;
        invalidate();
    }

    public void setMainCircleColor(int mainCircleColor) {
        this.mainCircleColor = mainCircleColor;
        invalidate();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setProgressPrimaryColor(int progressPrimaryColor) {
        this.progressPrimaryColor = progressPrimaryColor;
        invalidate();
    }

    public int getProgressSecondaryColor() {
        return progressSecondaryColor;
    }

    public void setProgressSecondaryColor(int progressSecondaryColor) {
        this.progressSecondaryColor = progressSecondaryColor;
        invalidate();
    }

    public void setLabelSize(int labelSize) {
        this.labelSize = labelSize;
        invalidate();
    }

    public void setLabelColor(int labelColor) {
        this.labelColor = labelColor;
        invalidate();
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
        invalidate();
    }

    public boolean isContinuous() {
        return isContinuous;
    }

    public void setIsContinuous(boolean isContinuous) {
        this.isContinuous = isContinuous;
        invalidate();
    }

    public void setProgressPrimaryCircleSize(float progressPrimaryCircleSize) {
        this.progressPrimaryCircleSize = progressPrimaryCircleSize;
        invalidate();
    }

    public void setProgressSecondaryCircleSize(float progressSecondaryCircleSize) {
        this.progressSecondaryCircleSize = progressSecondaryCircleSize;
        invalidate();
    }

    public void setProgressPrimaryStrokeWidth(float progressPrimaryStrokeWidth) {
        this.progressPrimaryStrokeWidth = progressPrimaryStrokeWidth;
        invalidate();
    }

    public void setProgressSecondaryStrokeWidth(float progressSecondaryStrokeWidth) {
        this.progressSecondaryStrokeWidth = progressSecondaryStrokeWidth;
        invalidate();
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
        invalidate();
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = Math.max(max, min);
        progressRadius = -1;
        backCircleRadius = -1;
        mainCircleRadius = -1;
        sweepAngle = -1;
        progressPrimaryCircleSize = -1;
        progressSecondaryCircleSize = -1;
        deg = 2; //resets progress
        invalidate();
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        if (min < 0) {
            this.min = 0;
        } else this.min = Math.min(min, max);
        progressRadius = -1;
        backCircleRadius = -1;
        mainCircleRadius = -1;
        sweepAngle = -1;
        progressPrimaryCircleSize = -1;
        progressSecondaryCircleSize = -1;
        progress = 0;
        invalidate();
    }

    public float getMainCircleRadius() {
        return mainCircleRadius;
    }

    public void setMainCircleRadius(float mainCircleRadius) {
        this.mainCircleRadius = mainCircleRadius;
        invalidate();
    }

    public float getBackCircleRadius() {
        return backCircleRadius;
    }

    public void setBackCircleRadius(float backCircleRadius) {
        this.backCircleRadius = backCircleRadius;
        invalidate();
    }

    public float getProgressRadius() {
        return progressRadius;
    }

    public void setProgressRadius(float progressRadius) {
        this.progressRadius = progressRadius;
        invalidate();
    }

    public void setAntiClockwise(boolean antiClockwise) {
        isAntiClockwise = antiClockwise;
        invalidate();
    }
}
