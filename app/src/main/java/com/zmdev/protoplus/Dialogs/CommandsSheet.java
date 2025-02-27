package com.zmdev.protoplus.Dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zmdev.protoplus.Adapters.CommandAdapter;
import com.zmdev.protoplus.Fragments.CommandCreationDialog;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Entities.Command;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class CommandsSheet extends BottomSheetDialogFragment {

    private RecyclerView recycler;
    private List<Command> commands;
    private CommandAdapter adapter;
    private CommandsSheetCallback callback;

    public CommandsSheet(List<Command> commands, CommandsSheetCallback callback) {
        this.commands = commands;
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sheet_commands_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CommandAdapter();
        adapter.setCommands(commands);
        adapter.setOnCommandClickedListener((command, p) -> {
            callback.onClick(command);
            dismiss();
        });

        recycler = view.findViewById(R.id.sheet_commands_recycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        if (commands.isEmpty()) {
            view.findViewById(R.id.no_custom_commands_txt).setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.create_command_btn).setOnClickListener(v -> {
            CommandCreationDialog dialog = new CommandCreationDialog();
            dialog.show(getParentFragmentManager(),"new_cmnd_dialog");
            dismiss();
        });
    }

    public interface CommandsSheetCallback {
        void onClick(Command command);
    }

}
