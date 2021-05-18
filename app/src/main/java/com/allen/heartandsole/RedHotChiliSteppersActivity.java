package com.allen.heartandsole;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedHotChiliSteppersActivity extends AppCompatActivity {

    private MediaPlayer player;
    private List<Integer> playlist;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_hot_chili_steppers);
        // Sets a custom toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowCustomEnabled(true);
            bar.setDisplayShowTitleEnabled(false);
            bar.setCustomView(R.layout.toolbar);
        }
        playlist = new ArrayList<>();
        playlist.add(R.raw.seedless_strawberries);
        playlist.add(R.raw.spring_tides);
        playlist.add(R.raw.yesyesyes);
        playlist.add(R.raw.healing);
        playlist.add(R.raw.voyage);
        playlist.add(R.raw.azure);
        Collections.shuffle(playlist);
        index = 0;
        player = MediaPlayer.create(this, playlist.get(index));
        player.setOnCompletionListener(mPlayer -> {
            index = (index + 1) % playlist.size();
            try {
                player.reset();
                player.setDataSource(RedHotChiliSteppersActivity.this.getResources()
                        .openRawResourceFd(playlist.get(index)));
                player.prepare();
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }

    public void play(View view) {
        player.start();
        view.setVisibility(View.GONE);
        findViewById(R.id.pause).setVisibility(View.VISIBLE);
    }

    public void pause(View view) {
        player.pause();
        view.setVisibility(View.GONE);
        findViewById(R.id.play).setVisibility(View.VISIBLE);
    }
}
