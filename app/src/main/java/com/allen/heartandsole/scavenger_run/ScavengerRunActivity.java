package com.allen.heartandsole.scavenger_run;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;

import com.allen.heartandsole.R;

public class ScavengerRunActivity extends AppCompatActivity {

    private FragmentManager fragMan;

    private ScavengerRunMainFragment mainFragment;
    private ScavengerRunImageFragment imageFragment;
    private ScavengerRunMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scavenger_run);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);

        mainFragment = new ScavengerRunMainFragment();
        imageFragment = new ScavengerRunImageFragment(this.getResources().getString(
                R.string.google_maps_key));
        mapFragment = new ScavengerRunMapFragment();

        fragMan = getSupportFragmentManager();
        fragMan.beginTransaction().replace(R.id.fragment, mainFragment).commit();
    }

    public void switchToMapFragment(View view) {
        mapFragment.setDest(imageFragment.getDest());
        fragMan.beginTransaction().replace(R.id.fragment, mapFragment).commit();
    }

    public void switchToImageFragment(View view) {
        fragMan.beginTransaction().replace(R.id.fragment, imageFragment).commit();
    }

    public void startImageFragment(View view) {
        imageFragment.setGetNearbyPOIs(mainFragment.getLocation(), mainFragment.getRadius(),
                mainFragment.getType());
        fragMan.beginTransaction().replace(R.id.fragment, imageFragment).commit();
    }

    public void backToHomepage(View view) {
        super.onBackPressed();
    }
}