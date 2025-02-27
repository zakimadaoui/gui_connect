package com.zmdev.protoplus.Dialogs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zmdev.protoplus.Adapters.CommandAdapter;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.ThemeUtils;
import com.zmdev.protoplus.db.Entities.Command;

public class WidgetDocsDialog extends DialogFragment {

    private int descStrID;
    private int reqsStrID;
    private int usageStrID;
    private Command command;

    public WidgetDocsDialog(int descStrID, int reqsStrID, int usageStrID, Command command) {
        this.descStrID = descStrID;
        this.reqsStrID = reqsStrID;
        this.usageStrID = usageStrID;
        this.command = command;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //configure dialog look
        setStyle(STYLE_NORMAL, ThemeUtils.dialogThemeID);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_widget_docs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView descTv = view.findViewById(R.id.wdoc_desc);
        descTv.setText(descStrID);
        TextView reqTv = view.findViewById(R.id.wdoc_reqs);
        reqTv.setText(reqsStrID);
        TextView usageTv = view.findViewById(R.id.wdoc_usage);
        usageTv.setText(usageStrID);

        TextView commandTv = view.findViewById(R.id.command_item_txt);
        commandTv.setText(CommandAdapter.getHighlightedCommand(command));


        view.findViewById(R.id.wdoc_yt_tutos).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/zakimadaoui/GuiConnectHelper/blob/master/docs/GuiConnect+%20tutorial%20II:%20custom%20commands.md")));
        });

    }


}

