package com.allen.heartandsole;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);


        // Sets a custom toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);
    }

    // Signs up a new account:
    public void signUp(View view) {
        String un = ((EditText) findViewById(R.id.username)).getText().toString();
        SharedPrefSingleton.getInstance().addAccount(un.length() == 0 ? null : un);
        super.onBackPressed();
    }
}
