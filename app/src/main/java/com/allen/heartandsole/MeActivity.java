package com.allen.heartandsole;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(R.layout.toolbar);
        String welcome = "Welcome, " + getIntent().getStringExtra("username");
        ((TextView) findViewById(R.id.me_heading)).setText(welcome);
        makeNavButtonsCircular();
    }

    private void makeNavButtonsCircular() {
        LinearLayout navButtons = findViewById(R.id.nav_buttons);
        for (int i = 0; i < navButtons.getChildCount(); i++) {
            View next = navButtons.getChildAt(i);
            if (!(next instanceof LinearLayout)) continue;
            LinearLayout ll = (LinearLayout) next;
            for (int j = 0; j < ll.getChildCount(); j++) {
                next = ll.getChildAt(j);
                if (!(next instanceof RelativeLayout)) continue;
                RelativeLayout rl = (RelativeLayout) next;
                for (int k = 0; k < rl.getChildCount(); k++) {
                    next = rl.getChildAt(k);
                    if (next instanceof Button) {
                        Button button = (Button) next;
                        button.post(() -> {
                            ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                            layoutParams.width = button.getHeight();
                            button.setLayoutParams(layoutParams);
                            button.postInvalidate();
                        });
                    } else if (next instanceof ImageButton) {
                        ImageButton button = (ImageButton) next;
                        button.post(() -> {
                            ViewGroup.LayoutParams layoutParams = button.getLayoutParams();
                            layoutParams.width = button.getHeight();
                            button.setLayoutParams(layoutParams);
                            button.postInvalidate();
                        });
                    }
                }
            }
        }
    }

    public void solelyForYou(View view) {
        startActivity(new Intent(this, SolelyForYouActivity.class));
    }
}
