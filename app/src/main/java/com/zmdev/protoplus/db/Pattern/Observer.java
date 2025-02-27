package com.zmdev.protoplus.db.Pattern;

public abstract class Observer <T> {
    public abstract void onUpdate(T t, LiveData.LiveDataState state);
}
