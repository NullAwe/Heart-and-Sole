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

    private SharedPreferencesAccountAPI accountAPI;

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

        accountAPI = new SharedPreferencesAccountAPI(this);
    }

    public void addAccount(View view) {
        EditText username = findViewById(R.id.su_username),
                password = findViewById(R.id.su_password);
        String unText = username.getText().toString(), pwText = password.getText().toString();
        String resultText;
        if (unText.length() < 5) resultText = "Username too short.";
        else if (accountAPI.addAccount(new Account(unText, pwText))) resultText = "Successful!";
        else resultText = "An account with this username already exists.";
        ((TextView) findViewById(R.id.sign_up_result)).setText(resultText);
    }


    public void checkAccount(View view) {
        EditText username = findViewById(R.id.si_username),
                password = findViewById(R.id.si_password);
        String unText = username.getText().toString(), pwText = password.getText().toString();
        String resultText;
        Account acc = accountAPI.getAccount(unText);
        if (acc.getUsername().length() == 0) resultText = "Account doesn't exist.";
        else if (acc.getPassword().equals(pwText)) resultText = "Successfully logged in!";
        else resultText = "Wrong password.";
        ((TextView) findViewById(R.id.sign_in_result)).setText(resultText);
        if (resultText.equals("Successfully logged in!")) {
            Intent goToHomepage = new Intent(this, MeActivity.class);
            goToHomepage.putExtra("username", acc.getUsername());
            startActivity(goToHomepage);
        }
    }
}
