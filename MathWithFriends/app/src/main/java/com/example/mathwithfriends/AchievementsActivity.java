package com.example.mathwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.utility.FullScreenModifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AchievementsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

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
        checkAchievements();
    }


    private void checkAchievements()
    {
//        ImageView myImage = findViewById(R.id.achievement5winIcon);
//        myImage.setVisibility();
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
