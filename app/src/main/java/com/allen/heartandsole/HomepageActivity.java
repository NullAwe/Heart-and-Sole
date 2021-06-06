package com.allen.heartandsole;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.allen.heartandsole.scavenger_run.ScavengerRunActivity;
import com.allen.heartandsole.solely_for_you.SolelyForYouActivity;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

/**
 * This is the Activity that inflates upon opening the app. Users are presented with an
 * information page describing the app if it is their first time opening the app. If it is not
 * their first time opening the app, they are sent directly to the main functioning portion of
 * the app.
 */
public class HomepageActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentStatePagerAdapter adapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        // Initializes class fields:
        SharedPrefSingleton.setContext(this);
        // Sets the viewed fragment:
        viewPager = findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), this::signUp);
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(findViewById(R.id.pager));
        // Sets a custom toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);
    }

    public void signUp(String un) {
        SharedPrefSingleton.getInstance().addAccount(un.length() == 0 ? null : un);
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
    }

    public void goToSolelyForYou(View view) {
        if (checkPermission()) startActivity(new Intent(this, SolelyForYouActivity.class));
        else requestPermission(1);
    }

    public void goToScavengerRun(View view) {
        if (checkPermission()) startActivity(new Intent(this, ScavengerRunActivity.class));
        else requestPermission(2);
    }

    public boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED || ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(int code) {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
    }

    public void goToRedHotChiliSteppers(View view) {
        startActivity(new Intent(this, RedHotChiliSteppersActivity.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 1) goToSolelyForYou(null);
            else goToScavengerRun(null);
        }
    }

    public static class PagerAdapter extends FragmentStatePagerAdapter {

        private final SignUpFragment.SignUpResponseHandler handler;

        public PagerAdapter(FragmentManager fm, SignUpFragment.SignUpResponseHandler handler) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.handler = handler;
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            if (i == 1) return new AppInfoFragment();
            if (SharedPrefSingleton.getInstance().hasAccount()) return new WalkFragment();
            return new SignUpFragment(handler);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position == 0 ? "Walk!" : "Info";
        }
    }
}
