package com.allen.heartandsole.scavenger_run;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.allen.heartandsole.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class ScavengerRunMainFragment extends Fragment implements OnMapReadyCallback {

    private Activity activity;
    private Context context;
    private View view;

    private GoogleMap map;
    private FusedLocationProviderClient locProv;
    private LatLng curPos;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scavenger_run_main, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        this.view = view;
        activity = requireActivity();
        context = requireContext();
        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) mapFrag.getMapAsync(this);
        locProv = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        this.map = gMap;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) return;
        map.setMyLocationEnabled(true);
        LocationRequest lr = LocationRequest.create().setInterval(1000).setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationCallback lc = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult lr) {}
        };
        locProv.getLastLocation().addOnSuccessListener(activity, loc -> {
            if (loc == null) return;
            curPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 15.0f));
        });
        locProv.requestLocationUpdates(lr, lc, Looper.myLooper());
    }

    public String getType() {
        String type = "";
        RadioGroup group = ((RadioGroup) view.findViewById(R.id.radio_type));
        int children = group.getChildCount();
        for (int i = 0; i < children - 1; i++) {
            RadioButton child = (RadioButton) group.getChildAt(i);
            if (child.isChecked()) {
                type = child.getText().toString().toLowerCase();
                break;
            }
        }
        return type;
    }

    public int getRadius() {
        return 1500;
    }

    public LatLng getLocation() {
        long millis = System.currentTimeMillis();
        while (curPos == null && millis + 5000 > System.currentTimeMillis()) {
            Log.i("ScavengerRunMainFragment", "waiting for location");
        }
        return curPos;
    }
}
