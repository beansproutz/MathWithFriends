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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final String userID = FirebaseAuth.getInstance().getUid();
    private Integer currAvatar;      //

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

    public void getAvatarID() {
        Log.d("HomeActivity", "User ID is " + userID);

        DatabaseReference ref = database.getReference().child("Users").child(userID);

        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Get user's current avatar from Firebase.
                User user = mutableData.getValue(User.class);

                if (user == null) {
                    return Transaction.success(mutableData);
                }

                if (user.getAvatarID() == null) {
                    user.setAvatarID(1);
                }

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                currAvatar = dataSnapshot.child("avatarID").getValue(Integer.class);

                // TODO
                switch (currAvatar) {
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                    case 8:
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

    // Invoked when the play button is clicked.
    // Starts the instructions from the home page.
    public void onHomepagePlayClick(View view) {
        Intent intent = new Intent(HomeActivity.this, InstructionsActivity.class);
        startActivity(intent);
    }

}
