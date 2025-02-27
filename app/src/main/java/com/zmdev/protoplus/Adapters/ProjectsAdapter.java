package com.zmdev.protoplus.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import com.zmdev.protoplus.R;
import com.zmdev.protoplus.Utils.TutorialFlow;
import com.zmdev.protoplus.db.Entities.Project;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {

    private List<Project> projects = new ArrayList<>();
    private NavController navController;
    private Activity tutoActivity;
    private OnProjectClicked clickListener;

    public void setTutoActivity(Activity tutoActivity) {
        this.tutoActivity = tutoActivity;
    }

    public void setNavController(NavController navController) {
        this.navController = navController;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProjectViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project current = projects.get(position);
        holder.title.setText(current.getName());
        holder.gui_btn.setOnClickListener(v -> {
            //set selected project in view model and navigate to gui controller page
            clickListener.onClicked(current);
            navController.navigate(R.id.open_gui_controller_action);
        });

        holder.commands_btn.setOnClickListener(v -> {
            //set selected project in view model and navigate to commands page
            clickListener.onClicked(current);
            navController.navigate(R.id.show_project_commands_action);
        });

        //this will only work on first use
        if (position == 0) //make sure its only for first element
        TutorialFlow.showFlowFor(tutoActivity,
                new View[]{holder.gui_btn, holder.commands_btn},
                new String[]{
                        "Create, access and edit your Graphical interface from here",
                        "Create and manage your custom commands from here"
                }, "4");
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }


    public class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        MaterialButton commands_btn;
        MaterialButton gui_btn;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.project_title_txtvw);
            commands_btn = itemView.findViewById(R.id.project_commands_btn);
            gui_btn = itemView.findViewById(R.id.project_gui_btn);
        }
    }

    public void setOnProjectClickedListener(OnProjectClicked listener) {
        this. clickListener = listener;
    }
    public interface OnProjectClicked{
        void onClicked(Project project);
    }
}
