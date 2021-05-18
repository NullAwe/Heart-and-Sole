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
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
//            String http = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
//                    curPos.latitude + "," + curPos.longitude + "&destination=" +
//                    (curPos.latitude + Math.random()) + "," + (curPos.longitude + Math.random()) +
//                    "&key=" + getString(R.string.google_maps_key) + "&mode=walking";
//            HttpURLConnection con;
//            try {
//                con = (HttpURLConnection) new URL(http).openConnection();
//                con.setRequestMethod("GET");
//                Scanner sc = new Scanner(con.getInputStream());
//                StringBuilder sb = new StringBuilder();
//                while (sc.hasNextLine()) sb.append(sc.nextLine()).append("\n");
//                sc.close();
//                con.disconnect();
//                List<LatLng> points = decodePoints(sb.toString());
//                for (int i = 1; i < points.size(); i++) {
//                    Polyline polyline = map.addPolyline(new PolylineOptions().add(
//                            points.get(i - 1),
//                            points.get(i)));
//                    stylePolyline(polyline);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        });
        locProv.requestLocationUpdates(lr, lc, Looper.myLooper());
    }

    public static List<LatLng> decodePoints(String encoded){
        int index = 0, lat = 0, lng = 0;
        List<LatLng> points = new ArrayList<>();
        while (index < encoded.length()) {
            int shift = 0, result = 0;
            while (true) {
                int b = encoded.charAt(index++) - '?';
                result |= ((b & 31) << shift);
                shift += 5;
                if (b < 32) break;
            }
            lat += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
            shift = 0;
            result = 0;
            while (true) {
                int b = encoded.charAt(index++) - '?';
                result |= ((b & 31) << shift);
                shift += 5;
                if (b < 32) break;
            }
            lng += ((result & 1) != 0 ? ~(result >> 1) : result >> 1);
            points.add(new LatLng(lat * 10,lng * 10));
        }
        return points;
    }

    private static void stylePolyline(Polyline polyline) {
        polyline.setColor(0xff000000);
        polyline.setWidth(12);
        polyline.setJointType(JointType.ROUND);
        polyline.setStartCap(new RoundCap());
        polyline.setEndCap(new RoundCap());
    }
}
