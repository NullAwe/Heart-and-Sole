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

import com.allen.heartandsole.account.Account;
import com.allen.heartandsole.account.AccountAPI;
import com.allen.heartandsole.account.FirebaseAccountAPI;

public class SignUpActivity extends AppCompatActivity {

    // Displays when the EditText fields do not fit within length requirements:
    private static final String LEN_MESSAGE = "Username or password does not fit length " +
            "requirements.";

    // Used for signing up a new account:
    private AccountAPI accountAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
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

    // Signs up a new account:
    public void signUp(View view) {
        EditText un = findViewById(R.id.username), pw = findViewById(R.id.password);
        String unText = un.getText().toString(), pwText = pw.getText().toString();
        if (unText.length() < 5 || unText.length() > 10 || pwText.length() < 5)
            ((TextView) findViewById(R.id.sign_up_result)).setText(LEN_MESSAGE);
        else accountAPI.addAccount(new Account(unText, pwText));
    }
}
