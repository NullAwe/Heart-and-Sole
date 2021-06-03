package com.allen.heartandsole;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.allen.heartandsole.scavenger_run.ScavengerRunActivity;
import com.allen.heartandsole.solely_for_you.SolelyForYouActivity;

/**
 * This is the Activity that inflates upon opening the app. Users are presented with an
 * information page describing the app if it is their first time opening the app. If it is not
 * their first time opening the app, they are sent directly to the main functioning portion of
 * the app.
 */
public class HomepageActivity extends AppCompatActivity {

    private FragmentManager fragMan;
    private int curFrag;

    private SignUpFragment signUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        // Initializes class fields:
        SharedPrefSingleton.setContext(this);
        fragMan = getSupportFragmentManager();
        curFrag = -1;
        signUpFragment = new SignUpFragment();
        // Sets the viewed fragment:
        onBottomBarClicked(findViewById(SharedPrefSingleton.getInstance().hasAccount() ?
                R.id.walk : R.id.info));
        // Sets a custom toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);
    }

    public void onBottomBarClicked(View view) {
        LinearLayout bottomBar = findViewById(R.id.bottom_bar);
        int children = bottomBar.getChildCount(), id = -1;
        for (int i = 0; i < children; i++) {
            RelativeLayout child = (RelativeLayout) bottomBar.getChildAt(i);
            if (view == child) id = i;
            child.getChildAt(0).setVisibility(View.INVISIBLE);
        }
        ((RelativeLayout) view).getChildAt(0).setVisibility(View.VISIBLE);
        if (id == curFrag) return;
        setFragment(id);
        curFrag = id;
    }


    public void switchToInfoFragment(View view) {
        fragMan.beginTransaction().replace(R.id.fragment, new AppInfoFragment()).commit();
    }

    public void switchToWalkFragment(View view) {
        if (!SharedPrefSingleton.getInstance().hasAccount())
            fragMan.beginTransaction().replace(R.id.fragment, signUpFragment).commit();
        else
            fragMan.beginTransaction().replace(R.id.fragment, new WalkFragment()).commit();
    }

    private void setFragment(int id) {
        if (id == 0) switchToInfoFragment(null);
        else switchToWalkFragment(null);
    }

    public void signUp(View view) {
        String un = signUpFragment.getUN();
        SharedPrefSingleton.getInstance().addAccount(un.length() == 0 ? null : un);
        switchToWalkFragment(null);
    }

    public void goToSolelyForYou(View view) {
        startActivity(new Intent(this, SolelyForYouActivity.class));
    }

    public void goToScavengerRun(View view) {
        startActivity(new Intent(this, ScavengerRunActivity.class));
    }

    public void goToRedHotChiliSteppers(View view) {
        startActivity(new Intent(this, RedHotChiliSteppersActivity.class));
    }
}
