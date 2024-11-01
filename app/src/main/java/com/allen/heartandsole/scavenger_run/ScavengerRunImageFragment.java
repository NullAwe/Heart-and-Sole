package com.allen.heartandsole.scavenger_run;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.allen.heartandsole.APIRequestHelper;
import com.allen.heartandsole.GetDirectionsJSON;
import com.allen.heartandsole.GetNearbyPOIs;
import com.allen.heartandsole.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;

import java.util.Locale;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ScavengerRunImageFragment extends Fragment implements OnMapReadyCallback {

    private final String apiKey;

    private Activity activity;
    private Context context;
    private View view;

    private FusedLocationProviderClient locProv;
    private LatLng curPos, dest;
    private int ind = -1;
    private String url;
    private boolean changed = true;
    private GetNearbyPOIs getNearbyPOIs;

    private float mins = -1, mils = -1;
    private Bitmap bitmap = null;

    public ScavengerRunImageFragment(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scavenger_run_image, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = requireActivity();
        context = requireContext();
        this.view = view;
        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.rec_map);
        if (mapFrag != null) mapFrag.getMapAsync(this);
        locProv = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) return;
        gMap.setMyLocationEnabled(true);
        locProv.getLastLocation().addOnSuccessListener(activity, loc -> {
            if (loc == null) return;
            try {
                curPos = new LatLng(loc.getLatitude(), loc.getLongitude());
                if (url == null) return;
                if (changed) {
                    changed = false;
                    getNearbyPOIs = new GetNearbyPOIs(url, apiKey, context);
                }
                if (getNearbyPOIs == null) return;
                if (ind == -1) ind = (int) (Math.random() * getNearbyPOIs.getImages().size());
                if (getNearbyPOIs.getImages().size() == 0) return;
                ImageView scavImage = view.findViewById(R.id.scav_image);
                if (bitmap == null) {
                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(chain -> {
                                Request newRequest = chain.request().newBuilder()
                                        .addHeader("X-Android-Package", context.getPackageName())
                                        .addHeader("X-Android-Cert",
                                                Objects.requireNonNull(APIRequestHelper
                                                        .getSignature(context.getPackageManager(),
                                                                context.getPackageName())))
                                        .build();
                                return chain.proceed(newRequest);
                            })
                            .build();
                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bm, Picasso.LoadedFrom from) {
                            bitmap = bm;
                            scavImage.setImageBitmap(bm);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {}
                    };
                    Picasso picasso = new Picasso.Builder(context)
                            .downloader(new OkHttp3Downloader(client))
                            .build();
                    picasso.load(getNearbyPOIs.getImages().get(ind)).into(target);
                } else {
                    scavImage.setImageBitmap(bitmap);
                }
                dest = getNearbyPOIs.getNearbyPOIs().get(ind);
                if (mins < 0 || mils < 0) {
                    GetDirectionsJSON dirs = new GetDirectionsJSON(getDirUrl(curPos, dest), context);
                    mins = dirs.getMinutes();
                    mils = dirs.getMiles();
                }
                ((TextView) view.findViewById(R.id.walking_time)).setText(
                        String.format(Locale.getDefault(), "Expected walking time " +
                                        "(from source): %.2f min", mins));
                ((TextView) view.findViewById(R.id.walking_dist)).setText(
                        String.format(Locale.getDefault(), "Walking dist (from source): " +
                                        "%.2f mi", mils));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public LatLng getDest() {
        return dest;
    }

    public void setGetNearbyPOIs(LatLng location, int radiusMeters, String type) {
        if (type == null || type.length() == 0) {
            url = getPOIUrl(location, radiusMeters);
        } else {
            url = getPOIUrl(location, radiusMeters, type);
        }
        changed = true;
    }

    private String getPOIUrl(LatLng location, int radius) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.latitude + "," + location.longitude + "&radius=" + radius + "&key=" +
                apiKey;
    }

    private String getPOIUrl(LatLng location, int radius, String type) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.latitude + "," + location.longitude + "&radius=" + radius + "&type=" +
                type + "&key=" + apiKey;
    }

    private String getDirUrl(LatLng location, LatLng dest) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                location.latitude + "," + location.longitude + "&destination=" +
                dest.latitude + "," + dest.longitude + "&key=" + apiKey + "&mode=walking";
    }
}
