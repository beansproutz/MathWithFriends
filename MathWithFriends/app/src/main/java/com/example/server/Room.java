package com.example.server;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Room {
    private Long playerCount;
    private Long firstUserQuestionsSolved;
    private Long secondUserQuestionsSolved;
    private String firstUserID;
    private String secondUserID;

    public Room() {
        // Needed for Room.class access in Firebase
        this.playerCount = 0L;
        this.firstUserQuestionsSolved = 0L;
        this.secondUserQuestionsSolved = 0L;
        this.firstUserID = "";
        this.secondUserID = "";
    }

    public Room(Long playerCount, Long firstUserQuestionsSolved, Long secondUserQuestionsSolved, String firstUserID, String secondUserID) {
        this.playerCount = playerCount;
        this.firstUserQuestionsSolved = firstUserQuestionsSolved;
        this.secondUserQuestionsSolved = secondUserQuestionsSolved;
        this.firstUserID = firstUserID;
        this.secondUserID = secondUserID;
    }

    public boolean playerWon(String userID) {
        if (firstUserID.equals(userID)) {
            return firstUserQuestionsSolved > secondUserQuestionsSolved;
        }

        if (secondUserID.equals(userID)) {
            return secondUserQuestionsSolved > firstUserQuestionsSolved;
        }

        return false;
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
        if (userExists(userID)) {
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

    public Long getFirstUserQuestionsSolved() {
        return firstUserQuestionsSolved;
    }

    public void setFirstUserQuestionsSolved(Long firstUserQuestionsSolved) {
        this.firstUserQuestionsSolved = firstUserQuestionsSolved;
    }

    public Long getSecondUserQuestionsSolved() {
        return secondUserQuestionsSolved;
    }

    public void setSecondUserQuestionsSolved(Long secondUserQuestionsSolved) {
        this.secondUserQuestionsSolved = secondUserQuestionsSolved;
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
