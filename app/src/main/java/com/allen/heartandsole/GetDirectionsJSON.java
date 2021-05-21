package com.allen.heartandsole;

import android.os.Handler;
import android.os.Looper;

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

    private final String url;

    public GetDirectionsJSON(String url) {
        this.url = url;
    }

    public List<LatLng> getDirections() throws JSONException {
        JSONObject json = new GetDirectionRunner().executeAsync(() -> url);
        JSONArray routes = json.getJSONArray("routes");
        JSONObject obj = routes.getJSONObject(0);
        JSONObject polyline = obj.getJSONObject("overview_polyline");
        return PolyUtil.decode(polyline.getString("points"));
    }

    private static List<LatLng> decodePoints(String encoded) {
        int index = 0, lat = 0, lng = 0;
        List<com.google.android.gms.maps.model.LatLng> points = new ArrayList<>();
        while (index < encoded.length()) {
            int shift = 0, result = 0;
            while (true) {
                int b = encoded.charAt(index++) - '?';
                result |= ((b & 31) << shift);
                shift += 5;
                if (b < 32) break;
            }
            lat += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
            shift = 0;
            result = 0;
            while (true) {
                int b = encoded.charAt(index++) - '?';
                result |= ((b & 31) << shift);
                shift += 5;
                if (b < 32) break;
            }
            lng += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
            points.add(new com.google.android.gms.maps.model.LatLng(lat * 10,lng * 10));
        }
        return points;
    }

    private static class GetDirectionRunner {

        private final Executor executor = Executors.newSingleThreadExecutor();
        private final Handler handler = new Handler(Looper.getMainLooper());

        public JSONObject executeAsync(Callable<String> callable) {
            final AtomicReference<JSONObject> json = new AtomicReference<>();
            executor.execute(() -> {
                try {
                    json.set(readJsonFromUrl(callable.call()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            while (json.get() == null);
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