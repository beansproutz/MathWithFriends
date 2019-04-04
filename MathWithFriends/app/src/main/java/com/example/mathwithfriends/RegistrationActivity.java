package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    public final static String TAG = "RegistrationTAG";
    private TextView userEmail, userPass, userConPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        userEmail = findViewById(R.id.et_reg_userEmail);
        userPass = findViewById(R.id.et_reg_password);
        // Confirmation password
        userConPass = findViewById(R.id.et_reg_confirm_password);
    }

    public void signUp(View view) {
        String user = userEmail.getText().toString();
        final String password = userPass.getText().toString();
        String confirmPassword = userConPass.getText().toString();
        // Check to see if any of the sign-up fields are empty.
        checkForEmptyString(user, password, confirmPassword);

        // Check to see if the passwords match each other.
        if(validatePasswords(password, confirmPassword)){
            Toast.makeText(RegistrationActivity.this, "Passwords do not match.",
                    Toast.LENGTH_SHORT).show();
            userPass.setText(null);
            userConPass.setText(null);
            return;
        }

        // Check to see if it's a valid email.
        if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()) {
            userEmail.setError("Please enter a valid email");
            userEmail.requestFocus();
            return;
        }

        // Minimum password length for firebase is 6. Thus we are checking for that.

        if(password.length()<6){
            userPass.setError("Password has to be at least 6 characters");
            userPass.requestFocus();
            return;
        }

        Log.d("Username", user);
        Log.d("Password", password);

        mAuth.createUserWithEmailAndPassword(user, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(),
                                    "User Registration successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            postUserData();
                            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                            startActivity(intent); // Change back to Home Screen
                        } else {

                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationActivity.this,
                                    "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


    }

    private void postUserData(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        User user = new User(userEmail.getText().toString(), userPass.getText().toString(), 1);

        mDatabase.child("Users").child(mAuth.getUid()).setValue(user);
    }

    private boolean validatePasswords(String password, String confirmPassword){
        if(!password.equals(confirmPassword)){
            return true;
        }
        return false;
    }

    private void checkForEmptyString(String email, String password, String conPassword) {
        if(validateEmptyString(email)){
            userEmail.setError("Email is required");
            userEmail.requestFocus();
            return;
        }
        if(validateEmptyString(password)){
            userPass.setError("Password is required");
            userPass.requestFocus();
            return;
        }
        if(validateEmptyString(conPassword)){
            userConPass.setError("Confirmation Password is required");
            userConPass.requestFocus();
            return;
        }
    }

    private boolean validateEmptyString(String curr) {
        if(curr == null || curr.isEmpty())
            return true;
        return false;
    }
}
