package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.ColorPref;
import com.zmdev.protoplus.WidgetsPreferences.OrientationPref;
import com.zmdev.protoplus.WidgetsPreferences.QuadJoySizePref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

public class ProtoQuadJoy extends ConstraintLayout implements ProtoView {

    //basics
    private final Context mContext;
    private final ProtoViewAttrs mAttrs;
    private Command mCommand;
    private Parameter[] parameters;
    private int varIndex;
    private OnCommandExecutedListener listener;
    private boolean isPreview = false;


    private FloatingActionButton fab_up;
    private FloatingActionButton fab_down;
    private FloatingActionButton fab_left;
    private FloatingActionButton fab_right;
    private final OnClickListener clickListener = v -> {
        int id = v.getId();
        String cmnd ="";
        if (id == R.id.joy_up_btn) {
            cmnd = "U";
        } else if (id == R.id.joy_down_btn) {
            cmnd = "D";
        } else if (id == R.id.joy_left_btn) {
            cmnd = "L";
        } else if (id == R.id.joy_right_btn) {
            cmnd = "R";
        }

        if (!isPreview) {
            parameters[varIndex].setDefValue(cmnd);
            mCommand.setParams(parameters);
            listener.onCommandExecuted(mCommand);
        }
        performHapticFeedback(
                HapticFeedbackConstants.VIRTUAL_KEY,
                HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
        );
    };


    public ProtoQuadJoy(@NonNull Context context, AttrsAndCommand specs) {
        super(context);
        mContext = context;
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
        } catch (NullPointerException ignored) {
            //this block will fail when a widget is added from the catalog
            //since no real command object will be passed as the attrs have not been
            //added yet to the DB (until user saves in editor)
            isPreview = true;
        }

        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.view_joy_quad_btns, this);

        fab_up = findViewById(R.id.joy_up_btn);
        fab_down = findViewById(R.id.joy_down_btn);
        fab_left = findViewById(R.id.joy_left_btn);
        fab_right = findViewById(R.id.joy_right_btn);
        fab_up.setOnClickListener(clickListener);
        fab_down.setOnClickListener(clickListener);
        fab_left.setOnClickListener(clickListener);
        fab_right.setOnClickListener(clickListener);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setX(mAttrs.getX());
        setY(mAttrs.getY());
        setSize(mAttrs.getWidth(),0);
        setRotation(mAttrs.getOrientation());
        setPrimaryColor(mAttrs.getPrimaryColor());
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.listener = listener;
    }

    @Override
    public void setSize(int width, int height) {
        mAttrs.setWidth(width);
        if (width == 0) {
            fab_up.setSize(FloatingActionButton.SIZE_MINI);
            fab_down.setSize(FloatingActionButton.SIZE_MINI);
            fab_right.setSize(FloatingActionButton.SIZE_MINI);
            fab_left.setSize(FloatingActionButton.SIZE_MINI);
        } else {
            fab_up.setSize(FloatingActionButton.SIZE_NORMAL);
            fab_down.setSize(FloatingActionButton.SIZE_NORMAL);
            fab_right.setSize(FloatingActionButton.SIZE_NORMAL);
            fab_left.setSize(FloatingActionButton.SIZE_NORMAL);
        }
    }

    @Override
    public void setOrientation(int angle) {
        mAttrs.setOrientation(angle);
        setRotation(angle);
    }

    @Override
    public void setPrimaryText(String text) {

    }

    @Override
    public void setPrimaryColor(int color) {
        mAttrs.setPrimaryColor(color);
        fab_up.setBackgroundTintList(ColorStateList.valueOf(color));
        fab_down.setBackgroundTintList(ColorStateList.valueOf(color));
        fab_left.setBackgroundTintList(ColorStateList.valueOf(color));
        fab_right.setBackgroundTintList(ColorStateList.valueOf(color));
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
        return 1;
    }

    @Override
    public void setViewInMode(int mode) {
        isPreview = true;
        if (mode == MODE_NO_TOUCH) {
            fab_up.setClickable(false);
            fab_down.setClickable(false);
            fab_right.setClickable(false);
            fab_left.setClickable(false);
        }
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd =  new Command(0,"y",null,
                new Parameter[]{new Parameter("R","direction", 1,DEF_COMMAND)
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
        attrs.setViewType(Type.VIEW_TYPE_QUAD_JOY);
        attrs.setPrimaryColor(Color.rgb(177, 177, 177));
        attrs.setPreviewTitle("direction buttons");
        return attrs;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[]{
                new QuadJoySizePref(mContext, this),
                new OrientationPref(mContext, this),
                new ColorPref(mContext, this,false,fm,null,null),
        };
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_direction_desc,
                R.string.doc_direction_reqs,
                R.string.doc_direction_usage
        };
    }
}
