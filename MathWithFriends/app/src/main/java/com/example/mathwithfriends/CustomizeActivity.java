package com.example.mathwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize activities (buttons).
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize Firebase stuffs.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        // button is pressed, inform user and set icon. If home button
        // is pressed, return to home page.
        switch(view.getId()) {
            case R.id.userIcon1:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(1);
                Toast.makeText(CustomizeActivity.this,
                        "Icon 1 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon2:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(2);
                Toast.makeText(CustomizeActivity.this,
                        "Icon 2 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon3:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(3);
                Toast.makeText(CustomizeActivity.this,
                        "Icon 3 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon4:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(4);
                Toast.makeText(CustomizeActivity.this,
                        "Icon 4 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon5:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(5);
                Toast.makeText(CustomizeActivity.this,
                        "Icon 5 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon6:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(6);
                Toast.makeText(CustomizeActivity.this,
                        "Icon 6 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon7:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(7);
                Toast.makeText(CustomizeActivity.this,
                        "Icon 7 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.userIcon8:
                mDatabase.child("Users").child(mAuth.getUid()).child("avatarID").setValue(8);
                Toast.makeText(CustomizeActivity.this,
                        "Icon 8 chosen!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.custom2home:
                Intent intent = new Intent(CustomizeActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}