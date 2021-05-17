package com.allen.heartandsole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AccountAPI accountAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);

        accountAPI = new FirebaseAccountAPI(succeeded -> {
            String resultText;
            if (succeeded) resultText = "Successful!";
            else resultText = "An account with that username already exists.";
            ((TextView) findViewById(R.id.sign_up_result)).setText(resultText);
        }, (status, username) -> {
            String resultText = "";
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
            }
            ((TextView) findViewById(R.id.sign_in_result)).setText(resultText);
        });
    }

    public void addAccount(View view) {
        EditText username = findViewById(R.id.su_username),
                password = findViewById(R.id.su_password);
        String unText = username.getText().toString(), pwText = password.getText().toString(),
                lengthMessage = "Username or password does not fit length requirements.";
        if (unText.length() < 5 || pwText.length() < 5 || unText.length() > 10)
            ((TextView) findViewById(R.id.sign_up_result)).setText(lengthMessage);
        else accountAPI.addAccount(new Account(unText, pwText));
    }

    public void checkAccount(View view) {
        EditText username = findViewById(R.id.si_username),
                password = findViewById(R.id.si_password);
        String unText = username.getText().toString(), pwText = password.getText().toString();
        accountAPI.auth(new Account(unText, pwText));
    }
}
