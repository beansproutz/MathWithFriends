package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class CustomizeActivity extends AppCompatActivity {

    private String userID = FirebaseAuth.getInstance().getUid();

    private DatabaseReference mDatabase; // To write to avatarID field
    private Integer currAvatar;          // Currently chosen avatar, updated as user presses buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity/buttons.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize Firebase stuffs to use later.
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Access Firebase and get the user's current avatar.
        getCurrAvatar();
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

    // This method is called whenever the user presses any of the avatar
    // icons. Depending on which icon is chosen, update currAvatar with
    // the corresponding icon number and inform the user.
    public void chooseAvatar(View view) {
        switch(view.getId()) {
            case R.id.userIcon1:
                currAvatar = 1;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 1 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon2:
                currAvatar = 2;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 2 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon3:
                currAvatar = 3;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 3 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon4:
                currAvatar = 4;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 4 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon5:
                currAvatar = 5;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 5 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon6:
                currAvatar = 6;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 6 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon7:
                currAvatar = 7;
                Toast.makeText(CustomizeActivity.this,
                        "Icon 7 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon8:
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