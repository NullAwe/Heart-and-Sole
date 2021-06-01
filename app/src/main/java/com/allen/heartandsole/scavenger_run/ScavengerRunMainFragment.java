package com.allen.heartandsole.scavenger_run;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.allen.heartandsole.GetDirectionsJSON;
import com.allen.heartandsole.GetNearbyPOIs;
import com.allen.heartandsole.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.net.URLConnection;

public class ScavengerRunMainFragment extends Fragment implements OnMapReadyCallback {

    private Activity activity;
    private Context context;
    private View view;

    private GoogleMap map;
    private FusedLocationProviderClient locProv;
    private LatLng dest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scavenger_run_main, parent, false);
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
        this.map = gMap;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) return;
        map.setMyLocationEnabled(true);
        locProv.getLastLocation().addOnSuccessListener(activity, loc -> {
            if (loc == null) return;
            try {
                LatLng curPos = new LatLng(loc.getLatitude(), loc.getLongitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 15.0f));
                GetNearbyPOIs get = new GetNearbyPOIs(getPOIUrl(curPos),
                        context.getString(R.string.google_maps_key));
                Picasso.get().load(get.getImages().get(0)).into((ImageView)
                        view.findViewById(R.id.scav_image));

                dest = get.getNearbyPOIs().get(0);
                GetDirectionsJSON dirs = new GetDirectionsJSON(getDirUrl(curPos, dest));
                float mins = dirs.getMinutes(), mils = dirs.getMiles();
                ((TextView) view.findViewById(R.id.walking_time)).setText(
                        String.format("Expected walking time: %.2f min", mins));
                ((TextView) view.findViewById(R.id.walking_dist)).setText(
                        String.format("Walking dist: %.2f mi", mils));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public LatLng getDest() {
        return dest;
    }

    private String getPOIUrl(LatLng location) {
        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.latitude + "," + location.longitude + "&radius=1500&type=park&key=" +
                context.getString(R.string.google_maps_key);
    }

    private String getDirUrl(LatLng location, LatLng dest) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                location.latitude + "," + location.longitude + "&destination=" +
                dest.latitude + "," + dest.longitude + "&key=" +
                getString(R.string.google_maps_key) + "&mode=walking";
    }

    private static Bitmap getBitmap(String url) {
        try {
            URL u = new URL(url);
            URLConnection con = u.openConnection();

            return BitmapFactory.decodeStream(u.openConnection().getInputStream());
        } catch (Exception e) {
            return null;
        }
    }
}
