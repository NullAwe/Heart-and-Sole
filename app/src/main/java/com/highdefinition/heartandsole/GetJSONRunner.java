package com.highdefinition.heartandsole;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class GetJSONRunner {

    private final Executor executor = Executors.newSingleThreadExecutor();

    public JSONObject executeAsync(Callable<String> callable, Context context) {
        final AtomicReference<JSONObject> json = new AtomicReference<>();
        executor.execute(() -> {
            try {
                json.set(readJsonFromUrl(callable.call(), context));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        long millis = System.currentTimeMillis();
        while (json.get() == null && System.currentTimeMillis() - millis < 5000) {
            Log.i("GetJSONRunner", "waiting for response");
        }
        return json.get();
    }

    private static JSONObject readJsonFromUrl(String url, Context context) throws IOException,
                                                                         JSONException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestProperty("X-Android-Package", context.getPackageName());
        con.setRequestProperty("X-Android-Cert",
                APIRequestHelper.getSignature(context.getPackageManager(),
                        context.getPackageName()));

        try (InputStream is = con.getInputStream()) {
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