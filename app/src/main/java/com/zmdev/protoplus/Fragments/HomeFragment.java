package com.zmdev.protoplus.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zmdev.protoplus.App;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.TutorialFlow;
import com.zmdev.protoplus.db.Entities.Parameter;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        CardView projects = view.findViewById(R.id.projects_btn);
        CardView terminal = view.findViewById(R.id.terminal_btn);
        CardView connections = view.findViewById(R.id.connect_btn);
        CardView guide = view.findViewById(R.id.guide_btn);
        FloatingActionButton tutos_fab = view.findViewById(R.id.home_tutos_fab);

        projects.setOnClickListener(v -> {
            navController.navigate(R.id.home_to_projects);
        });
        terminal.setOnClickListener(v -> {
            navController.navigate(R.id.home_to_terminal);
        });
        connections.setOnClickListener(v -> {
            navController.navigate(R.id.action_home_connect);
        });
        guide.setOnClickListener(v -> {
            navController.navigate(R.id.action_home_to_guide);
        });
        tutos_fab.setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://github.com/zakimadaoui/GuiConnectHelper")));
        });

        setCardWidth(projects);
        setCardWidth(terminal);
        setCardWidth(connections);
        setCardWidth(guide);

        TutorialFlow.showFlowFor(
                getActivity(),
                new View[]{projects, connections, terminal, guide, tutos_fab},
                new String[]{
                        getString(R.string.tuto_projects),
                        getString(R.string.tuto_connections),
                        getString(R.string.tuto_terminal),
                        getString(R.string.tuto_settings),
                        getString(R.string.tuto_lib_fab)
        }, "0");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Parameter[] parameterArr = {
                new Parameter("7", "hint1", 1, 1233),
                new Parameter("8", "hint2", 1, 1234) ,
                new Parameter("9", "hint3", 1, 1235) ,
                new Parameter("10", "hint4", 0, 1236)
        };

    }

    private void setCardWidth(CardView card) {
        card.getLayoutParams().width = (int) (App.screen_width * 0.5 * 0.7);
        card.getLayoutParams().height = (int) (App.screen_width * 0.5 * 0.7);
        card.requestLayout();
    }

}
