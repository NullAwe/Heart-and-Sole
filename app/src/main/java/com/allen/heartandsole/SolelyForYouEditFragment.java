package com.allen.heartandsole;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.Slider;

import java.util.HashMap;
import java.util.Map;

public class SolelyForYouEditFragment extends Fragment implements OnMapReadyCallback {

    private static final Map<Integer, Integer> timeMap = new HashMap<Integer, Integer>() {{
        put(0, 5);
        put(1, 10);
        put(2, 15);
        put(3, 30);
        put(4, 45);
        put(5, 60);
        put(6, 90);
        put(7, 120);
    }};

    private Activity activity;
    private Context context;
    private View view;

    private GoogleMap map;
    private FusedLocationProviderClient locProv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solely_for_you_edit, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = requireActivity();
        context = requireContext();
        this.view = requireView();
        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.rec_map);
        if (mapFrag != null) mapFrag.getMapAsync(this);
        locProv = LocationServices.getFusedLocationProviderClient(context);
        Slider timeSlider = this.view.findViewById(R.id.time_slider);
        timeSlider.setValue(3.0f); // test
        timeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                String text = "Current Time: " + getTime(slider.getValue());
                ((TextView) view.findViewById(R.id.cur_time)).setText(text);
            }
        });
        timeSlider.setLabelFormatter(SolelyForYouEditFragment::getTime);
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
        LocationCallback lc = new LocationCallback() {};
        locProv.getLastLocation().addOnSuccessListener(activity, loc -> {
            if (loc == null) return;
            LatLng curPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 15.0f));
        });
        locProv.requestLocationUpdates(lr, lc, Looper.myLooper());
    }

    private static String getTime(float value) {
        int mins = timeMap.get(Math.round(value));
        if (mins < 60) return mins + " min";
        else if (mins % 60 == 0) return mins / 60 + " hr";
        return mins / 60 + " hr " + mins % 60 + " min";
    }
}
