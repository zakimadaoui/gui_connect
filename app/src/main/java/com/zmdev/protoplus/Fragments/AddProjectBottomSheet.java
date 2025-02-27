package com.zmdev.protoplus.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.ThemeUtils;
import com.zmdev.protoplus.db.Controllers.ProjectsController;
import com.zmdev.protoplus.db.Entities.Project;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zmdev.protoplus.db.Pattern.LiveData;
import com.zmdev.protoplus.db.Pattern.Observer;

import java.util.List;

public class AddProjectBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "AddProjectFragment";
    private ProjectsController projectsController;
    private boolean action_completed = false;
    private NavController navController;
    private ProgressDialog dialog;
    private boolean inEditMode = false;
    private Project project; //project to edit in EditMode
    private EditText title_edit;
    private TextView save_button;
    private TextView projects_sheet_title;
    private Observer<List<Project>> mObserver = new Observer<List<Project>>() {
        @Override
        public void onUpdate(List<Project> projects, LiveData.LiveDataState state) {
            if (state == LiveData.LiveDataState.ON_ATTACH) {
                projectsController.setProjects(projects);
                if (action_completed) {
                    navigateToNewPage(projects.get(projects.size() - 1));
                    action_completed = false;
                }
            }
        }
    };

    public void setInEditMode(Project project) {
        this.project = project;
        this.inEditMode = true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, ThemeUtils.bottomSheetThemeID);
        projectsController = new ProjectsController(getContext());
        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Adding Project ...");
        dialog.setCancelable(false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        projectsController.attachObserver(mObserver);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.page_add_project, container,false);
        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        save_button = view.findViewById(R.id.add_project_btn);
        projects_sheet_title = view.findViewById(R.id.projects_sheet_title);
        title_edit = view.findViewById(R.id.project_title_edit);

        if (inEditMode){
            projects_sheet_title.setText(R.string.edit_project_name);
            save_button.setText(R.string.edit);
            title_edit.setText(project.getName());
        }

        //Save button
        save_button.setOnClickListener(v -> {
            String title = title_edit.getText().toString();
            if (title.trim().isEmpty()) {
                title_edit.setError("Required field !");
            } else {
                action_completed = true;
                if (inEditMode) {
                    project.setName(title);
                    projectsController.update(project);
                } else {
                    projectsController.insert(new Project(title));
                }
                dismiss();
            }
        });
        return view;
    }

    private void navigateToNewPage(Project project) {
        dialog.dismiss();
        projectsController.setSelectedProject(project);
        navController.navigate(R.id.new_project_action);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        projectsController.detachObserver(mObserver);
    }
}
