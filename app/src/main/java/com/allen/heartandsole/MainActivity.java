package com.allen.heartandsole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ViewFlipper appModesDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Sets a custom toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);
        // Fixes the ImageView background height.
        View bg = findViewById(R.id.unique_route_feature_bg),
                darkener = findViewById(R.id.darkener);
        bg.post(() -> {
            ViewGroup.LayoutParams params = bg.getLayoutParams();
            params.height = findViewById(R.id.unique_route_feature).getHeight();
            bg.setLayoutParams(params);
        });
        darkener.post(() -> {
            ViewGroup.LayoutParams params = darkener.getLayoutParams();
            params.height = findViewById(R.id.unique_route_feature).getHeight();
            darkener.setLayoutParams(params);
            darkener.getBackground().setAlpha(100);
        });
        appModesDesc = findViewById(R.id.app_modes_description);
        appModesDesc.setFlipInterval(7500);
        appModesDesc.setInAnimation(this, R.anim.slide_in_right);
        appModesDesc.setOutAnimation(this, R.anim.slide_out_left);
//        appModesDesc.setInAnimation(this, R.anim.slide_in_left);
//        appModesDesc.setOutAnimation(this, R.anim.slide_out_right);
        appModesDesc.startFlipping();
    }

    public void moveVFLeft(View view) {
        appModesDesc.setInAnimation(this, R.anim.slide_in_left);
        appModesDesc.setOutAnimation(this, R.anim.slide_out_right);
        appModesDesc.showPrevious();
        resetFlip();
    }

    public void moveVFRight(View view) {
        appModesDesc.setInAnimation(this, R.anim.slide_in_right);
        appModesDesc.setOutAnimation(this, R.anim.slide_out_left);
        appModesDesc.showNext();
        resetFlip();
    }

    public void resetFlip() {
        appModesDesc.stopFlipping();
        appModesDesc.startFlipping();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                appModesDesc.setInAnimation(MainActivity.this, R.anim.slide_in_right);
                appModesDesc.setOutAnimation(MainActivity.this, R.anim.slide_out_left);
            }
        }, 100);
    }

    public void goToSignUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void goToSignIn(View view) {
        startActivity(new Intent(this, SignInActivity.class));
    }
}
