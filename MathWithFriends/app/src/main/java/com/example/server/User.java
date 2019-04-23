package com.example.server;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private Integer avatarID;
    private Integer points;
    private Integer level;
    private boolean musicSetting;
    private boolean sfxSetting;

    public User() {
        // Default constructor
        this.avatarID = 1;
        this.points = 0;
        this.level = 0;
    }

    public User(Integer avatarID, Integer points, Integer level) {
        this.avatarID = avatarID;   // Initialize to default avatar. Users can change later.
        this.points = points;       // Initialize to 0. Increments every time user wins a game.
        this.level = level;         // Initialize to 0. Increments with every achievement reached.
    }

    public Integer getAvatarID() {
        return this.avatarID;
    }

    public void setAvatarID(Integer avatarID) {
        this.avatarID = avatarID;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public boolean getMusicSetting() { return this.musicSetting; }

    public boolean getSfxSetting() { return this.sfxSetting; }
}
