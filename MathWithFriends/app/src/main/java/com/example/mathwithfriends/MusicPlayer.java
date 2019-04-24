package com.example.mathwithfriends;

import android.app.Service;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.internal.LifecycleActivity;

public class MusicPlayer extends Service {
    private static final String TAG = null;
    MediaPlayer player;

    @Nullable
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.sample);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onUnBind(Intent arg0) {
        // TO DO Auto-generated method
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(player.isLooping()) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onLowMemory() {

    }
}
