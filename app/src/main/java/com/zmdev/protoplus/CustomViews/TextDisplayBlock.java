package com.zmdev.protoplus.CustomViews;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.WidgetsPreferences.ColorPref;
import com.zmdev.protoplus.WidgetsPreferences.TextPref;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Entities.Parameter;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import org.jetbrains.annotations.NotNull;

import static com.zmdev.protoplus.CustomViews.ProtoView.Type.VIEW_TYPE_TEXT_OUTPUT;

public class TextDisplayBlock extends RelativeLayout implements ProtoView {


    private final ProtoViewAttrs mAttrs;
    private Command mCommand;
    private TextView title_txtvw;
    private TextView data_txtvw;
    private MyScrollView scrollView;

    public TextDisplayBlock(@NonNull Context context, AttrsAndCommand attrs) {
        super(context);
        mAttrs = attrs.getAttrs();

        if (mAttrs.getLinkedCommandID() == DEF_COMMAND) mCommand = getDefCommand();
        else mCommand = attrs.getCommand();

        init(context);
    }

    private void init(Context context) {
        //inflate layout
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.widget_display_layout, this);
        title_txtvw = findViewById(R.id.widget_display_title);
        data_txtvw = findViewById(R.id.widget_display_data);
        scrollView = findViewById(R.id.widget_display_scroll);

        if (mAttrs.getText().isEmpty()) {
            title_txtvw.setVisibility(GONE);
        } else {
            title_txtvw.setText(mAttrs.getText());
        }

        if (mAttrs.getWidth() != 0 && mAttrs.getHeight() != 0) {
            scrollView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            scrollView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            setLayoutParams(new RelativeLayout.LayoutParams(mAttrs.getWidth(), mAttrs.getHeight()));
        } else {
            setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        setPrimaryColor(mAttrs.getPrimaryColor());

        setX(mAttrs.getX());
        setY(mAttrs.getY());
    }


    @Override
    public void execReceiveCommand(String data) {
        data_txtvw.setText(data);
    }

    @Override
    public String getOutputWidgetCommandId() {
        return mCommand.getOpcode();
    }

    @Override
    public boolean receivesData() {
        return true;
    }

    @Override
    public ProtoViewAttrs getAttrs() {
        mAttrs.setX(getX());
        mAttrs.setY(getY());
        return mAttrs;
    }

    @Override
    public void setOrientation(int angle) { }

    public static ProtoViewAttrs getPreviewAttrs() {
        ProtoViewAttrs attrs = new ProtoViewAttrs();
        attrs.setViewType(VIEW_TYPE_TEXT_OUTPUT);
        attrs.setLinkedCommandID(DEF_COMMAND);
        attrs.setPrimaryColor(Color.parseColor("#2196F3"));
        attrs.setPreviewTitle("Text Display");
        attrs.setText("TextDisplay");
        attrs.setX(0);
        attrs.setY(0);
        attrs.setProWidget(true);
        return attrs;
    }

    @Override
    public void setSize(int width, int height) {
        setLayoutParams(new ViewGroup.LayoutParams(mAttrs.getWidth(), mAttrs.getHeight()));
    }

    @Override
    public void setSizeInPixels(int newWidth, int newHeight) {
        mAttrs.setWidth(newWidth);
        mAttrs.setHeight(newHeight);
        scrollView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        scrollView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(new RelativeLayout.LayoutParams(newWidth, newHeight));
    }

    @Override
    public SizeMode getSizeMode() {
        return SizeMode.FREE;
    }

    @Override
    public void setPrimaryColor(int color) {
        mAttrs.setPrimaryColor(color);
        title_txtvw.setTextColor(color);
        scrollView.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void setSecondaryColor(int color) {

    }

    @Override
    public void setPrimaryText(String text) {
        mAttrs.setText(text);
        title_txtvw.setText(text);
    }

    @Override
    public int getVarParamsNbr() {
        return 0;
    }

    @Override
    public void setViewInMode(int mode) {
        if (mode == ProtoView.MODE_NO_TOUCH) {
            scrollView.disableScroll();
        }
    }

    @NotNull
    @Override
    public Command getDefCommand() {
        Command cmnd = new Command(0,"disp1",null,
                new Parameter[]{
                });
        cmnd.setId(DEF_COMMAND);
        return cmnd ;
    }

    @Override
    public View[] getWidgetPreferences(FragmentManager fm) {
        return new View[]{
                new TextPref(getContext(), this, true, false, false),
                new ColorPref(getContext(),this, false, fm, null, null)

        };
    }

    @Override
    public void setOnCommandExecutedListener(OnCommandExecutedListener listener) {
    }

    @Override
    public int[] getViewDetailsArray(){
        return new int[]{
                R.string.doc_text_disp_desc,
                R.string.doc_text_disp_reqs,
                R.string.doc_text_disp_usage
        };
    }

}
