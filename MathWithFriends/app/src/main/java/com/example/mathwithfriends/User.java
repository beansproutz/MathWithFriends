package com.example.mathwithfriends;

import android.graphics.drawable.Icon;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private String Email, Password;
    private Integer AvatarID;
    private Integer Points, Level;

    public User() {
        // Default constructor
    }

    public User(String Email, String Password, Integer AvatarID, Integer Points, Integer Level) {
        this.Email = Email;
        this.Password = Password;
        this.AvatarID = AvatarID;   // Initialize to default avatar. Users can change later.
        this.Points = Points;       // Initialize to 0. Increments every time user wins a game.
        this.Level = Level;         // Initialize to 0. Increments with every achievement reached.
    }

    public String getUserEmail() {
        return this.Email;
    }

    public String getPassword() {
        return this.Password;
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
}
