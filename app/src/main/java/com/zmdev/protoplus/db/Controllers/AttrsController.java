package com.zmdev.protoplus.db.Controllers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.lifecycle.LiveData;

import com.zmdev.protoplus.CustomViews.ProtoView;
import com.zmdev.protoplus.db.Entities.AttrsAndCommand;
import com.zmdev.protoplus.db.Entities.ProtoViewAttrs;
import com.zmdev.protoplus.db.AppDatabase;
import com.zmdev.protoplus.db.Daos.AttrsDao;
import java.util.List;

public class AttrsController {

    AttrsDao dao;
    private static final Object mutex = new Object() ;
    public AttrsController(Context context) {
        dao = AppDatabase.getInstance(context).attrsDAO();
    }

    public void insert(ProtoViewAttrs attrs) {
        new Thread(() -> {
            synchronized (mutex){
                dao.insert(attrs);
            }
        }).start();
    }

    public void insertAll(List<View> viewList, Runnable onFinish) {
        new Thread(() -> {
            synchronized (mutex){
                for (View v: viewList) dao.insert(((ProtoView) v).getAttrs());
                new Handler(Looper.getMainLooper()).post(onFinish);
            }
        }).start();
    }

    public void update(ProtoViewAttrs attrs) {
        new Thread(() -> {
            synchronized (mutex){
                dao.update(attrs);
            }
        }).start();
    }

    public void delete(ProtoViewAttrs attrs) {
        new Thread(() -> {
            synchronized (mutex){
                dao.relink(attrs.getId());
                dao.delete(attrs);
            }
        }).start();
    }

    public void deleteList(List<ProtoViewAttrs> attrs) {
        new Thread(() -> {
            synchronized (mutex){
                for (ProtoViewAttrs pa: attrs) dao.delete(pa);
            }
        }).start();
    }

    public LiveData<List<AttrsAndCommand>> getAttrsAndCommands(int ProjectID) {
        return dao.getAttrsAndCommands(ProjectID);
    }

    public List<AttrsAndCommand> getAttrsAndCommandsList(int ProjectID) {
        return dao.getAttrsAndCommandsList(ProjectID);
    }


}
