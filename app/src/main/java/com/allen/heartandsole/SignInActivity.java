package com.allen.heartandsole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SignInActivity extends AppCompatActivity {

    // Used for signing up a new account:
    private AccountAPI accountAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        // Sets a custom toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowCustomEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
            bar.setCustomView(R.layout.toolbar);
        }
        // Sets up the Firebase Account API:
        accountAPI = new FirebaseAccountAPI(succeeded -> {
            String resultText;
            if (succeeded) resultText = "Successful!";
            else resultText = "An account with that username already exists.";
            ((TextView) findViewById(R.id.sign_up_result)).setText(resultText);
        }, (status, username) -> {
            String resultText;
            switch (status) {
                case NO_USER:
                    resultText = "Account doesn't exist.";
                    break;
                case WRONG_PASSWORD:
                    resultText = "Incorrect password.";
                    break;
                case SUCCESS:
                    resultText = "Successful!";
                    Intent goToHomepage = new Intent(this, MeActivity.class);
                    goToHomepage.putExtra("username", username);
                    startActivity(goToHomepage);
                    break;
                default:
                    resultText = "";
            }
            ((TextView) findViewById(R.id.sign_in_result)).setText(resultText);
        });
    }

    // Signs a user in:
    public void signIn(View view) {
        EditText un = findViewById(R.id.username), pw = findViewById(R.id.password);
        String unText = un.getText().toString(), pwText = pw.getText().toString();
        accountAPI.auth(new Account(unText, pwText));
    }
}
