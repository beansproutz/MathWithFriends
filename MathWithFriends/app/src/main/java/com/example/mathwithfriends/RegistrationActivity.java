package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.server.User;
import com.example.utility.FullScreenModifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private TextView userName;
    private TextView userEmail;
    private TextView userPass;
    private TextView userConfirmationPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.et_reg_username);
        userEmail = findViewById(R.id.et_reg_email);
        userPass = findViewById(R.id.et_reg_password);
        userConfirmationPass = findViewById(R.id.et_reg_confirm_password);
    }

    public void signUp(View view) {
        String user = userName.getText().toString();
        String email = userEmail.getText().toString();
        String password = userPass.getText().toString();
        String confirmPassword = userConfirmationPass.getText().toString();

        // Check to see if any of the sign-up fields are empty.
        if (checkForEmptyString(user, password, confirmPassword)) {
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(RegistrationActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove leading and trailing whitespaces
        user = user.trim();
        email = email.trim();
        password = password.trim();
        confirmPassword = confirmPassword.trim();

        // Check to see if the passwords match each other.
        if (validatePasswords(password, confirmPassword)) {
            Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            userPass.setText(null);
            userConfirmationPass.setText(null);
            return;
        }

        final int MIN_FIREBASE_PASSWORD_LENGTH = 6;

        if (password.length() < MIN_FIREBASE_PASSWORD_LENGTH){
            userPass.setError("Password must be at least 6 characters");
            userPass.requestFocus();
            return;
        }

        final String userName = user;
        final String userEmail = email;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Sign in success, update UI with the signed-in user's information
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User registration: successful", Toast.LENGTH_SHORT).show();
                            postUserData(userName, userEmail);
                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            finish();
                        }
                        // If sign in fails, display a message to the user.
                        else {
                            Toast.makeText(RegistrationActivity.this, "This username has already been used", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void goBack2Login (View view) {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void postUserData(String userName, String userEmail){
        String userID = mAuth.getUid();

        if (userID == null) {
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        User user = new User(userName, userEmail);

        mDatabase.child("Users").child(mAuth.getUid()).setValue(user);
    }

    private boolean validatePasswords(String password, String confirmPassword){
        return !password.equals(confirmPassword);
    }

    private boolean checkForEmptyString(String username, String password, String conPassword) {
        if (validateEmptyString(username)){
            userName.setError("Username is required");
            userName.requestFocus();
            return true;
        }
        if (validateEmptyString(password)){
            userPass.setError("Password is required");
            userPass.requestFocus();
            return true;
        }
        if (validateEmptyString(conPassword)){
            userConfirmationPass.setError("Confirmation password is required");
            userConfirmationPass.requestFocus();
            return true;
        }

        return false;
    }

    private boolean validateEmptyString(String curr) {
        return (curr == null) || curr.isEmpty();
    }
}
