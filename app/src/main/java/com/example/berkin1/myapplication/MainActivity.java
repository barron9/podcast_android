package com.example.berkin1.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.berkin1.myapplication.Player.Playeradapter;
import com.example.berkin1.myapplication.Player.Playeritem;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.icosillion.podengine.models.Episode;
import com.icosillion.podengine.models.Podcast;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ExoPlayer exoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxBus.get().register(this);
        try_get_pod_from_internet();


    }

    public void initplayer(final String url) {
        FrameLayout fr = findViewById(R.id.fr);
        final TextView tw = findViewById(R.id.tw);
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer = null;
        }
        TrackSelector trackSelector = new DefaultTrackSelector();

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "AudioStreamer"));

        MediaSource audioSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));

        exoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, new DefaultLoadControl());

        exoPlayer.prepare(audioSource);

        exoPlayer.addListener(new Player.DefaultEventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == 3) {
                    tw.setText("playing");

                } else if (playbackState == 2) {
                    tw.setText("buffering");

                }
            }
        });

        exoPlayer.setPlayWhenReady(true);

    }

    public void try_get_pod_from_internet() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Do network action in this function
                try {
                    Podcast podcast = new Podcast(new URL("http://acikradyo.com.tr/i/rss/Ahsaptan_Betona_Mecidiyeden_Jetona.xml"));
                    Log.d("episodes", podcast.getTitle() + ", size: " + podcast.getEpisodes().size());
                    final ArrayList<Playeritem> persons = new ArrayList<Playeritem>();
                    for (Episode episode : podcast.getEpisodes()) {
                        Log.d("episodes", "- " + episode.getTitle() + String.valueOf(episode));
                        persons.add(new Playeritem(episode.getTitle(), episode.getEnclosure().getURL().toString(), 0));
                    }
                    list_pods_to_view(persons);
                } catch (Exception ex) {
                    Log.d("episodes", ex.toString());
                }
            }
        }).start();
    }

    public void list_pods_to_view(final ArrayList<Playeritem> values) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ListView lw = findViewById(R.id.lw);
                Playeradapter listViewAdapter = new Playeradapter(MainActivity.this, values);
                lw.setAdapter(listViewAdapter);
                listViewAdapter.notifyDataSetChanged();

            }
        });
    }

    @Subscribe(
            thread = EventThread.IO,
            tags = {
                    @Tag("eat")
            }
    )
    public void eat(final String food) {
        // purpose
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                initplayer(food);
                //Toast.makeText(MainActivity.this, food, Toast.LENGTH_LONG).show();

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.get().unregister(this);

    }

}
