package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

public class ProtoViewTemplate extends View implements ProtoView {

    /* New view check list:
     * 1- extend the type of view you need
     * 2- implement the init method
     * 3- make sure the command listener is not null
     * 4- implement the needed functions, remove unused ones
     * 5- implement the getPreviewAttrs() function
     * 6- Add View Constant in ProtoView.Type interface
     * 7- add ProtoWidget.getPreviewAttrs() to catalog in Dialogs.WidgetsCatalogDialog
     * 8- add case statement in ViewBuilder class
     * 9- implement preferences array
     * 10- implement default command
     * 11- implement widget details
     * 12- add support for orientation if needed
     * */

    //------------------ basics -------------------
    private Context mContext;
    private Command mCommand;
    private Parameter[] parameters;
    private int[] paramIndex; //if needed
    private ProtoViewAttrs mAttrs;
    private ProtoView.OnCommandExecutedListener listener;
    //---------------------------------------------

    public ProtoViewTemplate(@NonNull Context context, AttrsAndCommand specs) {
        super(context);
        mContext = context;
        mAttrs = specs.getAttrs();
        mCommand = specs.getCommand();

        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();

        //store var params indices
        try {
            paramIndex = new int[mCommand.getParamsNbr()];
            parameters = mCommand.getParams();
            //store var params indices
            int count = 0;
            for (int i = 0; i < parameters.length ; i++) {
                if (parameters[i].getIsVariable() == 1) paramIndex[count++] = i;
                if (count == mCommand.getParamsNbr()) break;
            }

            //later
            // parameters[paramIndex[0]].setDefValue(val1);
            // parameters[paramIndex[1]].setDefValue(val2);
            // linkedCommand.setParams(parameters);
            // callback.onCommandExecuted(linkedCommand);

        } catch (NullPointerException ignored) {
            //this block will fail when a widget is added from the catalog
            //since no real command object will be passed as the attrs have not been
            //added yet to the DB (until user saves in editor)
        }

        init();
    }

    private void init() {

        //inflater template:
        //------------------
        //LayoutInflater inflater = (LayoutInflater) mContext
        //        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //inflater.inflate(R.layout.widget_layout, this);
        //------------------

        //set size, position, default colors, text ...
    }

    @Override
    public void setSize(int width, int height) {

    }

    @Override
    public void setSizeInPixels(int newWidth, int newHeight) {
    }

    @Override
    public SizeMode getSizeMode() {
//        return SizeMode.BOTH_ONLY;
//        return SizeMode.FREE;
//        return SizeMode.WIDTH_ONLY;
//        return SizeMode.HEIGHT_ONLY;
        return SizeMode.NOT_RESIZABLE;
    }

    @Override
    public void setOrientation(int angle) {

    }

    @Override
    public void setPrimaryText(String text) { }

    @Override
    public void setPrimaryColor(int color) {

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
    public void setViewInMode(int mode) {
        //implement and add flags for touch if needed
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getVarParamsNbr() {
        return 0;
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"text",null,
                new Parameter[]{
                        new Parameter("hello world!","text", 1,DEF_COMMAND)
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd ;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
//        attrs.setViewType(YOUR_VIEW_TYPE);
//        attrs.setPrimaryColor(Color.rgb(15, 122, 255));
//        attrs.setSecondaryColor(Color.rgb(15, 122, 255));
//        attrs.setPreviewTitle("Toggle Button");
        attrs.setLinkedCommandID(DEF_COMMAND);
        return attrs;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[0];
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_accelerometer_desc,
                R.string.doc_accelerometer_reqs,
                R.string.doc_accelerometer_usage
        };
    }

}
