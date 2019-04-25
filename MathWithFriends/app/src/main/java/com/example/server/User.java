package com.example.server;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private Integer avatarID;
    private Integer gamesWon;
    private Integer gamesPlayed;
    private Integer musicSetting;
    private Integer sfxSetting;

    public User() {
        // Default constructor
        this.avatarID = 1;
        this.gamesWon = 0;
        this.gamesPlayed = 0;
        this.musicSetting = 1;
        this.sfxSetting = 1;
    }

    public User(Integer avatarID, Integer gamesWon, Integer gamesPlayed, Integer musicSetting, Integer sfxSetting) {
        this.avatarID = avatarID;       // Initialize to default avatar. Users can change later.
        this.gamesWon = gamesWon;       // Initialize to 0. Increments every time user wins a game.
        this.gamesPlayed = gamesPlayed; // Initialize to 0. Increments every time user finishes a game.
        this.musicSetting = musicSetting; //Initialized to 1. Changes to 0 when user presses toggle button.
        this.sfxSetting = sfxSetting; //Initialized to 1. Changes to 0 when user presses toggle button.
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

    public Integer getMusicSetting() { return this.musicSetting; }

    public void setMusicSetting(Integer musicSetting) { this.musicSetting = musicSetting; }

    public Integer getSfxSetting() { return this.sfxSetting; }

    public void setSfxSetting(Integer sfxSetting) { this.sfxSetting = sfxSetting; }
}
