package com.allen.heartandsole;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SignUpActivity extends AppCompatActivity {

    // Displays when the EditText fields do not fit within length requirements:
    private static final String LEN_MESSAGE = "Name does not fit length requirements.";

    // Used for signing up a new account:
    private LocalUserAPI userAPI;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initializes class fields:
        userAPI = ((BinderWrapper<LocalUserAPI>)
                getIntent().getExtras().getBinder("userAPI")).get();
        // Sets a custom toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowCustomEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
            bar.setCustomView(R.layout.toolbar);
        }
    }

    // Signs up a new account:
    public void signUp(View view) {
        userAPI.addAccount(((EditText) findViewById(R.id.username)).getText().toString());
        super.onBackPressed();
    }
}
