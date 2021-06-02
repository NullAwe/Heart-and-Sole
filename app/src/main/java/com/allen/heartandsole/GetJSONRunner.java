package com.allen.heartandsole;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class GetJSONRunner {

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
        int ch;
        while ((ch = rd.read()) != -1) sb.append((char) ch);
        return sb.toString();
    }
}