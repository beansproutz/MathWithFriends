package com.example.server;

import android.text.BoringLayout;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private Integer avatarID;
    private Integer gamesWon;
    private Integer gamesPlayed;
    private Boolean musicSetting;
    private Boolean sfxSetting;

    public User() {
        // Default constructor
        this.avatarID = 1;
        this.gamesWon = 0;
        this.gamesPlayed = 0;
        this.musicSetting = true;
        this.sfxSetting = true;
    }

    public User(Integer avatarID, Integer gamesWon, Integer gamesPlayed, Boolean musicSetting, Boolean sfxSetting) {
        this.avatarID = avatarID;       // Initialize to default avatar. Users can change later.
        this.gamesWon = gamesWon;       // Initialize to 0. Increments every time user wins a game.
        this.gamesPlayed = gamesPlayed; // Initialize to 0. Increments every time user finishes a game.
        this.musicSetting = musicSetting; //Initialized to true. Changes to false when user presses toggle button.
        this.sfxSetting = sfxSetting; //Initialized to true. Changes to false when user presses toggle button.
    }

    public Integer getAvatarID() {
        return this.avatarID;
    }

    public void setAvatarID(Integer avatarID) {
        this.avatarID = avatarID;
    }

    public Integer getGamesWon() {
        return this.gamesWon;
    }

    public void setGamesWon(Integer gamesWon) {
        this.gamesWon = gamesWon;
    }

    public Integer getGamesPlayed() {
        return this.gamesPlayed;
    }

    public void setGamesPlayed(Integer gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Boolean getMusicSetting() { return this.musicSetting; }

    public void setMusicSetting(Boolean musicSetting) { this.musicSetting = musicSetting; }

    public Boolean getSfxSetting() { return this.sfxSetting; }

    public void setSfxSetting(Boolean sfxSetting) { this.sfxSetting = sfxSetting; }
}
