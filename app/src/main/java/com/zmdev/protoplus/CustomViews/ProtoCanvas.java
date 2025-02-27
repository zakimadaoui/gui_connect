
package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;

import androidx.annotation.Nullable;

import com.zmdev.protoplus.App;
import com.zmdev.protoplus.Utils.ThemeUtils;

import static com.zmdev.protoplus.App.canvas_height;
import static com.zmdev.protoplus.App.display_unit;
import static com.zmdev.protoplus.App.loss_offset;
import static com.zmdev.protoplus.App.screen_height;
import static com.zmdev.protoplus.App.screen_width;
import static com.zmdev.protoplus.App.unit_div;

public class ProtoCanvas extends View {

    private static final String TAG = "ProtoGrid";
    //precision lines vars
    private float[] smallPoints;
    private float[] largePoints;
    private float[] lines = {
          //x           |         y
             0                  ,-1,
            screen_width        ,-1,
            -1                  ,0 ,
            -1                  ,screen_height
    };

    private Paint   smallPointsPaint;
    private Paint   largePointsPaint;
    private Paint   linesPaint;
    private boolean drawPrecisionLines = false;
    //selection and resizing vars
    private ProtoView selectedView;
    float sLeft, sTop, sRight, sBottom;
    private final RectF selectionRect = new RectF();
    private Paint selectionPaint;
    private Paint selectionCirclePaint;
    private final float resizeRadius = (float) (16 * App.density_px);
    private final float selectionPadding = (float) (8 * App.density_px);
    private final float selectionCircleRadius = (float) (8 * App.density_px);
    private final float selectionPaddingDouble = 2* selectionPadding;
    private int newWidth = 0, newHeight = 0;
    boolean isThumbPressed = false;
    private boolean isResizeBordersShown = false;


    public ProtoCanvas(Context context) {
        super(context);
    }

    public ProtoCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProtoCanvas(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPoints(smallPoints, smallPointsPaint);
        canvas.drawPoints(largePoints, largePointsPaint);
        if (drawPrecisionLines) canvas.drawLines(lines,linesPaint);
        if (isResizeBordersShown) {
            canvas.drawCircle(sRight,sBottom,selectionCircleRadius,selectionCirclePaint);
            canvas.drawRect(selectionRect, selectionPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int x_nbr = (int) unit_div; //nbr of pixel blocks on x-axis
        int y_nbr = (int) (h / display_unit); //nbr of pixel blocks on y-axis
        canvas_height = h;

        if (x_nbr%2 != 0) x_nbr++;

        //generating small points
        int nbr_points = (x_nbr + 1) * (y_nbr + 1);
        smallPoints = new float[nbr_points * 2]; //twice cuz of the two coordinates X and Y
        int i = 0;
        for (int y= 0; y <= y_nbr  ; y++) {
            for (int x= 0; x <= x_nbr ; x++) {
                smallPoints[i]   = loss_offset + x * display_unit;
                smallPoints[i+1] =  y * display_unit;
                i+=2;
            }
        }

        //generating larger points
        int x_nbr_large = x_nbr/4 ;
        int y_nbr_large = y_nbr/4 ;
        int nbr_large_points = (x_nbr_large+1) * (y_nbr_large+1) ;
        largePoints = new float[nbr_large_points * 2]; //twice cuz of the two coordinates X
        // and Y
        int j = 0;
        for (int y= 0; y <= y_nbr_large  ; y++) {
            for (int x= 0; x <= x_nbr_large ; x++) {
                largePoints[j]   = loss_offset + x * 4 * display_unit;
                largePoints[j+1] = y * 4 * display_unit;
                j+=2;
            }
        }
    }

    private void init() {

        smallPointsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallPointsPaint.setColor(ThemeUtils.canvasDotsColor);
        smallPointsPaint.setStrokeCap(Paint.Cap.ROUND); //disable this to get square
        smallPointsPaint.setStrokeWidth(4f); //5dp points
        smallPointsPaint.setStyle(Paint.Style.FILL);

        largePointsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        largePointsPaint.setColor(ThemeUtils.canvasPointerColor);
        largePointsPaint.setStrokeCap(Paint.Cap.ROUND); //disable this to get square
        largePointsPaint.setStrokeWidth(7f); //2dp points
        largePointsPaint.setStyle(Paint.Style.FILL);

        linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linesPaint.setColor(ThemeUtils.canvasPointerColor);
        linesPaint.setStrokeWidth(3f); //2dp points

        selectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectionPaint.setColor(ThemeUtils.canvasSelectorColor);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth((float) (1.5 * App.density_px));

        selectionCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectionCirclePaint.setColor(ThemeUtils.canvasSelectorColor);
        selectionCirclePaint.setStyle(Paint.Style.FILL);
    }


    //========================= Precision lines ===========================
    public void drawPrecisionLines(float x, float y) {
        drawPrecisionLines = true;
        lines[1] = y;
        lines[3] = y;
        lines[4] = x;
        lines[6] = x;
        invalidate();
    }

    public void hidePrecisionLines() {
        this.drawPrecisionLines = false;
        invalidate();
    }

    //========================= Resizing lines ===========================
    public void showResizingBorders(View v, int scroll, boolean showBorders){
        if (showBorders) {
            selectedView = (ProtoView) v;
            sLeft   = v.getX() + v.getLeft()  - selectionPadding;
            sRight  = v.getX() + v.getRight() + selectionPadding;
            sTop    = v.getY() + v.getTop()   - selectionPadding - scroll;
            sBottom = v.getY() + v.getBottom() + selectionPadding - scroll;
            selectionRect.set(sLeft, sTop, sRight, sBottom );
            isResizeBordersShown = true;
        } else {
            isResizeBordersShown = false;
            isThumbPressed = false;
        }
        invalidate();
    }

    public void performResizeThumbCheck(float x, float y, int scroll) {
            isThumbPressed = (Math.abs(y - scroll - sBottom) <= resizeRadius) && (Math.abs(x - sRight) <= resizeRadius);
            if(isThumbPressed)
                performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                );
    }

    public void trackThumbAndResize(float x, float y){
        //update depending on view resizing mode
        switch (selectedView.getSizeMode()) {
            case BOTH_ONLY:
                sBottom += x - sRight ;
                sRight = x;
                break;
            case WIDTH_ONLY:
                sRight = x;
                break;
            case HEIGHT_ONLY:
                sBottom = y;
                break;
            case FREE:
                sBottom = y ;
                sRight = x;
                break;
            case NOT_RESIZABLE:
                return;
        }
        newHeight = (int) (sBottom - sTop - selectionPaddingDouble);
        newWidth = (int) (sRight - sLeft - selectionPaddingDouble);

        //TODO: constrain width and height
//        int MINW = (int) (selectedView.getMinWidth() + 2*selectionPadding);
//        int MINH = (int) (selectedView.getMinHeight() + 2*selectionPadding);
//        newHeight = Math.max(newHeight, MINH);
//        sBottom = Math.max(sBottom, MINH+sTop);
//        newWidth = Math.max(newWidth, MINW);
//        sRight = Math.max(sRight, MINW+sLeft);

        selectedView.setSizeInPixels(newWidth,newHeight);
        selectionRect.set(sLeft, sTop, sRight, sBottom );
        invalidate();
    }

    public boolean isResizeBordersShown() {
        return isResizeBordersShown;
    }

    public boolean isThumbPressed() {
        return isThumbPressed;
    }
}
