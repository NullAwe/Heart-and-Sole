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

public class SolelyForYouMainFragment extends Fragment implements OnMapReadyCallback {

    private Activity activity;
    private Context context;

    private GoogleMap map;
    private FusedLocationProviderClient locProv;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solely_for_you_main, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = requireActivity();
        context = requireContext();
        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.rec_map);
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
        LocationCallback lc = new LocationCallback() {};
        locProv.getLastLocation().addOnSuccessListener(activity, loc -> {
            if (loc == null) return;
            LatLng curPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 15.0f));
        });
        locProv.requestLocationUpdates(lr, lc, Looper.myLooper());
    }
}
