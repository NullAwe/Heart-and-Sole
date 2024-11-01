package com.allen.heartandsole.solely_for_you;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.List;

public class SolelyForYouMapFragment extends Fragment implements OnMapReadyCallback {

    private Activity activity;
    private Context context;
    private View view;

    private GoogleMap map;
    private FusedLocationProviderClient locProv;
    private LatLng lastPos, dest;
    private Polyline cur;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solely_for_you_map, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = requireActivity();
        context = requireContext();
        this.view = view;
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
            public void onLocationResult(@NonNull LocationResult lr) {
                List<Location> locations = lr.getLocations();
                if (locations.isEmpty()) return;
                Location loc = locations.get(locations.size() - 1);
                LatLng curPos = new LatLng(loc.getLatitude(), loc.getLongitude());
                if (lastPos == null) lastPos = curPos;
                Polyline line = map.addPolyline(new PolylineOptions().add(lastPos, curPos));
                stylePolyline(line);
                lastPos = curPos;
                if (distance(curPos, dest) < 0.05) {
                    view.findViewById(R.id.done).setVisibility(View.VISIBLE);
                }
            }
        };
        locProv.getLastLocation().addOnSuccessListener(activity, loc -> {
            if (loc == null) return;
            lastPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPos, 15.0f));
            if (cur != null) {
                PolylineOptions options = new PolylineOptions();
                for (LatLng point : cur.getPoints()) options.add(point);
                cur = map.addPolyline(options);
                dest = cur.getPoints().get(cur.getPoints().size() - 1);
                stylePathPolyline(cur);
            }
        });
        locProv.requestLocationUpdates(lr, lc, Looper.myLooper());
    }

    public void setCur(Polyline polyline) {
        cur = polyline;
    }

    private static void stylePathPolyline(Polyline polyline) {
        polyline.setColor(0xff00aaff);
        polyline.setWidth(20);
        polyline.setJointType(JointType.ROUND);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
    }

    private static void stylePolyline(Polyline polyline) {
        polyline.setColor(0xff000000);
        polyline.setWidth(12);
        polyline.setJointType(JointType.ROUND);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
    }

    private static float distance(LatLng l1, LatLng l2) {
        double lat1 = Math.toRadians(l1.latitude),
                lat2 = Math.toRadians(l2.latitude),
                lng1 = Math.toRadians(l1.longitude),
                lng2 = Math.toRadians(l2.longitude),
                dlat = lat2 - lat1, dlon = lng2 - lng1,
                a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2)
                        * Math.pow(Math.sin(dlon / 2), 2),
                c = 2 * Math.asin(Math.sqrt(a)),
                r = 3956;
        return (float) (c * r);
    }
}
