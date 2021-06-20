package com.highdefinition.heartandsole.scavenger_run;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import com.highdefinition.heartandsole.R;

/**
 * Activity that implements the scavenger run feature. The fragments
 * {@link ScavengerRunMainFragment}, {@link ScavengerRunImageFragment},
 * {@link ScavengerRunMapFragment}, and {@link ScavengerRunDoneFragment} are all components of
 * this activity.
 */
public class ScavengerRunActivity extends AppCompatActivity {

    private FragmentManager fragMan;

    private ScavengerRunMainFragment mainFragment;
    private ScavengerRunImageFragment imageFragment;
    private ScavengerRunMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scavenger_run);
        // Sets a custom toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);
        // Initializes class fields:
        fragMan = getSupportFragmentManager();
        mainFragment = new ScavengerRunMainFragment();
        imageFragment = new ScavengerRunImageFragment(this.getResources().getString(
                R.string.google_maps_key)); // Error here because of Android Studio v4.1+ bug not
        // being able to recognize resource values created in build.gradle. Safe to ignore.
        mapFragment = new ScavengerRunMapFragment();
        // Sets the viewed fragment to ScavengerRunMainFragment:
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
        switchToImageFragment(view);
    }

    public void switchToDoneFragment(View view) {
        fragMan.beginTransaction().replace(R.id.fragment, new ScavengerRunDoneFragment()).commit();
    }

    public void backToHomepage(View view) {
        super.onBackPressed();
    }
}