package com.example.mathwithfriends;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private String Email, Password;

    public User() {
        // Default constructor
    }
    public User(String Email, String Password) {
        this.Email = Email;
        this.Password = Password;
    }
    public String getUserEmail() {return this.Email;}
    public String getPassword() {return this.Password;}
}
