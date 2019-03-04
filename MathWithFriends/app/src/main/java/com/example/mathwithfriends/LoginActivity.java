package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    public final static String TAG = "LoginActivity";

    private TextView userEmail, userPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.et_user_login);
        userPass = findViewById(R.id.et_user_password);
    }

    public void login(View view) {

        String email = userEmail.getText().toString();
        String password = userPass.getText().toString();
        checkForEmptyString(email, password);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private boolean validateEmptyString(String curr){
        if(curr == null || curr.isEmpty())
            return true;
        return false;
    }

    private void checkForEmptyString(String email, String password) {
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
    }

    public void registration(View view) {
        Intent register = new Intent(this, RegistrationActivity.class);
        startActivity(register);
    }
}
