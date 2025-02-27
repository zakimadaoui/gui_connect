package com.zmdev.protoplus.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.zmdev.protoplus.Adapters.WidgetPreviewAdapter;
import com.zmdev.protoplus.CustomViews.AccBlock;
import com.zmdev.protoplus.CustomViews.FluxBlock;
import com.zmdev.protoplus.CustomViews.GravityBlock;
import com.zmdev.protoplus.CustomViews.GyroBlock;
import com.zmdev.protoplus.CustomViews.OrientationBlock;
import com.zmdev.protoplus.CustomViews.ProtoColorPicker;
import com.zmdev.protoplus.CustomViews.ProtoJoystick;
import com.zmdev.protoplus.CustomViews.LightBlock;
import com.zmdev.protoplus.CustomViews.ProtoButton;
import com.zmdev.protoplus.CustomViews.ProtoFab;
import com.zmdev.protoplus.CustomViews.ProtoKeyPad;
import com.zmdev.protoplus.CustomViews.ProtoKnob;
import com.zmdev.protoplus.CustomViews.ProtoQuadJoy;
import com.zmdev.protoplus.CustomViews.ProtoSlider;
import com.zmdev.protoplus.CustomViews.ProtoSwitch;
import com.zmdev.protoplus.CustomViews.ProtoTextEdit;
import com.zmdev.protoplus.CustomViews.ProtoTouchPad;
import com.zmdev.protoplus.CustomViews.ProximityBlock;
import com.zmdev.protoplus.CustomViews.TextDisplayBlock;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.ThemeUtils;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;

import java.util.ArrayList;
import java.util.List;

public class WidgetsCatalogDialog extends DialogFragment {

    RecyclerView recycler;
    WidgetPreviewAdapter adapter;
    private PreviewCallback callback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, ThemeUtils.activityDialogThemeID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_widget_chooser, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.wchooser_back_btn).setOnClickListener(v -> dismiss());

        List<ProtoViewAttrs> attrsList = new ArrayList<>();
        attrsList.add(ProtoButton.getPreviewAttrs());
        attrsList.add(ProtoFab.getPreviewAttrs());
        attrsList.add(ProtoSlider.getPreviewAttrs());
        attrsList.add(ProtoSwitch.getPreviewAttrs());
        attrsList.add(ProtoJoystick.getPreviewAttrs());
        attrsList.add(ProtoQuadJoy.getPreviewAttrs());
        attrsList.add(ProtoKnob.getPreviewAttrs());
        attrsList.add(GyroBlock.getPreviewAttrs());
        attrsList.add(AccBlock.getPreviewAttrs());
        attrsList.add(GravityBlock.getPreviewAttrs());
        attrsList.add(FluxBlock.getPreviewAttrs());
        attrsList.add(LightBlock.getPreviewAttrs());
        attrsList.add(ProximityBlock.getPreviewAttrs());
        // attrsList.add(ProtoSliderFloat.getPreviewAttrs()); //TODO: under dev

        //pro widgets
        attrsList.add(OrientationBlock.getPreviewAttrs());
        attrsList.add(ProtoTextEdit.getPreviewAttrs());
        attrsList.add(TextDisplayBlock.getPreviewAttrs());
        attrsList.add(ProtoTouchPad.getPreviewAttrs());
        attrsList.add(ProtoKeyPad.getPreviewAttrs());
        attrsList.add(ProtoColorPicker.getPreviewAttrs());

        adapter = new WidgetPreviewAdapter(attrsList);

        adapter.setOnWidgetClickedListener(attrs -> {
            callback.onPreviewSelected(attrs);
            dismiss();
        });
        recycler = view.findViewById(R.id.widgets_gridlayout);
        recycler.setItemViewCacheSize(40);
        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(adapter);

    }

    public interface PreviewCallback {
        void onPreviewSelected(ProtoViewAttrs specs);
    }

    public void setCallback(PreviewCallback callback) {
        this.callback = callback;
    }


}
