package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import static com.zmdev.protoplus.App.display_unit;
import static com.zmdev.protoplus.App.loss_offset;
import static com.zmdev.protoplus.App.unit_div;

public class ProtoSquareGrid extends View {

    private static final String TAG = "ProtoGrid";
    private float[] lines;
    private Paint   linesPaint;

    public ProtoSquareGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProtoSquareGrid(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLines(lines, linesPaint);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //generating grid
        int x_nbr = (int) (unit_div/4.0f);
        int y_nbr = (int) (h / (display_unit*4.0f)) +1;
        lines = new float[x_nbr * 4 + y_nbr * 4];
        int j = 0;
        for (int x = 0; x < x_nbr; x++) {
            lines[j]   = loss_offset + x * 4 * display_unit;
            lines[j+1] = 0;
            lines[j+2] = loss_offset + x * 4 * display_unit;
            lines[j+3] = h;
            j+=4;
        }
        for (int y = 0; y < y_nbr; y++) {
            lines[j]   = 0;
            lines[j+1] = y * 4 * display_unit;
            lines[j+2] = w;
            lines[j+3] = y * 4 * display_unit;
            j+=4;
        }
    }

    private void init() {
        linesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linesPaint.setColor(Color.argb(21,63,81,181));
        linesPaint.setStrokeWidth(3f); //2dp points
    }

}
