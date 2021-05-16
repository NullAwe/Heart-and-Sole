package com.allen.heartandsole;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

public class SolelyForYouActivity extends AppCompatActivity {

    private FragmentManager fragMan;

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
        fragMan = getSupportFragmentManager();
        FragmentTransaction ft = fragMan.beginTransaction();
        ft.replace(R.id.fragment, new SolelyForYouMainFragment()).commit();
    }

    public void switchToEdit(View view) {
        FragmentTransaction ft = fragMan.beginTransaction();
        ft.replace(R.id.fragment, new SolelyForYouEditFragment()).commit();
    }

    public void switchToRoute(View view) {
        FragmentTransaction ft = fragMan.beginTransaction();
        ft.replace(R.id.fragment, new SolelyForYouMapFragment()).commit();
    }

    public void switchToPreview(View view) {
        FragmentTransaction ft = fragMan.beginTransaction();
        ft.replace(R.id.fragment, new SolelyForYouMainFragment()).commit();
    }
}