package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.HapticFeedbackConstants;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.App;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.ColorPref;
import com.zmdev.protoplus.WidgetsPreferences.IconPref;
import com.zmdev.protoplus.WidgetsPreferences.OrientationPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_FAB;

public class ProtoFab extends FloatingActionButton
        implements ProtoView, View.OnClickListener {

    private static final String TAG = "ProtoFab";
    private OnCommandExecutedListener commandCallback;
    private Command mCommand;
    private final ProtoViewAttrs mAttrs;
    private boolean isPreview = false;
    private int primary_color ;
    private int orientation = 0;
    private int size = 0;
    private final int padding = (int) (8 * App.density_px);


    public ProtoFab(@NonNull Context context, AttrsAndCommand specs) {
        super(context);
        mAttrs = specs.getAttrs();
        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();
        init();
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.commandCallback = listener;
    }


    @Override
    public void onClick(View v) {
        if (! isPreview ) commandCallback.onCommandExecuted(mCommand);
        performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        );
    }

    void init() {

        setOrientation(mAttrs.getOrientation());
        setImageTintList(ColorStateList.valueOf(Color.WHITE));
        if (mAttrs.getDrawableId() != 0) {
            setImageDrawable(App.iconPack.getIcon(mAttrs.getDrawableId()).getDrawable());
        } else {
            setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_gui));
        }
        setOnClickListener(this);
        setBackgroundTintList(ColorStateList.valueOf(mAttrs.getPrimaryColor()));

        setX(mAttrs.getX());
        setY(mAttrs.getY());

        //load attrs to vars
        if(mAttrs.getPrimaryColor() == 0) primary_color = ContextCompat.getColor(getContext(), R.color.app_color);
        else primary_color = mAttrs.getPrimaryColor();
    }

    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        mAttrs.setOrientation(orientation);
        mAttrs.setWidth(size);
        mAttrs.setPrimaryColor(primary_color);
        return mAttrs;
    }

    public static ProtoViewAttrs getPreviewAttrs() {

        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setLinkedCommandID(DEF_COMMAND);
        attrs.setViewType(VIEW_TYPE_FAB);
        attrs.setPrimaryColor(0xFFE66868);
        attrs.setPreviewTitle("Icon Button");
        return attrs;
    }

    //======================= Base view implementations ======================
    @Override
    public void setPrimaryColor(int color) {
        primary_color = color;
        setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void setSecondaryColor(int color) {
    }

    @Override
    public void setPrimaryText(String text) {
    }

    @Override
    public void setOrientation(int angle) {
        orientation = angle;
        setRotation(angle);
    }

    @Override
    public void setDrawable(int id) {
        mAttrs.setDrawableId(id);
        setImageDrawable(App.iconPack.getIcon(id).getDrawable());
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
    public void setSize(int width, int height) {
//        int min = getSuggestedMinimumWidth();
//        if (height < min && width == ProtoView.SIZE_CUSTOM) height = min;
//        this.size = height;
//
//        ViewGroup.LayoutParams params = getLayoutParams();
//        if (params == null) params = new ViewGroup.LayoutParams(0,0);
//        if (width == ProtoView.SIZE_FIT) {
//            if (orientation == 0 || orientation == 180) {
//                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                params.width = MAX_WIDGET_SIZE;
//            } else {
//                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//                params.height = MAX_WIDGET_SIZE;
//            }
//        }else if(width == ProtoView.SIZE_WRAP) {
//            params.height = ViewGroup.LayoutParams.WRAP_CONTENT ;
//            params.width =  ViewGroup.LayoutParams.WRAP_CONTENT ;
//        }else {//CUSTOM size or default
//            params.height = height;
//            params.width = height;
//        }
//        requestLayout();
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        Context c = getContext();
        return new View[]{
                new OrientationPref(c, this),
                new ColorPref(c, this, false,fm,null,null),
                new IconPref(c, this,fm),
        };
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
                        new Parameter("2","which", 0,DEF_COMMAND),
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd;
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
