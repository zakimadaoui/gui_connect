package com.zmdev.protoplus.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zmdev.protoplus.Adapters.CommandAdapter;
import com.zmdev.protoplus.Adapters.SwipeToDelete;
import com.zmdev.protoplus.Adapters.SwipeToEdit;
import com.zmdev.protoplus.db.AppDatabase;
import com.zmdev.protoplus.db.Controllers.CommandsController;
import com.zmdev.protoplus.db.Controllers.ProjectsController;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.db.Entities.Project;
import com.zmdev.protoplus.db.Pattern.LiveData;
import com.zmdev.protoplus.db.Pattern.Observer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddCommandsFragment extends Fragment  {
    private static final String TAG = "AddCommandsFragment";
    private View view = null;
    private RecyclerView recycler;
    private CommandAdapter adapter;
    private Context mContext;
    private TextView project_title;
    private NavController navController;
    private CommandsController commandsController;
    List<Command> commands = new ArrayList<>();
    private int lastSwipedPosition;

    private final Observer<List<Command>> mObserver = new Observer<List<Command>>() {
        @Override
        public void onUpdate(List<Command> commands, LiveData.LiveDataState state) {
            AddCommandsFragment.this.commands = commands;
            adapter.setCommands(commands);

            Log.d(TAG, "onUpdate: " + state);

            switch (state) {
                case ON_ATTACH:
                    adapter.notifyDataSetChanged();
                    break;
                case INSERT:
                    adapter.notifyItemInserted(commands.size()-1);
                    break;
                case UPDATE:
                    adapter.notifyItemChanged(lastSwipedPosition);
                    break;
                case DELETE:
                    adapter.notifyItemRemoved(lastSwipedPosition);
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        commandsController = new CommandsController(mContext);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        if (view == null) { // need to check this due to nav-components caching
            view = inflater.inflate(R.layout.page_add_commands, container, false);

            //project title
            project_title = view.findViewById(R.id.add_commands_project_title);

            //add command fab
            view.findViewById(R.id.add_new_command_fab).setOnClickListener(v -> {
                CommandCreationDialog dialog = new CommandCreationDialog();
                dialog.show(getParentFragmentManager(),"new_cmnd_dialog");
            });

            //link to gui btn
            view.findViewById(R.id.link_to_gui_btn).setOnClickListener(v -> {
                navController.navigate(R.id.link_to_gui_action);
            });


            adapter = new CommandAdapter();
            recycler = view.findViewById(R.id.commands_recycler);
            recycler.setLayoutManager(new LinearLayoutManager(mContext));
            recycler.setAdapter(adapter);
            adapter.setOnCommandClickedListener((command, position) -> {
                openCommandEditorDialog(command);
                lastSwipedPosition = position;
            });

            commandsController.attachObserver(mObserver);

            //Swipe to delete
            new ItemTouchHelper(new SwipeToDelete(position -> {
                lastSwipedPosition = position;
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.delete_command_title)
                        .setMessage(R.string.delete_command_text)
                        .setPositiveButton(R.string.yes, (dialog, which) -> commandsController.delete(commands.get(position)))
                        .setNegativeButton(R.string.no, (dialog, which) -> adapter.notifyItemChanged(position))
                        .create()
                        .show();
            }, mContext)).attachToRecyclerView(recycler);

            //Swipe to edit
            new ItemTouchHelper(new SwipeToEdit(position -> {
                lastSwipedPosition = position;
                openCommandEditorDialog(commands.get(position));
                adapter.notifyItemChanged(position);
            }, mContext)).attachToRecyclerView(recycler);

        }

        project_title.setText(ProjectsController.getSelectedProject().getName());

        return view;
    }

    private void openCommandEditorDialog(Command command) {
        CommandCreationDialog dialog = new CommandCreationDialog();
        dialog.setInEditMode(command);
        dialog.show(getParentFragmentManager(),"");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mObserver != null )commandsController.detachObserver(mObserver);
    }
}
