package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.Connections.BaseConnection;
import com.zmdev.protoplus.Connections.ConnectionStateKeeper;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.ColorPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_TEXT_BOX;

public class ProtoTextEdit extends LinearLayout implements ProtoView, View.OnClickListener {

    private static final String TAG = "ProtoTextBox";
    //--------------------------------------------------
    private Context mContext;
    private Command mCommand;
    private ProtoViewAttrs mAttrs;
    private Parameter[] parameters;
    private int varIndex1 = 0;
    private ProtoView.OnCommandExecutedListener listener;
    private BaseConnection connection;
    //---------------------------------------------------

    private EditText editText;
    private ImageButton imageButton;

    public ProtoTextEdit(Context context, AttrsAndCommand specs) {
        super(context);
        mContext = context;
        mAttrs = specs.getAttrs();
        mCommand = specs.getCommand();
        connection = ConnectionStateKeeper.getInstance().getConnection();

        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = specs.getCommand();

        try {
            parameters = mCommand.getParams();
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getIsVariable() == 1) {
                    varIndex1 = i;
                    break;
                }
            }
        } catch (NullPointerException ignored) { }

        init();
    }

    private void init(){
        //inflate layout
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_textbox, this);
        setX(mAttrs.getX());
        setY(mAttrs.getY());


        editText = findViewById(R.id.text_box_edit);
        imageButton = findViewById(R.id.text_box_send_btn);

        if (mAttrs.getWidth() != 0 && mAttrs.getHeight() != 0) {
            setLayoutParams(new ViewGroup.LayoutParams(mAttrs.getWidth(), mAttrs.getHeight()));
            editText.getLayoutParams().height = mAttrs.getHeight();
        }

        if (mAttrs.getPrimaryColor() != 0) {
            imageButton.setImageTintList(ColorStateList.valueOf(mAttrs.getPrimaryColor()));
        }

        imageButton.setOnClickListener(this);
        setPrimaryColor(mAttrs.getPrimaryColor());
    }

    @Override
    public void onClick(View v) {
        if (listener == null) return;
        String text = editText.getText().toString();
        if (!text.isEmpty() && connection!= null) {
//            parameters[varIndex1].setDefValue(text);
//            mCommand.setParams(parameters);
//            listener.onCommandExecuted(mCommand);
            connection.write(text);
            editText.setText(null);
        }
    }
    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    @Override
    public int getVarParamsNbr() {
        return -1;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[]{
                new ColorPref(mContext,this, false, fm, null, null)
        };
    }

    @Override
    public void setSizeInPixels(int newWidth, int newHeight) {
            mAttrs.setWidth(newWidth);
            mAttrs.setHeight(newHeight);
            getLayoutParams().width = newWidth;
            getLayoutParams().height = newHeight;
            editText.getLayoutParams().height = mAttrs.getHeight();
            requestLayout();
    }

    @Override
    public void setSize(int width, int height) {
        //ignored
    }

    @Override
    public SizeMode getSizeMode() {
        return SizeMode.FREE;
    }

    @Override
    public void setViewInMode(int mode) {
        if (mode == ProtoView.MODE_NO_TOUCH){
            imageButton.setClickable(false);
            editText.setVisibility(INVISIBLE);
        }
    }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(VIEW_TYPE_TEXT_BOX);
        attrs.setLinkedCommandID(DEF_COMMAND);
        attrs.setPrimaryColor(Color.parseColor("#2196F3"));
        attrs.setPreviewTitle("Text Edit");
        attrs.setText("Text Edit");
        attrs.setWidth(0);
        attrs.setHeight(0);
        attrs.setX(0);
        attrs.setY(0);
        attrs.setProWidget(true);
        return attrs;
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"NO COMMAND REQUIRED",null,
                new Parameter[]{});
        cmnd.setId(DEF_COMMAND);
        return cmnd ;
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
        this.listener = listener;
    }

    @Override
    public void setPrimaryColor(int color) {
        mAttrs.setPrimaryColor(color);
        imageButton.setImageTintList(ColorStateList.valueOf(color));
        findViewById(R.id.text_box_layout).setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_text_edit_desc,
                R.string.doc_text_edit_reqs,
                R.string.doc_text_edit_usage
        };
    }


}
