package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.server.User;
import com.example.utility.FullScreenModifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class HomeActivity extends AppCompatActivity {

    private final String TAG = "HomeActivity";
    private String userID = FirebaseAuth.getInstance().getUid();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        if (userID == null) {
            Log.e("HomeActivity", "Entered home page without authentication");
            finish();
        }

        ImageView avatar = (ImageView) findViewById(R.id.imageView2);                       //used to display avatar on Homescreen
        Button goToCustomize = (Button) findViewById(R.id.customizeButton);
        Button goToAchievement = (Button) findViewById(R.id.gotoAchievement);
        ToggleButton sfxToggle = (ToggleButton) findViewById(R.id.sfxButton);
        ToggleButton musicToggle = (ToggleButton) findViewById(R.id.musicButton);

        Intent svc = new Intent(this, MusicPlayer.class);
        startService(svc); //starts MusicPlayer Service

        // Access Firebase and get the user's current avatar.
        getAvatarID();

        /*Commented code may be used later for checking status of a toggle button*/

/*
        sfxToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled


                } else {
                    // The toggle is disabled


                }
            }
        });


        musicToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled

                } else {
                    // The toggle is disabled

                }
            }
        });
*/
    }

    private void stopMusic(){
        stopService(new Intent(HomeActivity.this, MusicPlayer.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    public void getAvatarID() {
        if (userID == null) {
            Log.e(TAG, "User ID not found!");
            return;
        }

        DatabaseReference userRef = database.getReference("Users").child(userID);

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

                // Ensure this user has an avatar setting if they somehow did not already
                if (user.getAvatarID() == null) {
                    user.setAvatarID(1);
                }

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    Log.e(TAG, "Data Snapshot of user data was null. Could not update avatar.");
                    return;
                }

                Integer currAvatar = dataSnapshot.child("avatarID").getValue(Integer.class);

                if (currAvatar == null) {
                    Log.e(TAG, "Data Snapshot of avatar ID was null. Could not update avatar.");
                    return;
                }

                // TODO for Zack
                // TODO guessing we will update the image on the screen?
                switch (currAvatar) {
                    case 1:
                        Log.d(TAG, "This user has avatar ID 1 active!");
                        break;
                    case 2:
                        Log.d(TAG, "This user has avatar ID 2 active!");
                        break;
                    case 3:
                        Log.d(TAG, "This user has avatar ID 3 active!");
                        break;
                    case 4:
                        Log.d(TAG, "This user has avatar ID 4 active!");
                        break;
                    case 5:
                        Log.d(TAG, "This user has avatar ID 5 active!");
                        break;
                    case 6:
                        Log.d(TAG, "This user has avatar ID 6 active!");
                        break;
                    case 7:
                        Log.d(TAG, "This user has avatar ID 7 active!");
                        break;
                    case 8:
                        Log.d(TAG, "This user has avatar ID 8 active!");
                        break;
                }
            }
        });
    }

    // Invoked when the Customize button is clicked.
    public void onHomepageCustomizeClick(View view) {
        Intent intent = new Intent(HomeActivity.this, CustomizeActivity.class);
        startActivity(intent);
    }

    public void onHomepageAchievementClick(View view) {
        Intent intent = new Intent(HomeActivity.this, AchievementsActivity.class);
        startActivity(intent);
    }

    // Invoked when the play button is clicked.
    // Starts the instructions from the home page.
    public void onHomepagePlayClick(View view) {
        Intent intent = new Intent(HomeActivity.this, InstructionsActivity.class);
        startActivity(intent);
    }

}
