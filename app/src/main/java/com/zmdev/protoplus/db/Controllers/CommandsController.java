package com.zmdev.protoplus.db.Controllers;

import android.content.Context;
import com.zmdev.protoplus.db.AppDatabase;
import com.zmdev.protoplus.db.Daos.AttrsDao;
import com.zmdev.protoplus.db.Daos.CommandDao;
import com.zmdev.protoplus.db.Entities.Command;
import com.zmdev.protoplus.db.Pattern.LiveData;
import com.zmdev.protoplus.db.Pattern.Observer;
import java.util.List;

public class CommandsController {

    private static final String TAG = "CommandsController";

    private final CommandDao dao;
    private final AttrsDao attrsDAO;
    private List<Command> commands ;
    private final static Object mutex = new Object();
    private static LiveData<List<Command>> commandsLiveData;
    private final int currentProjectId;

    public CommandsController(Context context) {
        dao = AppDatabase.getInstance(context).commandDAO();
        attrsDAO = AppDatabase.getInstance(context).attrsDAO();
        if(commandsLiveData == null) commandsLiveData = new LiveData<>();
        currentProjectId = ProjectsController.getSelectedProject().getId();
    }

    public void attachObserver(Observer<List<Command>> observer){

        new Thread(() -> {
            synchronized (mutex){
                commands = dao.getCommands(currentProjectId);
                commandsLiveData.attachObserver(observer, commands);
            }
        }).start();
    }

    public void detachObserver(Observer<List<Command>> observer){
        commandsLiveData.detachObserver(observer);
    }

    public void insert(Command command) {
        new Thread(() -> {
            synchronized (mutex){
                dao.insert(command);
                commands = dao.getCommands(currentProjectId);
                commandsLiveData.notifyObservers(commands, LiveData.LiveDataState.INSERT);
            }
        }).start();
    }

    public void update(Command command) {
        new Thread(() -> {
            synchronized (mutex){
//                attrsDAO.relink(command.getId());
                dao.update(command);
                commands = dao.getCommands(currentProjectId);
                commandsLiveData.notifyObservers(commands, LiveData.LiveDataState.UPDATE);
            }
        }).start();
    }

   public void updateAndRelink(Command command) {
        new Thread(() -> {
            synchronized (mutex){
                attrsDAO.relink(command.getId());
                dao.update(command);
                commands = dao.getCommands(currentProjectId);
                commandsLiveData.notifyObservers(commands, LiveData.LiveDataState.UPDATE);
            }
        }).start();
    }


    public void delete(Command command) {
        new Thread(() -> {
            synchronized (mutex){
                attrsDAO.relink(command.getId());
                dao.delete(command);
                commands = dao.getCommands(currentProjectId);
                commandsLiveData.notifyObservers(commands, LiveData.LiveDataState.DELETE);
            }
        }).start();
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }
}
