package com.zmdev.protoplus.CustomViews;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

public interface ProtoView {

    ProtoViewAttrs getAttrs();
    void setOrientation(int angle); //angle can be {0, 90, 180,270}
    int getVarParamsNbr();

    int[] getViewDetailsArray();
    View[] getWidgetPreferences(FragmentManager fm); //fragment manager needed in colorPreferences dialog
    default void setMinProgress(double min){}
    default void setMaxProgress(double max){}
    default void setShowPercentage(boolean show){}
    default void setPrimaryColor(int color){}
    default void setSecondaryColor(int color){}
    default void setPrimaryText(String text){}
    default void setDrawable(int id){}
    default ProtoViewAttrs saveAndGetCurrentState(){
        /* To save current state use
         * [] protoView. @ mAttrs.setStateData(String.valueOf(dataHere));
         * [] return maAttrs
         *
         * [] Implement needStateSave() function to let Proto+
         * know that state of this widget needs saving
         * */
        return null;
    }
    default boolean needStateSave(){return false;}

    int SIZE_WRAP = -1;
    void setSize(int width, int height);
    default void setSizeInPixels(int newWidth, int newHeight){}
    default SizeMode getSizeMode(){ return SizeMode.NOT_RESIZABLE; }
    enum SizeMode{
        NOT_RESIZABLE, //cannot resize manually from canvas
        WIDTH_ONLY, //only width (orientation dependent)
        HEIGHT_ONLY, //only height (orientation dependent)
        BOTH_ONLY, //both but not separate
        FREE // can resize both width and height separately
    }
    /*
     * Size Rules:
     * --------------
     * width and height are stored as percentage(eg: 100%) that way the
     * they wan all share the same SizePreference, then internally  each widget
     * decides how to use that percentage
     *
     * if width == -1 => WRAP_CONTENT (views measures itself & decides)
     * if width >=  0 => CUSTOM_WIDTH (0% to 100% parent width)
     *
     * 100% size means fill_parent_layout (not screen), this is cause widgets may
     * fit in the catalog layout as well as in the controller layour
     *
     * NOTE: SIZE and ORIENTATION must be compatible i.e. with should flip to height and vise versa
     * for vertical orientations
     * */


    int MODE_NO_TOUCH = 0;
    int MODE_PREVIEW  = 1;
    /*
     * View Modes:
     * No touch Mode: disable its touch listeners when in catalog or in canvas
     * preview mode : only set its params for preview display
     * */
    void setViewInMode(int mode);


    //Common method for widgets to generate their own default commands
    int DEF_COMMAND = -1;
    @NotNull
    Command getDefCommand();

    //callback for action performed on widget
    void setOnCommandExecutedListener(OnCommandExecutedListener listener);

    //used to check if the view needs to receive commands
    default boolean receivesData(){return false;}
    default void execReceiveCommand(String data){ }
    default String getOutputWidgetCommandId(){ return null; }

    interface OnCommandExecutedListener {
        void onCommandExecuted(Command command);
    }

    interface Type {
        int VIEW_TYPE_BUTTON = 0;
        int VIEW_TYPE_SLIDER = 1;
        int VIEW_TYPE_SLIDER_FLOAT = 5;
        int VIEW_TYPE_FAB = 2;
        int VIEW_TYPE_JOYSTICK = 3;
        int VIEW_TYPE_KNOB = 4;
        int VIEW_TYPE_GYRO = 6;
        int VIEW_TYPE_SWITCH = 7;
        int VIEW_TYPE_TOUCHPAD = 8;
        int VIEW_TYPE_QUAD_JOY = 9;
        int VIEW_TYPE_ACC = 10;
        int VIEW_TYPE_GRAVITY = 11;
        int VIEW_TYPE_FLUX = 12;
        int VIEW_TYPE_LIGHT = 13;
        int VIEW_TYPE_PROXIMITY = 14;
        int VIEW_TYPE_TEXT_OUTPUT = 15;
        int VIEW_TYPE_TEXT_BOX = 16;
        int VIEW_TYPE_COLOR_PICKER = 17;
        int VIEW_TYPE_ORIENTATION = 18;
        int VIEW_TYPE_KEYPAD = 19;
    }
}
