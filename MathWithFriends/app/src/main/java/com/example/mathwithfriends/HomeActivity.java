package com.example.mathwithfriends;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.example.server.User;
import com.example.utility.FullScreenModifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());
        mAuth = FirebaseAuth.getInstance();

        if (userID == null) {
            Log.e("HomeActivity", "Entered home page without authentication");
            finish();
        }

        //Access Firebase and get the user's Music Settings
        getMusicSetting();

        //Access Firebase and get the user's SFX Settings
        getSFXSetting();

        // Access Firebase and display user's Avatar
        setAvatar();

    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser curruser = mAuth.getCurrentUser();
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

                if (currMusicSetting) {
                    musicToggle.setChecked(true);
                    musicToggle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_musicimg, 0, 0);
                    startMusic();
                }

                else {
                    musicToggle.setChecked(false);
                    musicToggle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_musicoff, 0, 0);
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
                    sfxToggle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sound_on, 0, 0);
                }

                else {
                    sfxToggle.setChecked(false);
                    sfxToggle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sound_off, 0, 0);
                }
            }
        });
    }


//Used to display Avatar on Home Screen
    private void setAvatar() {
        DatabaseReference userRef = database.getReference("Users").child(userID);

        userRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }

                Integer playerAvatarID = dataSnapshot.child("avatarID").getValue(Integer.class);

                if (playerAvatarID == null) {
                    return;
                }

                ImageView playerAvatar = findViewById(R.id.playerAvatar);
                playerAvatar.setImageResource(getAvatar(playerAvatarID)); //Sets the image to current avatar selected
            }
        });
    }

    public int getAvatar(Integer avatarID) {
        int avatarSource = 0;

        switch(avatarID) {
            case 1:
                avatarSource = R.drawable.cloud_regular;
                break;
            case 2:
                avatarSource = R.drawable.square_regular;
                break;
            case 3:
                avatarSource = R.drawable.triangle_regular;
                break;
            case 4:
                avatarSource = R.drawable.circle_regular;
                break;
            case 5:
                avatarSource = R.drawable.cloud_special;
                break;
            case 6:
                avatarSource = R.drawable.square_special;
                break;
            case 7:
                avatarSource = R.drawable.triangle_special;
                break;
            case 8:
                avatarSource = R.drawable.circle_special;
                break;
            default:
                /* EMPTY */
        }

        return avatarSource;
    }

    public void onMusicToggleClick(View view) {
        ToggleButton musicToggle = (ToggleButton) findViewById(R.id.musicButton);
        final Boolean currMusicSetting;

        if (musicToggle.isChecked()) {
            startMusic();
            currMusicSetting = true;
            musicToggle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_musicimg, 0, 0);

        }
        else {
            stopMusic();
            currMusicSetting = false;
            musicToggle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_musicoff, 0, 0);
        }

        updateMusicSetting(currMusicSetting);

    }

    public void onSFXToggleClick(View view) {
        ToggleButton sfxToggle = (ToggleButton) findViewById(R.id.sfxButton);
        final Boolean currSFXSetting;

        if (sfxToggle.isChecked()) {
            currSFXSetting = true;
            sfxToggle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sound_on, 0, 0);

            //PLAY SOUND EFFECT
            MediaPlayer mp;
            mp = MediaPlayer.create(this, R.raw.sword_collide);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                    mp=null;
                }
            });
            mp.start();


        }
        else {
            currSFXSetting = false;
            sfxToggle.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sound_off, 0, 0);
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
        finish();
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
