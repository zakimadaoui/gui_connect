package com.zmdev.protoplus.db.Controllers;

import android.content.Context;
import android.util.Log;

import com.zmdev.protoplus.db.AppDatabase;
import com.zmdev.protoplus.db.Daos.AttrsDao;
import com.zmdev.protoplus.db.Daos.CommandDao;
import com.zmdev.protoplus.db.Daos.ProjectDao;
import com.zmdev.protoplus.db.Entities.Project;
import com.zmdev.protoplus.db.Pattern.LiveData;
import com.zmdev.protoplus.db.Pattern.Observer;
import java.util.ArrayList;
import java.util.List;

public class ProjectsController {

    private static final String TAG = "ProjectsController";

    private static List<Project> projects ;
    private static Project selectedProject = null;
    private final ProjectDao dao;
    private final CommandDao commandDao;
    private final AttrsDao attrsDao;
    private final static Object mutex = new Object();
    private static LiveData<List<Project>> projectsLiveData;

    public ProjectsController(Context context) {
        dao = AppDatabase.getInstance(context).projectDAO();
        commandDao = AppDatabase.getInstance(context).commandDAO();
        attrsDao = AppDatabase.getInstance(context).attrsDAO();
        if(projectsLiveData == null) projectsLiveData = new LiveData<>();
    }

    public void attachObserver(Observer<List<Project>> observer){

        new Thread(() -> {
            synchronized (mutex){
                projects = dao.getProjects();
                projectsLiveData.attachObserver(observer, projects);
            }
        }).start();
    }

    public void detachObserver(Observer<List<Project>> observer){
        projectsLiveData.detachObserver(observer);
    }

    public void insert(Project project) {
        new Thread(() -> {
            synchronized (mutex){
                dao.insert(project);
                projects = dao.getProjects();
                projectsLiveData.notifyObservers(projects, LiveData.LiveDataState.INSERT);
            }
        }).start();
    }

    public void update(Project project) {
        new Thread(() -> {
            synchronized (mutex){
                dao.update(project);
                projects = dao.getProjects();
                projectsLiveData.notifyObservers(projects, LiveData.LiveDataState.UPDATE);
            }
        }).start();
    }

    public void delete(Project project) {
        new Thread(() -> {
            synchronized (mutex){
                commandDao.delete(project.getId());
                attrsDao.delete(project.getId());
                dao.delete(project);
                projects = dao.getProjects();
                projectsLiveData.notifyObservers(projects, LiveData.LiveDataState.DELETE);
            }
        }).start();
    }

    public List<Project> getProjects() {
        if (projects == null) return new ArrayList<>();
        else return projects;
    }

    public void setProjects(List<Project> projects) {
        ProjectsController.projects = projects;
    }

    public void setSelectedProject(Project selectedProject) {
        ProjectsController.selectedProject = selectedProject;
    }

    // Only this needs to be static since some classes need to get access the
    // the current project name and/or ID
    public static Project getSelectedProject() {
        return selectedProject;
    }
}
