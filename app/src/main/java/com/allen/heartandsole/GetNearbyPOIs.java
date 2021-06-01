package com.allen.heartandsole;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class GetNearbyPOIs {

    private final List<LatLng> places;
    private final List<String> images;

    public GetNearbyPOIs(String url, String apiKey) throws JSONException {
        JSONObject json = new GetJSONRunner().executeAsync(() -> url);
        places = new ArrayList<>();
        images = new ArrayList<>();
        Log.i("allendebug", url);
        if (json != null) {
            JSONArray results = json.getJSONArray("results");
            int n = results.length();
            for (int i = 0; i < n; i++) {
                try {
                    JSONObject obj = results.getJSONObject(i);
                    JSONObject loc = obj.getJSONObject("geometry").getJSONObject("location");
                    places.add(new LatLng(loc.getDouble("lat"), loc.getDouble("lng")));
                    String photoRef = obj.getJSONArray("photos").getJSONObject(0)
                            .getString("photo_reference");
                    String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                            "photoreference=" + photoRef + "&sensor=false&maxheight=400&maxwidth" +
                            "=400&key=" + apiKey;
                    images.add(imageUrl);
                } catch (JSONException e) {
                    Log.i("allendebug", "no photos for " + i);
                    continue;
                }
            }
        }
    }

    public List<LatLng> getNearbyPOIs() {
        return places;
    }

    public List<String> getImages() {
        return images;
    }
}