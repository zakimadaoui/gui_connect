
package com.zmdev.protoplus.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.zmdev.protoplus.Adapters.ProjectsAdapter;
import com.zmdev.protoplus.Adapters.SwipeToDelete;
import com.zmdev.protoplus.Adapters.SwipeToEdit;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.TutorialFlow;
import com.zmdev.protoplus.db.Controllers.ProjectsController;
import com.zmdev.protoplus.db.Entities.Project;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zmdev.protoplus.db.Pattern.LiveData;
import com.zmdev.protoplus.db.Pattern.Observer;

import java.util.List;

public class ProjectsFragment extends Fragment {

    private static final String TAG = "ProjectsFragment";
    private Context mContext;
    private ProjectsAdapter adapter;
    private ProjectsController controller;
    private NavController navController;
    private List<Project> projects;
    private View layout;
    private int lastSwipedPosition = -1;
    private int projectsNbr = 0;

    private Observer<List<Project>> mObserver = new Observer<>() {
        @Override
        public void onUpdate(List<Project> projects, LiveData.LiveDataState state) {

            controller.setProjects(projects);
            ProjectsFragment.this.projects = projects;
            adapter.setProjects(projects);
            projectsNbr = projects.size();

            switch (state) {
                case ON_ATTACH:
                    adapter.notifyDataSetChanged();
                    break;
                case INSERT:
                    adapter.notifyItemInserted(projects.size() - 1);
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
        controller = new ProjectsController(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (layout == null) {
            layout = inflater.inflate(R.layout.page_projects, container, false);
            navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            adapter = new ProjectsAdapter();
            adapter.setNavController(navController);
            adapter.setTutoActivity(getActivity());

            adapter.setOnProjectClickedListener(project -> {
                controller.setSelectedProject(project);
            });

            //attach observer after creation of adapter, as observer callback needs a non null adapter
            controller.attachObserver(mObserver);
            mObserver = null;


            RecyclerView recycler = layout.findViewById(R.id.projects_recycler);
            recycler.setLayoutManager(new LinearLayoutManager(mContext));
            recycler.setAdapter(adapter);

            //Swipe to delete
            new ItemTouchHelper(new SwipeToDelete(position -> {
                lastSwipedPosition = position;
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.delete_project_title)
                        .setMessage(R.string.delete_project_text)
                        .setPositiveButton(R.string.yes, (dialog, which) ->{
                            controller.delete(projects.get(position));
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> adapter.notifyItemChanged(position))
                        .create()
                        .show();
            }, mContext)).attachToRecyclerView(recycler);

            //Swipe to edit
            new ItemTouchHelper(new SwipeToEdit(position -> {
                lastSwipedPosition = position;
                AddProjectBottomSheet bottomSheet = new AddProjectBottomSheet();
                bottomSheet.setInEditMode(projects.get(position));
                bottomSheet.show(getParentFragmentManager(), "");
                adapter.notifyItemChanged(position);
            }, mContext)).attachToRecyclerView(recycler);
        }

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.home_btn).setOnClickListener(v -> {
            navController.navigateUp();
        });
        FloatingActionButton fab = view.findViewById(R.id.add_new_project_fab2);
        fab.setOnClickListener(v -> navController.navigate(R.id.new_project_action));
        //show tutorial at first use
        TutorialFlow.showFlowFor(getActivity(), fab, getString(R.string.tuto_add_project), "1");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        controller.detachObserver(mObserver);
    }
}
