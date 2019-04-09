package com.example.mathwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomizeActivity extends AppCompatActivity
        implements View.OnClickListener {

    private ImageButton iconButton1;    // User icon choices 1 through 8
    private ImageButton iconButton2;
    private ImageButton iconButton3;
    private ImageButton iconButton4;
    private ImageButton iconButton5;
    private ImageButton iconButton6;
    private ImageButton iconButton7;
    private ImageButton iconButton8;
    private Button customToHomeButton;  // Return to home page button

    private FirebaseAuth mAuth;         // To get user id
    private DatabaseReference mDatabase;// To write to avatarID field

    private Integer currAvatar;         // Currently chosen avatar, updated as user presses buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activities (buttons).
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize Firebase stuffs.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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

        iconButton1 = findViewById(R.id.userIcon1);
        iconButton1.setOnClickListener(this);
        iconButton2 = findViewById(R.id.userIcon2);
        iconButton2.setOnClickListener(this);
        iconButton3 = findViewById(R.id.userIcon3);
        iconButton3.setOnClickListener(this);
        iconButton4 = findViewById(R.id.userIcon4);
        iconButton4.setOnClickListener(this);
        iconButton5 = findViewById(R.id.userIcon5);
        iconButton5.setOnClickListener(this);
        iconButton6 = findViewById(R.id.userIcon6);
        iconButton6.setOnClickListener(this);
        iconButton7 = findViewById(R.id.userIcon7);
        iconButton7.setOnClickListener(this);
        iconButton8 = findViewById(R.id.userIcon8);
        iconButton8.setOnClickListener(this);
        customToHomeButton = findViewById(R.id.custom2home);
        customToHomeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Perform tasks based on which button is pressed. If an icon
        // button is pressed, inform user and store the corresponding
        // icon number temporarily. If home button is pressed, update the
        // avatarID field in Firebase return to home page.
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
            case R.id.custom2home:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(currAvatar);
                Intent intent = new Intent(CustomizeActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}