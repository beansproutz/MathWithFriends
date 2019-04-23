package com.example.mathwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomizeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;          // To get user id
    private DatabaseReference mDatabase; // To write to avatarID field
    private Integer currAvatar;             // Currently chosen avatar, updated as user presses buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activity/buttons.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize Firebase stuffs to use later.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Access Firebase and get the user's current avatar.
        getCurrAvatar();
    }

    public void getCurrAvatar() {
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
        mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(currAvatar);
        Intent intent = new Intent(CustomizeActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}