package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

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

public class CustomizeActivity extends AppCompatActivity {

    private String userID;               // User's ID on Firebase
    private DatabaseReference mDatabase; // To access Firebase
    private Integer currAvatar;          // Currently chosen avatar, updated as user presses buttons
    private Integer gamesPlayed;
    private Integer gamesWon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity/buttons.
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_customize);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize Firebase stuffs to use later.
        userID = FirebaseAuth.getInstance().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Access Firebase and get the user's current avatar and
        // achievement level.
        getCurrAvatar();

        // Check user's achievement level and lock avatars accordingly.
        updateUserAchievements();
    }

    public void getCurrAvatar() {
        DatabaseReference ref = mDatabase.child("Users").child(userID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get user's current avatar from Firebase.
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    Log.e("CustomizeActivity", "Could not retrieve user data.");
                    return;
                }

                currAvatar = user.getAvatarID();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error: Can't retrieve avatar data");
            }
        });
    }

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
                if (dataSnapshot == null) {
                    Log.e("CustomizeActivity", "Error: Can't retrieve user data");
                    return;
                }

                // Get the user, and update the info we need to determine what achievements have been unlocked
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    Log.e("CustomizeActivity", "Error: User not found");
                    return;
                }

                gamesPlayed = user.getGamesPlayed();
                gamesWon = user.getGamesWon();

                Log.d("CustomizeActivity", "played | won: " + String.valueOf(gamesPlayed) + " | " + String.valueOf(gamesWon));

                // Update buttons now that user statistics have been successfully accessed
                checkUserAchievements();
            }
        });
    }

    public void checkUserAchievements() {
        if (gamesWon < 5) {
            ImageButton lockSquare = (ImageButton)findViewById(R.id.squareLvl2);
            lockSquare.setVisibility(View.INVISIBLE);
            ImageButton lockCloud = (ImageButton)findViewById(R.id.cloudLvl2);
            lockCloud.setVisibility(View.INVISIBLE);
            ImageButton lockTriangle = (ImageButton)findViewById(R.id.triangleLvl2);
            lockTriangle.setVisibility(View.INVISIBLE);
            ImageButton lockLump = (ImageButton)findViewById(R.id.lumpLvl2);
            lockLump.setVisibility(View.INVISIBLE);
        }
        else if (gamesWon >= 5 && gamesWon < 10) {
            ImageView unlockSquare = findViewById(R.id.lockedSquare);
            unlockSquare.setVisibility(View.GONE);

            ImageButton lockCloud = (ImageButton)findViewById(R.id.cloudLvl2);
            lockCloud.setVisibility(View.INVISIBLE);
            ImageButton lockTriangle = (ImageButton)findViewById(R.id.triangleLvl2);
            lockTriangle.setVisibility(View.INVISIBLE);
            ImageButton lockLump = (ImageButton)findViewById(R.id.lumpLvl2);
            lockLump.setVisibility(View.INVISIBLE);
        }
        else if (gamesWon >= 10 && gamesWon < 15) {
            ImageView unlockSquare = findViewById(R.id.lockedSquare);
            unlockSquare.setVisibility(View.GONE);
            ImageView unlockTriangle = findViewById(R.id.lockedTriangle);
            unlockTriangle.setVisibility(View.GONE);

            ImageButton lockCloud = (ImageButton)findViewById(R.id.cloudLvl2);
            lockCloud.setVisibility(View.INVISIBLE);
            ImageButton lockLump = (ImageButton)findViewById(R.id.lumpLvl2);
            lockLump.setVisibility(View.INVISIBLE);
        }
        else if (gamesWon > 15) {
            ImageView unlockSquare = findViewById(R.id.lockedSquare);
            unlockSquare.setVisibility(View.GONE);
            ImageView unlockTriangle = findViewById(R.id.lockedTriangle);
            unlockTriangle.setVisibility(View.GONE);
            ImageView unlockCloud = findViewById(R.id.lockedCloud);
            unlockCloud.setVisibility(View.GONE);
            ImageView unlockLump = findViewById(R.id.lockedLump);
            unlockLump.setVisibility(View.GONE);
        }
    }

    // This method is called whenever the user presses any of the avatar
    // icons. Depending on which icon is chosen, update currAvatar with
    // the corresponding icon number and inform the user.
    public void chooseAvatar(View view) {
        switch(view.getId()) {
            case R.id.cloud:
                currAvatar = 1;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 1 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.square:
                currAvatar = 2;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 2 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.triangle:
                currAvatar = 3;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 3 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.lump:
                currAvatar = 4;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 4 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cloudLvl2:
                currAvatar = 5;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 5 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.squareLvl2:
                currAvatar = 6;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 6 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.triangleLvl2:
                currAvatar = 7;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 7 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.lumpLvl2:
                currAvatar = 8;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 8 chosen!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // This method is called when the user presses the home button. Before
    // switching back to the home activity, this method updates the user's
    // avatarID field on Firebase with currAvatar.
    public void returnHome(View view) {
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

                // Update avatar ID on the database from what the user selected
                user.setAvatarID(currAvatar);
                mutableData.setValue(user);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                Intent intent = new Intent(CustomizeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}