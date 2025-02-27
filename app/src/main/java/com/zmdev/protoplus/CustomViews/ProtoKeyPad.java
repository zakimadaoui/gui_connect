package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_KEYPAD;

public class ProtoKeyPad extends GridLayout implements ProtoView, View.OnClickListener{

    /* New view check list:
     * 11- implement widget details
     * */

    //------------------ basics -------------------
    private Context mContext;
    private Command mCommand;
    private Parameter[] parameters;
    private int varIndex = 0;
    private ProtoViewAttrs mAttrs;
    private OnCommandExecutedListener listener;
    private Button[] pad = new Button[16];
    //---------------------------------------------

    public ProtoKeyPad(@NonNull Context context, AttrsAndCommand specs) {
        super(context);
        mContext = context;
        mAttrs = specs.getAttrs();
        mCommand = specs.getCommand();

        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();

        //store var params indices
        try {
            parameters = mCommand.getParams();
            if (parameters.length > 1) varIndex = 1;

        } catch (NullPointerException ignored) {
            //this block will fail when a widget is added from the catalog
            //since no real command object will be passed as the attrs have not been
            //added yet to the DB (until user saves in editor)
        }

        init();
    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_keypad, this);

        pad[0] =  findViewById(R.id.kypad_btn1);
        pad[1] =  findViewById(R.id.kypad_btn2);
        pad[2] =  findViewById(R.id.kypad_btn3);
        pad[3] =  findViewById(R.id.kypad_btn4);
        pad[4] =  findViewById(R.id.kypad_btn5);
        pad[5] =  findViewById(R.id.kypad_btn6);
        pad[6] =  findViewById(R.id.kypad_btn7);
        pad[7] =  findViewById(R.id.kypad_btn8);
        pad[8] =  findViewById(R.id.kypad_btn9);
        pad[9] =  findViewById(R.id.kypad_btn10);
        pad[10] = findViewById(R.id.kypad_btn11);
        pad[11] = findViewById(R.id.kypad_btn12);
        pad[12] = findViewById(R.id.kypad_btn13);
        pad[13] = findViewById(R.id.kypad_btn14);
        pad[14] = findViewById(R.id.kypad_btn15);
        pad[15] = findViewById(R.id.kypad_btn16);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (Button b : pad) b.setOnClickListener(this);
        setX(mAttrs.getX());
        setY(mAttrs.getY());
    }

    @Override
    public void setSize(int width, int height) {

    }

    @Override
    public void setSizeInPixels(int newWidth, int newHeight) {
    }

    @Override
    public SizeMode getSizeMode() {
        return SizeMode.NOT_RESIZABLE;
    }

    @Override
    public void setOrientation(int angle) {

    }

    @Override
    public void setViewInMode(int mode) {
        if (mode == MODE_NO_TOUCH) {
//            pad[3].setVisibility(GONE);
//            pad[7].setVisibility(GONE);
//            pad[11].setVisibility(GONE);
//            pad[15].setVisibility(GONE);
            for (int i = 0; i <16; i++) pad[i].setClickable(false);
        }
//        else if (mode == MODE_NO_TOUCH) {
//            for (int i = 0; i <16; i++) pad[i].setClickable(false);
//        }
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
        return 1;
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"k",null,
                new Parameter[]{
                        new Parameter("","key", 1,DEF_COMMAND)
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd ;
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(VIEW_TYPE_KEYPAD);
        attrs.setPreviewTitle("Keypad");
        attrs.setLinkedCommandID(DEF_COMMAND);
        attrs.setProWidget(true);
        return attrs;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[0];
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_keypad_desc,
                R.string.doc_keypad_reqs,
                R.string.doc_keypad_usage
        };
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            String text = ((Button) v).getText().toString();
            parameters[varIndex].setDefValue(text);
            listener.onCommandExecuted(mCommand);
        }
//         mCommand.setParams(parameters);
    }


}
