package com.allen.heartandsole;

import com.google.android.gms.maps.model.Polyline;

@SuppressWarnings("unused")
public class Event {

    private final Polyline path;

    public Event(Polyline path) {
        this.path = path;
    }

    public Polyline getPath() {
        return path;
    }
}
