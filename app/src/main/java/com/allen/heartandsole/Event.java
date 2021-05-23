package com.allen.heartandsole;

import com.google.android.gms.maps.model.Polyline;

public class Event {

    private final Polyline path;

    public Event(Polyline path) {
        this.path = path;
    }

    public Polyline getPath() {
        return path;
    }
}
