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
import android.widget.TextView;

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
    private String userName;
    private String roomID;
    private boolean hasJoinedRoom = false;
    private ValueEventListener listenerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_instructions);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        if (userID != null) {
            FirebaseDatabase.getInstance().getReference("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userName = dataSnapshot.child("userName").getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    // Invoked when the queue button is clicked.
    // - If there is a vacant room, then join it and wait
    // - If there are only full rooms, make a new one, then join it and wait
    public void onQueueClick(View view) {
        TextView userNameView = findViewById(R.id.findFriend);

        if (userNameView.getText().length() != 0) {
            waitForPlayerToAccept(userNameView.getText().toString());
            return;
        }

        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot roomsDataSnapshot) {
                for (DataSnapshot roomDataSnapshot : roomsDataSnapshot.getChildren()) {
                    roomID = roomDataSnapshot.getKey();
                    Room room = roomDataSnapshot.getValue(Room.class);

                    if (room == null) {
                        continue;
                    }

                    if (room.joinable(userID)) {
                        enterRoom();
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
        intent.putExtra("CONTINUE_PLAYING_MUSIC", true);
        startActivity(intent);
        finish();
    }

    // Generates a new room and then enters this user into it
    private void setupRoom() {
        roomID = roomsRef.push().getKey();

        if (roomID == null) {
            Log.e(TAG, "Failed to generate a room ID");
            return;
        }

        Room room = new Room();
        roomsRef.child(roomID).setValue(room);

        enterRoom();
    }

    // Enters this user into the game specified by the room ID
    private void enterRoom() {
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
        listenerRef = roomsRef.child(roomID).addValueEventListener(new ValueEventListener() {
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
                    stopMusic();
                    startActivity(intent);
                    hasJoinedRoom = true;
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

    private void waitForPlayerToAccept(@NonNull final String friendUserName) {
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Assume that we will make a new room
                roomID = friendUserName;

                for (DataSnapshot roomDataSnapshot : dataSnapshot.getChildren()) {
                    roomID = roomDataSnapshot.getKey();

                    if (roomID == null) {
                        continue;
                    }

                    // If someone made a room for us, join it
                    if (roomID.equals(userName)) {
                        roomID = userName;
                        break;
                    }
                }

                Room room;

                if (roomID.equals(friendUserName)) {
                    room = new Room();
                }
                else {
                    room = dataSnapshot.child(roomID).getValue(Room.class);
                }

                if (room != null) {
                    room.addUser(userID);
                    roomsRef.child(roomID).setValue(room);
                }

                waitForGameToStart(roomID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void stopMusic(){
        stopService(new Intent(this, MusicPlayer.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (listenerRef != null && roomID != null) {
            roomsRef.child(roomID).removeEventListener(listenerRef);
        }

        if (!hasJoinedRoom && roomID != null) {
            roomsRef.child(roomID).setValue(null);
        }
    }
}
