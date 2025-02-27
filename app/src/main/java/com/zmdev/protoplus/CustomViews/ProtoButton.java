package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.HapticFeedbackConstants;
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

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_BUTTON;

public class ProtoButton extends View implements ProtoView {
    private static final String TAG = "ProtoButton";
    private float textOffsetX;
    private float textOffsetY;
    private float textCenterX;
    private float textCenterY;
    private float btnWidth;
    private float minBtnWidth;
    private float minBtnHeight;
    private int orientation;
    private final float radius = (int) (5 * App.density_px);
    private float textSize = (float) (18 * App.density_sp);
    private String text = "BUTTON";
    private RectF btnRect;
    private Paint textPaint;
    private Paint btnPaint;

    //paddings
    private int textPadding = (int) (12 * App.density_px);
    private final float buttonPadding = (float) (4 * App.density_px);
    private final int pressedPadding = (int) buttonPadding + (int) (1.5 * App.density_px);

    private ProtoViewAttrs mAttrs;
    private Command command;
    private OnCommandExecutedListener listener;
    private boolean isPreview = false;
    private int max_width = 100;
    private boolean firstMeasure = true;

    public ProtoButton(Context context, AttrsAndCommand specs) {
        super(context);

        mAttrs = specs.getAttrs();
        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) command = getDefCommand();
        else command = specs.getCommand();

        //textPaint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);
        textPaint.setFakeBoldText(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTypeface(Typeface.MONOSPACE);

        //button paint
        btnRect = new RectF(0,0,0,0);
        btnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        btnPaint.setStyle(Paint.Style.FILL);
        btnPaint.setShadowLayer(7,3f,3f, ThemeUtils.shadowColor);

        //set the default layout params to be able to extract max size
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        //set other view attributes
        setX(mAttrs.getX());
        setY(mAttrs.getY());
        setPrimaryColor(mAttrs.getPrimaryColor());
        setPrimaryText(mAttrs.getText());
        orientation = mAttrs.getOrientation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (firstMeasure) {
            max_width = MeasureSpec.getSize(widthMeasureSpec);
            firstMeasure = false;
        }
        //get min dimensions by measuring text size
        measureMinDimensions();
        //adjust dimensions based on mode
        if (mAttrs.getWidth() == SIZE_WRAP) btnWidth = minBtnWidth;
        else btnWidth = max_width*mAttrs.getWidth()/100.0f;

        //adjust for padding
        btnWidth += 2 * buttonPadding;
        minBtnWidth += 2 * buttonPadding;
        minBtnHeight += 2 * buttonPadding;

        //constrain to minWidth
        if (btnWidth < minBtnWidth) btnWidth = minBtnWidth;

        //don't forget to calculate the center of the text
        findTextCenter();

        //recreate width and height specs
        int widthSpec;
        int heightSpec;
        if (mAttrs.getOrientation() == 90 || mAttrs.getOrientation() == 270) {
            widthSpec = MeasureSpec.makeMeasureSpec((int) (minBtnHeight), MeasureSpec.EXACTLY);
            heightSpec = MeasureSpec.makeMeasureSpec((int) (btnWidth), MeasureSpec.EXACTLY);
        } else {
            widthSpec = MeasureSpec.makeMeasureSpec((int) (btnWidth), MeasureSpec.EXACTLY);
            heightSpec = MeasureSpec.makeMeasureSpec((int) (minBtnHeight), MeasureSpec.EXACTLY);
        }
        //update rect size
        btnRect.set(buttonPadding, buttonPadding, btnWidth -buttonPadding ,minBtnHeight-buttonPadding);
        //publish measures to parent
        super.onMeasure(widthSpec, heightSpec);
    }

    //measure min btn dimensions based on text size
    //call this method only from after setSize() only !
    private void measureMinDimensions() {
        //get minimum size for the button from text bounds
        Rect textBounds = new Rect();
        textPaint.getTextBounds(text,0,text.length(), textBounds);
        minBtnWidth  = textBounds.width() + 2 * textPadding;
        minBtnHeight = textBounds.height() + 2 * textPadding;
        textOffsetX  = -(textBounds.left) / 2.0f;
        textOffsetY  = (textBounds.height() - textBounds.bottom) / 2.0f;
    }

    //find the pos to center the text
    private void findTextCenter() {
        textCenterX  = btnWidth / 2.0f + textOffsetX;
        textCenterY  = minBtnHeight / 2.0f + textOffsetY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float dx = 0;
        float dy = 0;
        if (orientation == 90) {
            dx = 0;
            dy = -minBtnHeight;
        } else if (orientation == 180) {
            dx = -btnWidth;
            dy = -minBtnHeight;
        } else if (orientation == 270) {
            dx = -btnWidth;
            dy = 0;
        }

        canvas.save();
        canvas.rotate(orientation, 0, 0);
        canvas.translate(dx, dy);
        super.onDraw(canvas);
        canvas.drawRoundRect(btnRect, radius, radius, btnPaint);
        canvas.drawText(text, textCenterX, textCenterY, textPaint);
        canvas.restore();

    }


    public void onClick() {
        if (!isPreview && listener!= null) listener.onCommandExecuted(command);
        performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING  // Ignore device's setting. Otherwise, you can use FLAG_IGNORE_VIEW_SETTING to ignore view's setting.
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                btnRect.set(pressedPadding, pressedPadding,
                        btnWidth - pressedPadding, minBtnHeight - pressedPadding);
                break;
            case MotionEvent.ACTION_UP:
                btnRect.set(buttonPadding, buttonPadding, btnWidth - buttonPadding,  minBtnHeight - buttonPadding);
                onClick();
                break;
        }
        invalidate();
        return true;
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    @Override
    public void setPrimaryColor(int color) {
        mAttrs.setPrimaryColor(color);
        btnPaint.setColor(color);
        invalidate();
    }

    @Override
    public void setPrimaryText(String text) {
        if (text == null || text.isEmpty()) text = "BUTTON";
        this.text = text;
        mAttrs.setText(text);
        requestLayout();
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
    }

    @Override
    public void setMaxProgress(double max) {
    }

    @Override
    public void setShowPercentage(boolean show) {

    }

    @Override
    public int getVarParamsNbr() {
        return 0;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        Context c = getContext();
        return new View[]{
                new TextPref(c, this, true, false, false)
                .setLabelHint(c.getString(R.string.button_cap))
                ,new SizePref(c, this, true, true),
                new OrientationPref(c, this),
                new ColorPref(c, this, false,fm,null,null),
        };
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(VIEW_TYPE_BUTTON);
        attrs.setWidth(20);
        attrs.setPrimaryColor(Color.rgb(15, 122, 255));
        attrs.setText("Button");
        attrs.setPreviewTitle("Button");
        return attrs;
    }

    @Override
    public void setSize(int width, int height) {
        mAttrs.setWidth(width);
        btnWidth = width;
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
    public void setViewInMode(int mode) {
        isPreview = true;
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"btn",null,
                new Parameter[]{
                        new Parameter("1","which", 0,DEF_COMMAND),
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd;
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.listener = listener;
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_button_desc,
                R.string.doc_button_reqs,
                R.string.doc_button_usage
        };
    }
}
