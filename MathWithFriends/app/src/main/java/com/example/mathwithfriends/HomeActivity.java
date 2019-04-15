package com.example.mathwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;         // To get user id
    private Integer currAvatar;         //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        ImageView avatar = (ImageView) findViewById(R.id.imageView2);                       //used to display avatar on Homescreen
        Button goToCustomize = (Button) findViewById(R.id.customizeButton);
        ToggleButton sfxToggle = (ToggleButton) findViewById(R.id.sfxButton);
        ToggleButton musicToggle = (ToggleButton) findViewById(R.id.musicButton);

        Intent svc = new Intent(this, MusicPlayer.class);
        startService(svc); //starts MusicPlayer Service

        // Initialize Firebase stuffs to use later.
        mAuth = FirebaseAuth.getInstance();

        // Access Firebase and get the user's current avatar.
        getAvatarID();

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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user's current avatar from Firebase.
                User user = dataSnapshot.getValue(User.class);
                currAvatar = user.getAvatarID();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: Can't retrieve avatar data");
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
