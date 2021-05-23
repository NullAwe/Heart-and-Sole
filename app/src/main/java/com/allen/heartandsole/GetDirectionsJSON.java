package com.allen.heartandsole;

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

public class GetDirectionsJSON {

    private final List<LatLng> route;
    private int time;

    public GetDirectionsJSON(String url) throws JSONException {
        time = 0;
        JSONObject json = new GetDirectionRunner().executeAsync(() -> url);
        if (json == null) route = new ArrayList<>();
        else {
            JSONObject routes = json.getJSONArray("routes").getJSONObject(0);
            JSONObject polyline = routes.getJSONObject("overview_polyline");
            JSONArray legs = routes.getJSONArray("legs");
            for (int i = 0, n = legs.length(); i < n; i++) {
                time += legs.getJSONObject(i).getJSONObject("duration").getInt("value") / 60;
            }
            route = PolyUtil.decode(polyline.getString("points"));

        }
    }

    public List<LatLng> getDirections() {
        return route;
    }

    public int getMinutes() {
        return time;
    }

    private static class GetDirectionRunner {

        private final Executor executor = Executors.newSingleThreadExecutor();

        public JSONObject executeAsync(Callable<String> callable) {
            final AtomicReference<JSONObject> json = new AtomicReference<>();
            executor.execute(() -> {
                try {
                    json.set(readJsonFromUrl(callable.call()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            long millis = System.currentTimeMillis();
            while (json.get() == null && System.currentTimeMillis() - millis < 5000);
            return json.get();
        }

        private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
            try (InputStream is = new URL(url).openStream()) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                String jsonText = readAll(rd);
                return new JSONObject(jsonText);
            }
        }

        private static String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) sb.append((char) cp);
            return sb.toString();
        }
    }
}