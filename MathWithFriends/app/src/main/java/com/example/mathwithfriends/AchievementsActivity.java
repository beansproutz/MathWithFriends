package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class AchievementsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize activities (buttons).

        // Initialize Firebase stuffs.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Check if achievements are locked
        checkUserAchievements();
    }

    private void checkUserAchievements() {
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
                    Log.e("AchievementsActivity", "Error: Can't retrieve user data");
                    return;
                }

                // Get the user, and update the info we need to determine what achievements have been unlocked
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    Log.e("AchievementsActivity", "Error: User not found");
                    return;
                }

                // Check which achievements were unlocked if there were any
                View fiveWinLock = findViewById(R.id.achievement5winsLocked);
                fiveWinLock.setVisibility(View.VISIBLE);

                View tenWinLock = findViewById(R.id.achievement10winsLocked);
                fiveWinLock.setVisibility(View.VISIBLE);

                View fifteenWinLock = findViewById(R.id.achievement15winsLocked);
                fiveWinLock.setVisibility(View.VISIBLE);

                // Check if
                if (user.getGamesWon() == 5) {
                    fiveWinLock.setVisibility(View.INVISIBLE);
                }
                if (user.getGamesWon() == 10) {
                    tenWinLock.setVisibility(View.INVISIBLE);
                }
                if (user.getGamesWon() == 15) {
                    fifteenWinLock.setVisibility(View.INVISIBLE);
                }

            }
        });
    }

    // Sends User back to Home Screen when "Home" button is pressed
    public void onAchievementHomeClick(View view) {
        Intent intent = new Intent(AchievementsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    // Test button to "unlock" achievements
    public void testUnlockClick(View view) {

        // For testing: when clicked should "unlock" achievement of 5 wins
        View fiveWins = findViewById(R.id.achievement5winsLocked);
        fiveWins.setVisibility(View.INVISIBLE);

    }

}
