package com.example.server;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Room {
    private Long playerCount;
    private Long firstUserLife;
    private Long secondUserLife;
    private String firstUserID;
    private String secondUserID;

    public Room() {
        // Needed for Room.class access in Firebase
        this.playerCount = 0L;
        this.firstUserLife = 3L;
        this.secondUserLife = 3L;
        this.firstUserID = "";
        this.secondUserID = "";
    }

    public Room(Long playerCount, Long firstUserLife, Long secondUserLife, String firstUserID, String secondUserID) {
        this.playerCount = playerCount;
        this.firstUserLife = firstUserLife;
        this.secondUserLife = secondUserLife;
        this.firstUserID = firstUserID;
        this.secondUserID = secondUserID;
    }

    public boolean userExists(String userID) {
        return userID.equals(firstUserID) || userID.equals(secondUserID);
    }

    public boolean empty() {
        return playerCount == 0L;
    }

    public boolean full() {
        final Long MAX_CAPACITY = 2L;
        return playerCount >= MAX_CAPACITY;
    }

    public boolean joinable(String userID) {
        if (full()) {
            Log.d("Room", "Failed to enter room because it is full");
        }
        else if (userExists(userID)) {
            Log.d("Room", "Failed to enter room because user exists in it");
        }
        return !(full() || userExists(userID));
    }

    public void addUser(String userID) {
        if (empty()) {
            firstUserID = userID;
        }
        else {
            secondUserID = userID;
        }

        playerCount++;
    }

    public Long getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Long playerCount) {
        this.playerCount = playerCount;
    }

    public Long getFirstUserLife() {
        return firstUserLife;
    }

    public void setFirstUserLife(Long firstUserLife) {
        this.firstUserLife = firstUserLife;
    }

    public Long getSecondUserLife() {
        return secondUserLife;
    }

    public void setSecondUserLife(Long secondUserLife) {
        this.secondUserLife = secondUserLife;
    }

    public String getFirstUserID() {
        return firstUserID;
    }

    public void setFirstUserID(String firstUserID) {
        this.firstUserID = firstUserID;
    }

    public String getSecondUserID() {
        return secondUserID;
    }

    public void setSecondUserID(String secondUserID) {
        this.secondUserID = secondUserID;
    }
}
