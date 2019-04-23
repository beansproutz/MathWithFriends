package com.example.mathwithfriends;

import android.graphics.drawable.Icon;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private Integer AvatarID;
    private Integer Points;
    private Integer Level;
    private boolean musicSetting , sfxSetting;

    public User() {
        // Default constructor
    }

    public User(Integer AvatarID, Integer Points, Integer Level) {
        this.AvatarID = AvatarID;   // Initialize to default avatar. Users can change later.
        this.Points = Points;       // Initialize to 0. Increments every time user wins a game.
        this.Level = Level;         // Initialize to 0. Increments with every achievement reached.
    }

    public Integer getAvatarID() {
        return this.AvatarID;
    }

    public void setAvatarID(Integer avatarID) {
        AvatarID = avatarID;
    }

    public Integer getPoints() {
        return this.Points;
    }

    public Integer getLevel() {
        return this.Level;
    }

    public boolean getMusicSetting() { return this.musicSetting; }

    public boolean getSfxSetting() { return this.sfxSetting; }
}
