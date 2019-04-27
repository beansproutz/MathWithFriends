package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

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
        setContentView(R.layout.activity_results);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        roomID = getIntent().getStringExtra("ROOM_ID");
        userID = mAuth.getUid();

        addListenerOnButton();
        updateStatus();
    }

    public void addListenerOnButton() {
        Button playAgain = findViewById(R.id.playAgainButton);
        Button homeButton = findViewById(R.id.homeButton);

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
                updateUserStatistics(room.playerWon(userID));
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
                TextView winMsg = findViewById(R.id.playerWin);
                winMsg.setVisibility(View.INVISIBLE);

                TextView loseMsg = findViewById(R.id.playerLose);
                loseMsg.setVisibility(View.INVISIBLE);

                if (user == null) {
                    return Transaction.success(mutableData);
                }

                user.setGamesPlayed(user.getGamesPlayed() + 1);

                if (hasWonGame) {
                    user.setGamesWon(user.getGamesWon() + 1);
                    winMsg.setVisibility(View.VISIBLE);
                }
                else {
                    loseMsg.setVisibility(View.VISIBLE);
                }

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }
}

