package com.allen.heartandsole.solely_for_you;

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

import com.allen.heartandsole.GetDirectionsJSON;
import com.allen.heartandsole.R;
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
import com.google.android.material.slider.Slider;

import java.util.List;
import java.util.Map;

public class SolelyForYouEditFragment extends Fragment implements OnMapReadyCallback {

    private static final int[] TIMES = {5, 10, 15, 30, 45, 60, 90, 120};

    private Activity activity;
    private Context context;

    private GoogleMap map;
    private FusedLocationProviderClient locProv;

    private final Map<Integer, List<LatLng>> routes;
    private GetDirectionsJSON def;
    private double angle;
    private LatLng origin;
    private Polyline cur;
    private int sliderValue = 3;

    public SolelyForYouEditFragment(Map<Integer, List<LatLng>> routes) {
        this.routes = routes;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solely_for_you_edit, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity = requireActivity();
        context = requireContext();
        SupportMapFragment mapFrag = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.rec_map);
        if (mapFrag != null) mapFrag.getMapAsync(this);
        locProv = LocationServices.getFusedLocationProviderClient(context);
        Slider timeSlider = view.findViewById(R.id.time_slider);
        timeSlider.setValue(sliderValue);
        timeSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {}

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                sliderValue = Math.round(slider.getValue());
                String text = "Current Time: " + getTime(sliderValue);
                ((TextView) view.findViewById(R.id.cur_time)).setText(text);
                changeRoute(TIMES[sliderValue]);
            }
        });
        String text = "Current Time: " + getTime(sliderValue);
        ((TextView) view.findViewById(R.id.cur_time)).setText(text);
        timeSlider.setLabelFormatter(SolelyForYouEditFragment::getTime);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap gMap) {
        this.map = gMap;
        changeRoute(TIMES[sliderValue]);
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

    private void changeRoute(int minutes) {
        if (cur != null) cur.remove();
        if (routes.containsKey(minutes)) {
            PolylineOptions options = new PolylineOptions();
            List<LatLng> route = routes.get(minutes);
            if (route == null) return;
            for (LatLng point : route) options.add(point);
            cur = map.addPolyline(options);
            stylePolyline(cur);
            return;
        }
        try {
            double dist = 0.007 * minutes / def.getMinutes();
            LatLng p1 = getPoint(origin, angle, dist);
            List<LatLng> points = new GetDirectionsJSON(getLink(origin, p1)).getDirections();
            routes.put(minutes, points);
            PolylineOptions options = new PolylineOptions();
            for (LatLng point : points) options.add(point);
            cur = map.addPolyline(options);
            stylePolyline(cur);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLink(LatLng curPos, LatLng p1) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                curPos.latitude + "," + curPos.longitude + "&destination=" +
                p1.latitude + "," + p1.longitude +
                "&key=" + getString(R.string.google_maps_key) + "&mode=walking";
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

    public void setDef(GetDirectionsJSON def) {
        this.def = def;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public Polyline getCur() {
        return cur;
    }

    private static String getTime(float value) {
        int mins = TIMES[Math.round(value)];
        if (mins < 60) return mins + " min";
        else if (mins % 60 == 0) return mins / 60 + " hr";
        return mins / 60 + " hr " + mins % 60 + " min";
    }
}
