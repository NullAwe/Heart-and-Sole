package com.highdefinition.heartandsole.solely_for_you;

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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.highdefinition.heartandsole.GetDirectionsJSON;
import com.highdefinition.heartandsole.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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
import java.util.Map;

public class SolelyForYouMainFragment extends Fragment implements OnMapReadyCallback {

    private Activity activity;
    private Context context;

    private GoogleMap map;
    private FusedLocationProviderClient locProv;

    private final Map<Integer, List<LatLng>> routes;
    private GetDirectionsJSON def;
    private double angle;
    private LatLng curPos;
    private Polyline cur;

    public SolelyForYouMainFragment(Map<Integer, List<LatLng>> routes) {
        this.routes = routes;
    }

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
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
            return;
        }
        map.setMyLocationEnabled(true);
        LocationRequest lr = LocationRequest.create().setInterval(1000).setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationCallback lc = new LocationCallback() {};
        locProv.getLastLocation().addOnSuccessListener(activity, loc -> {
            if (loc == null) return;
            curPos = new LatLng(loc.getLatitude(), loc.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 15.0f));
            if (cur != null) {
                cur.remove();
                PolylineOptions options = new PolylineOptions();
                for (LatLng point : cur.getPoints()) options.add(point);
                cur = map.addPolyline(options);
                stylePolyline(cur);
                return;
            }
            double dist = 0.01;
            angle = Math.random() * 2 * Math.PI;
            LatLng p1 = getPoint(curPos, angle, dist);
            try {
                def = new GetDirectionsJSON(getLink(curPos, p1), context);
                long millis = System.currentTimeMillis();
                while (millis + 5000 > System.currentTimeMillis() && def.getMinutes() < 0.1)
                    Log.i("SolelyForYouMainFragment", String.format("waiting for directions API, " +
                            "%d milliseconds left", 5000 - (System.currentTimeMillis() - millis)));
                changeRouteTo30();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        locProv.requestLocationUpdates(lr, lc, Looper.myLooper());
    }

    public GetDirectionsJSON getDef() {
        return def;
    }

    public double getAngle() {
        return angle;
    }

    public LatLng getOrigin() {
        return curPos;
    }

    public void setCur(Polyline polyline) {
        if (cur != null) cur.remove();
        cur = polyline;
    }

    public Polyline getCur() {
        return cur;
    }

    private String getLink(LatLng curPos, LatLng p1) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                curPos.latitude + "," + curPos.longitude + "&destination=" +
                p1.latitude + "," + p1.longitude +
                "&key=" + getString(R.string.google_maps_key) + "&mode=walking";
    }

    private void changeRouteTo30() {
        if (cur != null) cur.remove();
        if (routes.containsKey(30)) {
            PolylineOptions options = new PolylineOptions();
            List<LatLng> route = routes.get(30);
            if (route == null) return;
            for (LatLng point : route) options.add(point);
            cur = map.addPolyline(options);
            stylePolyline(cur);
            return;
        }
        try {
            double dist = 0.007 * 30 / def.getMinutes();
            LatLng p1 = getPoint(curPos, angle, dist);
            List<LatLng> points = new GetDirectionsJSON(getLink(curPos, p1), context).getDirections();
            routes.put(30, points);
            PolylineOptions options = new PolylineOptions();
            for (LatLng point : points) options.add(point);
            cur = map.addPolyline(options);
            stylePolyline(cur);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static LatLng getPoint(LatLng point, double angle, double dist) {
        return new LatLng(point.latitude + Math.sin(angle) * dist,
                point.longitude + Math.cos(angle) * dist);
    }

    private static void stylePolyline(Polyline polyline) {
        polyline.setColor(0xff00aaff);
        polyline.setWidth(20);
        polyline.setJointType(JointType.ROUND);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
    }
}
