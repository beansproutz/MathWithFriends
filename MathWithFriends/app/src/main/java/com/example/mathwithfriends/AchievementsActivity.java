package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.server.User;
import com.example.utility.FullScreenModifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class AchievementsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_achievements);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize Firebase stuffs.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userID = FirebaseAuth.getInstance().getUid();

        // Check if achievements are locked and update availability accordingly
        updateUserAchievements();

        // Check User's Music Setting and Play music if setting is true
        getMusicSetting();
    }

    // Check and update the status of achievements for user
    private void updateUserAchievements() {
        DatabaseReference userRef = mDatabase.child("Users").child(userID);

        userRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // No need to update user - just making sure the info is received from the database before doing stuff
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                // Check for DB fetch and existence of users, output errors accordingly
                if (dataSnapshot == null) {
                    Log.e("AchievementsActivity", "Error: Can't retrieve user data");
                    return;
                }

                // Get the user, and update the info we need to determine what achievements have been unlocked
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    Log.e("AchievementsActivity", "Error: User not found");
                    return;
                }

                // Get user game stats to calculate achievements
                Integer gamesPlayed = user.getGamesPlayed();
                Integer gamesWon = user.getGamesWon();

                Log.d("AchievementsActivity", "played | won: " + String.valueOf(gamesPlayed) + " | " + String.valueOf(gamesWon));

                // Unlock achievements gained accordingly
                View fiveWinLock = findViewById(R.id.achievement5winsLocked);
                fiveWinLock.setVisibility(View.VISIBLE);

                View tenWinLock = findViewById(R.id.achievement10winsLocked);
                fiveWinLock.setVisibility(View.VISIBLE);

                View fifteenWinLock = findViewById(R.id.achievement15winsLocked);
                fiveWinLock.setVisibility(View.VISIBLE);

                if (user.getGamesWon() >= 5) {
                    fiveWinLock.setVisibility(View.INVISIBLE);
                }
                if (user.getGamesWon() >= 10) {
                    tenWinLock.setVisibility(View.INVISIBLE);
                }
                if (user.getGamesWon() >= 15) {
                    fifteenWinLock.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    // Sends User back to Home Screen when "Home" button is pressed
    public void onAchievementHomeClick(View view) {
        Intent intent = new Intent(AchievementsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void getMusicSetting() {
        if (userID == null) {
            Log.e("AchievementsActivity", "User ID not found!");
            return;
        }

        DatabaseReference userRef = mDatabase.child("Users").child(userID);

        userRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);

                // Ignore when Firebase Transactions optimistically uses
                // null before actually reading in from the database
                if (user == null) {
                    return Transaction.success(mutableData);
                }

                // Ensure this user has MusicSetting
                if (user.getMusicSetting() == null) {
                    user.setMusicSetting(true);
                }

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    Log.e("AchievementsActivity", "Data Snapshot of user data was null. Could not update musicSetting.");
                    return;
                }

                Boolean currMusicSetting = dataSnapshot.child("musicSetting").getValue(Boolean.class);


                if (currMusicSetting == null) {
                    Log.e("AchievementsActivity", "Data Snapshot of musicSetting was null. Could not update musicSetting.");
                    return;
                }

                if (currMusicSetting) {
                    startMusic();
                }

                else {
                    stopMusic();
                }
            }
        });
    }

    private void startMusic() {
        startService(new Intent(this, MusicPlayer.class)); //starts MusicPlayer Service
    }

    private void stopMusic(){
        stopService(new Intent(this, MusicPlayer.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getMusicSetting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMusicSetting();
    }
}
