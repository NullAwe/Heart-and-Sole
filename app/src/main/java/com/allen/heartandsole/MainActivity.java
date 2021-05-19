package com.allen.heartandsole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

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
    }

    public void goToSignUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void goToSignIn(View view) {
        startActivity(new Intent(this, SignInActivity.class));
    }
}
