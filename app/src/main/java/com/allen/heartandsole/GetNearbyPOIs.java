package com.allen.heartandsole;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetNearbyPOIs {

    private final List<LatLng> places;
    private final List<String> images;

    public GetNearbyPOIs(String url, String apiKey, Context context) throws JSONException {
        JSONObject json = new GetJSONRunner().executeAsync(() -> url, context);
        places = new ArrayList<>();
        images = new ArrayList<>();
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
                    Log.i("GetNearbyPOIs", "no photos for " + i);
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