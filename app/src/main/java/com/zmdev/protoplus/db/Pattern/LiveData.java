package com.zmdev.protoplus.db.Pattern;

import android.os.Handler;
import android.os.Looper;

import java.util.LinkedList;
import java.util.List;

public class LiveData <T> {

    public enum LiveDataState {ON_ATTACH, INSERT, UPDATE, DELETE}
    private final List<Observer<T>> observers = new LinkedList<>();
    private T currentData;

    public void notifyObservers(T newData, LiveDataState state){
        currentData = newData;
        //Observers are generally views so make sure you notify on UI thread
        new Handler(Looper.getMainLooper()).post(() -> {
            for (Observer<T> o : observers) {
                o.onUpdate(currentData, state);
            }
        });
    }

    public void attachObserver(Observer<T> observer, T currentData) {
        this.currentData = currentData;
        this.observers.add(observer);
        new Handler(Looper.getMainLooper()).post(() -> {
            observer.onUpdate(currentData, LiveDataState.ON_ATTACH);
        });
    }

    public void detachObserver(Observer<T> observer) {
        this.observers.remove(observer);
    }
}
