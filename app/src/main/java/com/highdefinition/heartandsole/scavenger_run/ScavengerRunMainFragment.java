package com.highdefinition.heartandsole.scavenger_run;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
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

import com.highdefinition.heartandsole.R;
import com.highdefinition.heartandsole.SharedPrefSingleton;
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
import com.google.android.material.slider.Slider;

public class ScavengerRunMainFragment extends Fragment implements OnMapReadyCallback {

    private static final float[] DISTS = {0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 4.0f, 5.0f};

    private Activity activity;
    private Context context;
    private View view;

    private GoogleMap map;
    private FusedLocationProviderClient locProv;
    private LatLng curPos;

    private int sliderValue = 1;

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
        RadioGroup group = ((RadioGroup) view.findViewById(R.id.dest_type));
        String destType = SharedPrefSingleton.getInstance().getDestType();
        int children = group.getChildCount();
        for (int i = 0; i < children; i++) {
            RadioButton button = (RadioButton) group.getChildAt(i);
            if (button.getText().toString().equals(destType)) {
                button.toggle();
                break;
            }
        }
        group.setOnCheckedChangeListener((rGroup, id) -> {
            int c = group.getChildCount();
            for (int i = 0; i < c; i++) {
                RadioButton button = (RadioButton) rGroup.getChildAt(i);
                if (button.isChecked()) {
                    SharedPrefSingleton.getInstance().setDestType(button.getText().toString());
                    break;
                }
            }
        });

        Slider distSlider = view.findViewById(R.id.dist_slider);
        distSlider.setValue(sliderValue);
        distSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                sliderValue = Math.round(slider.getValue());
                String text = "Current Distance: " + getRadius(sliderValue);
                ((TextView) view.findViewById(R.id.cur_radius)).setText(text);
            }
        });
        String text = "Current Distance: " + getRadius(sliderValue);
        ((TextView) view.findViewById(R.id.cur_radius)).setText(text);
        distSlider.setLabelFormatter(ScavengerRunMainFragment::getRadius);
    }

    private static String getRadius(float val) {
        return DISTS[Math.round(val)] + " mi";
    }

    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        this.map = gMap;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            return;
        }
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
        RadioGroup group = ((RadioGroup) view.findViewById(R.id.dest_type));
        int children = group.getChildCount();
        for (int i = 0; i < children - 1; i++) {
            RadioButton child = (RadioButton) group.getChildAt(i);
            if (child.isChecked()) {
                type = child.getText().toString();
                SharedPrefSingleton.getInstance().setDestType(type);
                break;
            }
        }
        return type.toLowerCase();
    }

    public int getRadius() {
        return (int) (DISTS[sliderValue] * 1609);
    }

    public LatLng getLocation() {
        long millis = System.currentTimeMillis();
        while (curPos == null && millis + 5000 > System.currentTimeMillis()) {
            Log.i("ScavengerRunMainFragment", "waiting for location");
        }
        return curPos;
    }
}
