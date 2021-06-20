package com.highdefinition.heartandsole;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RedHotChiliSteppersActivity extends AppCompatActivity {

    private MediaPlayer player;
    private List<Song> playlist;
    private int index;
    private TextView songDescription;

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
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            String[] songDesc = field.getName().split("__"),
                    songName = songDesc[0].split("_"),
                    songArtist = songDesc[1].split("_");
            StringBuilder name = new StringBuilder(), artist = new StringBuilder();
            for (int i = 0; i < songName.length; i++) {
                name.append(capitalize(songName[i]));
                if (i < songName.length - 1) name.append(" ");
            }
            for (int i = 0; i < songArtist.length; i++) {
                artist.append(capitalize(songArtist[i]));
                if (i < songArtist.length - 1) artist.append(" ");
            }
            try {
                playlist.add(new Song(field.getInt(field), name.toString(), artist.toString()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        Collections.shuffle(playlist);
        index = 0;
        player = MediaPlayer.create(this, playlist.get(index).resId);
        songDescription = findViewById(R.id.song_description);
        updateSongDescription();

        player.setOnCompletionListener(mPlayer -> playNext(null));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }

    public void play(View view) {
        player.start();
        findViewById(R.id.play).setVisibility(View.GONE);
        findViewById(R.id.pause).setVisibility(View.VISIBLE);
    }

    public void pause(View view) {
        player.pause();
        findViewById(R.id.pause).setVisibility(View.GONE);
        findViewById(R.id.play).setVisibility(View.VISIBLE);
    }

    public void playNext(View view) {
        index = (index + 1) % playlist.size();
        player.reset();
        try {
            player.setDataSource(this.getResources().openRawResourceFd(playlist.get(index).resId));
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
        updateSongDescription();
        if (findViewById(R.id.pause).getVisibility() == View.GONE) pause(null);
    }

    private void updateSongDescription() {
        String text = "Now Playing:\n" + playlist.get(index).song + " by " +
                playlist.get(index).artist;
        songDescription.setText(text);
    }

    private static String capitalize(String word) {
        if (word.length() == 0) return word;
        if (word.length() == 1) return Character.toString(Character.toUpperCase(word.charAt(0)));
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    private static class Song {

        private final int resId;
        private final String song, artist;

        public Song(int resId, String song, String artist) {
            this.resId = resId;
            this.song = song;
            this.artist = artist;
        }
    }
}
