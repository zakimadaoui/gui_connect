package com.zmdev.protoplus.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.zmdev.protoplus.App;
import com.zmdev.protoplus.Connections.BaseConnection;
import com.zmdev.protoplus.Connections.ConnectionClassInUseException;
import com.zmdev.protoplus.Connections.ConnectionStateKeeper;
import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.TutorialFlow;
import com.zmdev.protoplus.ViewBuilder;
import com.zmdev.protoplus.db.AppDatabase;
import com.zmdev.protoplus.db.Controllers.ProjectsController;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Controllers.AttrsController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GuiFragment extends Fragment {

    private static final String TAG = "GuiFragment";
    private List<View> viewList = new ArrayList<>();
    Map<String, ProtoView> outputWidgetsMap = new HashMap<>();
    private Context mContext;
    private BaseConnection connection;
    private RelativeLayout controller_layout;
    private AttrsController attrsRepo;
    private TextView command_txt;
//    private ProtoSquareGrid grid;
    private NavController navController;
    private boolean justCreated = true;
    private int projectID;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        attrsRepo = new AttrsController(getContext());
        connection = ConnectionStateKeeper.getInstance().getConnection();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, backPressCallback);

        //register incoming commands
        try {
            if (connection != null)
            connection.registerToIncomingCommandCallback(incomingCommandsCallback);
        } catch (ConnectionClassInUseException e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_gui_controller, container, false);
        command_txt = view.findViewById(R.id.command_txt);
        controller_layout = view.findViewById(R.id.controller_layout);
//        guiScrollView = view.findViewById(R.id.gui_scroll_view);
//        grid = view.findViewById(R.id.square_grid);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        projectID = ProjectsController.getSelectedProject().getId();

        //Edit GUI button
        ImageButton edit_btn = view.findViewById(R.id.edit_controller_btn);
        edit_btn.setOnClickListener(v -> {
            saveState();
            navController.navigate(R.id.edit_controller_action);
        });
        TutorialFlow.showFlowFor(getActivity(), edit_btn, getString(R.string.tuto_editor), "2");

        //command button
        ImageButton cmnd_btn = view.findViewById(R.id.controller_show_command);
        cmnd_btn.setOnClickListener(v -> {
            if (command_txt.getVisibility() == View.GONE) command_txt.setVisibility(View.VISIBLE);
            else command_txt.setVisibility(View.GONE);
        });
        view.findViewById(R.id.controller_back).setOnClickListener(v -> {
            saveState();
            navController.navigateUp();
        });

        //Show grid button
        view.findViewById(R.id.controller_tutorials_btn).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/zakimadaoui/GuiConnectHelper/blob/master/docs/Getting%20started%20with%20GuiConnect%2B.md")));
//            if (grid.getVisibility() == View.INVISIBLE) grid.setVisibility(View.VISIBLE);
//            else grid.setVisibility(View.INVISIBLE);
        });

        loadUI(); //Load GUI from DB

        TutorialFlow.showPersistentTutorialsDialog(mContext);

        }

    private void loadUI() {
        new Thread(() -> {
            List<AttrsAndCommand> attrsAndCommands = AppDatabase.getInstance(mContext).attrsDAO().getAttrsAndCommandsList(projectID);

            new Handler(Looper.getMainLooper()).post(() -> {
                //clear old lists
                viewList.clear();
                viewsWithStateToSave.clear();
                //remove old views first
                controller_layout.removeAllViews();
                //load widgets to layout
                // populate list of Views With State to save
                // populate Map of receiving commands Widgets
                ProtoView pv;
                for (AttrsAndCommand specs : attrsAndCommands) {
                    View v = ViewBuilder.generateViewFromAttrs(specs, mContext);
                    pv = (ProtoView) v;
                    pv.setOnCommandExecutedListener(commandExecutedListener);
                    if(pv.needStateSave()) viewsWithStateToSave.add((ProtoView)v);
                    if(pv.receivesData()) outputWidgetsMap.put(pv.getOutputWidgetCommandId(), pv);
                    controller_layout.addView(v); //Run On UI Thread
                    viewList.add(v);
                }

                //find & apply the height needed to wrap the content
                new Handler(Looper.getMainLooper()).post(() -> {
                    int max = 0, temp;
                    for (View vv: viewList) {
                        temp = (int) (vv.getY() + vv.getBottom());
                        if(temp > max) max = temp;
                    }

                    controller_layout.getLayoutParams().height = (int) (max+ 32 *App.density_px);
                    controller_layout.requestLayout();
                });
            });
        }).start();
    }

    private final BaseConnection.IncomingCommandCallback incomingCommandsCallback = command -> {
        ProtoView pv = this.outputWidgetsMap.get(command.getIdentifier());
        if(pv != null) pv.execReceiveCommand(command.getData());
    };


    private final ProtoView.OnCommandExecutedListener commandExecutedListener = command -> {
        //this is how to make your events shared between all views
        command_txt.setText(command.toString());
        if (connection != null ) connection.write(command.csv());
    };

    private final OnBackPressedCallback backPressCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            saveState();
            navController.navigateUp();
        }
    };

    //save a list of views that need to save their state, then loop it at the end
    List<ProtoView> viewsWithStateToSave = new LinkedList<>();
    private void saveState() {
        for (ProtoView pv: viewsWithStateToSave) {
            attrsRepo.insert(pv.saveAndGetCurrentState());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /* if coming back from guiEditorFragment, then need to refresh View list from DB*/
        if(!justCreated) loadUI();
        else justCreated = false; //for first time ignore
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (connection != null) connection.unregisterFromCallbacks();
    }
}

