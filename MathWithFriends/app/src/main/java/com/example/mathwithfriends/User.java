package com.example.mathwithfriends;

import android.graphics.drawable.Icon;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private String Email, Password;
    private Integer AvatarID;

    public User() {
        // Default constructor
    }
    public User(String Email, String Password, Integer AvatarID) {
        this.Email = Email;
        this.Password = Password;
        this.AvatarID = AvatarID;  // Initialize each user to default avatar. Users can change later.
    }
    public String getUserEmail() {return this.Email;}
    public String getPassword() {return this.Password;}
    public Integer getIconID() {return this.AvatarID;}

    public void setAvatarID(Integer avatarID) {
        AvatarID = avatarID;
    }
}
