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
        mapFragment = new ScavengerRunMapFragment();

        fragMan = getSupportFragmentManager();
        fragMan.beginTransaction().replace(R.id.fragment, mainFragment).commit();
    }

    public void switchToMapFragment(View view) {
        mapFragment.setDest(mainFragment.getDest());
        fragMan.beginTransaction().replace(R.id.fragment, mapFragment).commit();
    }

    public void backToHomepage(View view) {
        super.onBackPressed();
    }
}