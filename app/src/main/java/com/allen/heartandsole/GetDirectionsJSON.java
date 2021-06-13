package com.allen.heartandsole;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetDirectionsJSON {

    private final List<LatLng> route;
    private float time;
    private float distance;

    public GetDirectionsJSON(String url, Context context) throws JSONException {
        time = distance = 0;
        JSONObject json = new GetJSONRunner().executeAsync(() -> url, context);
        if (json == null) route = new ArrayList<>();
        else {
            JSONObject routes = json.getJSONArray("routes").getJSONObject(0);
            JSONObject polyline = routes.getJSONObject("overview_polyline");
            JSONArray legs = routes.getJSONArray("legs");
            for (int i = 0, n = legs.length(); i < n; i++) {
                time += legs.getJSONObject(i).getJSONObject("duration").getInt("value") / 60.0;
                distance += legs.getJSONObject(i).getJSONObject("distance").getInt("value");
            }
            distance /= 1609;
            route = PolyUtil.decode(polyline.getString("points"));

        }
    }

    public List<LatLng> getDirections() {
        return route;
    }

    public float getMinutes() {
        return time;
    }

    public float getMiles() {
        return distance;
    }
}