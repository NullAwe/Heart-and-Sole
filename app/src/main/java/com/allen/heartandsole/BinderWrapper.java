package com.allen.heartandsole;

import android.os.Binder;

public class BinderWrapper<T> extends Binder {

    private final T t;

    public BinderWrapper(T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }
}
