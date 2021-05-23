package com.allen.heartandsole.solely_for_you;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.allen.heartandsole.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolelyForYouActivity extends AppCompatActivity {

    private FragmentManager fragMan;

    private SolelyForYouMainFragment mainFragment;
    private SolelyForYouEditFragment editFragment;
    private SolelyForYouMapFragment mapFragment;

    private final Map<Integer, List<LatLng>> routes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solely_for_you);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);

        mainFragment = new SolelyForYouMainFragment(routes);
        editFragment = new SolelyForYouEditFragment(routes);
        mapFragment = new SolelyForYouMapFragment();

        fragMan = getSupportFragmentManager();
        FragmentTransaction ft = fragMan.beginTransaction();
        ft.replace(R.id.fragment, mainFragment).commit();
    }

    public void switchToEdit(View view) {
        editFragment.setDef(mainFragment.getDef());
        editFragment.setAngle(mainFragment.getAngle());
        editFragment.setOrigin(mainFragment.getOrigin());
        FragmentTransaction ft = fragMan.beginTransaction();
        ft.replace(R.id.fragment, editFragment).commit();
    }

    public void switchToRoute(View view) {
        mapFragment.setCur(mainFragment.getCur());
        FragmentTransaction ft = fragMan.beginTransaction();
        ft.replace(R.id.fragment, mapFragment).commit();
    }

    public void switchToPreview(View view) {
        mainFragment.setCur(editFragment.getCur());
        FragmentTransaction ft = fragMan.beginTransaction();
        ft.replace(R.id.fragment, mainFragment).commit();
    }

    public void switchToDone(View view) {
        fragMan.beginTransaction().replace(R.id.fragment, new SolelyForYouDoneFragment()).commit();
    }

    public void backToHomepage(View view) {
        super.onBackPressed();
    }
}