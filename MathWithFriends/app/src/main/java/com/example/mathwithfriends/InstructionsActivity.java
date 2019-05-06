package com.example.mathwithfriends;

import com.example.server.Room;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class InstructionsActivity extends Activity {

    public final static String TAG = "InstructionsActivity";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference roomsRef = database.getReference("Rooms");
    final String userID = FirebaseAuth.getInstance().getUid();
    private DatabaseReference mDatabase; // Used for checking MusicSetting


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_instructions);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getMusicSetting(2); //Checks User's Music Setting (Plays song 2 if true)
    }

    // Invoked when the queue button is clicked.
    // - If there is a vacant room, then join it and wait
    // - If there are only full rooms, make a new one, then join it and wait
    public void onQueueClick(View view) {
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot roomsDataSnapshot) {
                for (DataSnapshot roomDataSnapshot : roomsDataSnapshot.getChildren()) {
                    String roomID = roomDataSnapshot.getKey();
                    Room room = roomDataSnapshot.getValue(Room.class);

                    if (room == null) {
                        continue;
                    }

                    if (room.joinable(userID)) {
                        enterRoom(roomID);
                        return;
                    }
                }

                // No room could be joined - make a new one and join it
                setupRoom();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to join or setup room upon queueing.");
            }
        });
    }

    // Invoked when the back button is clicked
    public void onBackClick(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
        getMusicSetting(1); //plays track 1 (Home Music)
    }

    // Generates a new room and then enters this user into it
    private void setupRoom() {
        final String roomID = roomsRef.push().getKey();

        if (roomID == null) {
            Log.e(TAG, "Failed to generate a room ID");
            return;
        }

        Room room = new Room();
        roomsRef.child(roomID).setValue(room);

        enterRoom(roomID);
    }

    // Enters this user into the game specified by the room ID
    private void enterRoom(final String roomID) {
        roomsRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData roomData) {
                Room room = roomData.child(roomID).getValue(Room.class);

                if (room == null || !room.joinable(userID)) {
                    return Transaction.success(roomData);
                }

                room.addUser(userID);
                roomData.child(roomID).setValue(room);

                return Transaction.success(roomData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                waitForGameToStart(roomID);
            }
        });
    }

    public void waitForGameToStart(final String roomID) {
        roomsRef.child(roomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);

                if (room == null) {
                    return;
                }

                // All players have joined! Start up the game and finish off queueing.
                if (room.full()) {
                    Intent intent = new Intent(InstructionsActivity.this, GameActivity.class);
                    intent.putExtra("ROOM_ID", roomID);
                    startActivity(intent);
                    finish();
                }
                else {
                    Button queueButton = findViewById(R.id.queueButton);
                    queueButton.setText(R.string.instructionsLoading);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Queueing canceled for user " + userID + " on room " + roomID);
            }
        });
    }


    public void getMusicSetting(final Integer songNum) {
        if (userID == null) {
            Log.e("InstructionsActivity", "User ID not found!");
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
                    Log.e("InstructionsActivity", "Data Snapshot of user data was null. Could not update musicSetting.");
                    return;
                }

                Boolean currMusicSetting = dataSnapshot.child("musicSetting").getValue(Boolean.class);


                if (currMusicSetting == null) {
                    Log.e("InstructionsActivity", "Data Snapshot of musicSetting was null. Could not update musicSetting.");
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
    protected void onDestroy() { super.onDestroy(); }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMusicSetting(2); //plays track 2 (Customize Music)
    }
}
