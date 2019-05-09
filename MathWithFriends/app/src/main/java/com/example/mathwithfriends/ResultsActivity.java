package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.server.Room;
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

public class ResultsActivity extends AppCompatActivity {
    private final String TAG = "ResultsActivity";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    private String roomID;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_results);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        roomID = getIntent().getStringExtra("ROOM_ID");
        userID = mAuth.getUid();

        addListenerOnButton();
        updateStatus();

        getMusicSetting(1);
    }

    public void addListenerOnButton() {
        ImageButton playAgain = findViewById(R.id.playAgainButton);
        ImageButton homeButton = findViewById(R.id.homeButton);

        // Go back to instructions page
        playAgain.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ResultsActivity.this, InstructionsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Go back to home screen
        homeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ResultsActivity.this, HomeActivity.class);
                intent.putExtra("CONTINUE_PLAYING_MUSIC", true);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateStatus() {
        DatabaseReference roomRef = mDatabase.child("Rooms").child(roomID);

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);

                if (room == null) {
                    Log.e(TAG, "Room was null after game terminated");
                    return;
                }

                // Update user based on whether they won this game or not
                boolean hasWonGame = getIntent().getBooleanExtra("RESULT", false);
                updateUserStatistics(hasWonGame);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUserStatistics(final boolean hasWonGame) {
        DatabaseReference userRef = mDatabase.child("Users").child(userID);

        userRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);

                if (user == null) {
                    return Transaction.success(mutableData);
                }

                user.setGamesPlayed(user.getGamesPlayed() + 1);

                if (hasWonGame) {
                    user.setGamesWon(user.getGamesWon() + 1);
                }

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                setResultsBanner(hasWonGame);
            }
        });
    }

    private void setResultsBanner(final boolean hasWonGame) {
        ImageButton winMsg = findViewById(R.id.playerWin);
        ImageButton loseMsg = findViewById(R.id.playerLose);

        if (hasWonGame) {
            winMsg.setVisibility(View.VISIBLE);
            loseMsg.setVisibility(View.INVISIBLE);
        }
        else {
            loseMsg.setVisibility(View.VISIBLE);
            winMsg.setVisibility(View.INVISIBLE);
        }
    }

    public void getMusicSetting(final Integer songNum) {
        if (userID == null) {
            Log.e("ResultsActivity", "User ID not found!");
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
                    Log.e("ResultsActivity", "Data Snapshot of user data was null. Could not update musicSetting.");
                    return;
                }

                Boolean currMusicSetting = dataSnapshot.child("musicSetting").getValue(Boolean.class);


                if (currMusicSetting == null) {
                    Log.e("ResultsActivity", "Data Snapshot of musicSetting was null. Could not update musicSetting.");
                    return;
                }

                if (currMusicSetting) {
                    startMusic(songNum);
                }

                else {
                    stopMusic();
                }
            }
        });
    }

    private void startMusic(Integer songNum) {
        Intent serviceIntent = new Intent(this,MusicPlayer.class);
        serviceIntent.putExtra("Song", songNum);
        startService(serviceIntent);
    }

    private void stopMusic(){
        stopService(new Intent(this, MusicPlayer.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.child("Rooms").child(roomID).setValue(null);
    }
}

