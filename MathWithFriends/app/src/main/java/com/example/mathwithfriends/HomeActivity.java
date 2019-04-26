package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
    private DatabaseReference mDatabase; //Used to Write to MusicSetting in Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (userID == null) {
            Log.e("HomeActivity", "Entered home page without authentication");
            finish();
        }

        ImageView avatar = (ImageView) findViewById(R.id.imageView2);  //used to display avatar on Homescreen
        Button goToCustomize = (Button) findViewById(R.id.customizeButton);
        Button goToAchievement = (Button) findViewById(R.id.gotoAchievement);
        ToggleButton sfxToggle = (ToggleButton) findViewById(R.id.sfxButton);
        ToggleButton musicToggle = (ToggleButton) findViewById(R.id.musicButton);


        // Access Firebase and get the user's current avatar.
        getAvatarID();

        //Access Firebase and get the user's Music Settings
        getMusicSetting();

        //Access Firebase and get the user's SFX Settings
        getSFXSetting();

    }

    public void updateMusicSetting(final Boolean musicVal) {
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

                // Update musicSetting based on the what the User toggles
                user.setMusicSetting(musicVal);
                mutableData.setValue(user);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
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
        stopMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMusic();
    }


    public void getMusicSetting() {
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
                    Log.e(TAG, "Data Snapshot of user data was null. Could not update musicSetting.");
                    return;
                }

                Boolean currMusicSetting = dataSnapshot.child("musicSetting").getValue(Boolean.class);
                ToggleButton musicToggle = (ToggleButton) findViewById(R.id.musicButton);


                if (currMusicSetting == null) {
                    Log.e(TAG, "Data Snapshot of musicSetting was null. Could not update musicSetting.");
                    return;
                }

                if (currMusicSetting.booleanValue() == true) {
                    musicToggle.setChecked(true);
                    startMusic();
                }

                else {
                    musicToggle.setChecked(false);
                    stopMusic();

                }
            }
        });
    }

    public void getSFXSetting() {
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

                // Ensure SFX Setting is set
                if (user.getSfxSetting() == null) {
                    user.setSfxSetting(true);
                }

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    Log.e(TAG, "Data Snapshot of user data was null. Could not update musicSetting.");
                    return;
                }

                Boolean currSFXSetting = dataSnapshot.child("sfxSetting").getValue(Boolean.class);
                ToggleButton sfxToggle = (ToggleButton) findViewById(R.id.sfxButton);


                if (currSFXSetting == null) {
                    Log.e(TAG, "Data Snapshot of sfxSetting was null. Could not update sfxSetting.");
                    return;
                }

                if (currSFXSetting.booleanValue() == true) {
                    sfxToggle.setChecked(true);
                }

                else {
                    sfxToggle.setChecked(false);
                }
            }
        });
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

    public void onMusicToggleClick(View view) {
        ToggleButton musicToggle = (ToggleButton) findViewById(R.id.musicButton);
        final Boolean currMusicSetting;

        if (musicToggle.isChecked()) {
            startMusic();
            currMusicSetting = true;
        }
        else {
            stopMusic();
            currMusicSetting = false;
        }

        updateMusicSetting(currMusicSetting);

    }

    public void onSFXToggleClick(View view) {
        ToggleButton sfxToggle = (ToggleButton) findViewById(R.id.sfxButton);
        final Boolean currSFXSetting;

        if (sfxToggle.isChecked()) {
            startMusic();
            currSFXSetting = true;
        }
        else {
            stopMusic();
            currSFXSetting = false;
        }

        updateSFXSetting(currSFXSetting);

    }

    public void updateSFXSetting(final Boolean sfxVal) {
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

                // Update sfxSetting the database based on toggle
                user.setSfxSetting(sfxVal);
                mutableData.setValue(user);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
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
