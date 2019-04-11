package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.Activity;
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

public class LoginActivity extends Activity {

    public final static String TAG = "LoginActivity";

    private TextView userEmail, userPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        userEmail = findViewById(R.id.et_user_login);
        userPass = findViewById(R.id.et_user_password);
    }

    public void login(View view) {

        String email = userEmail.getText().toString();
        String password = userPass.getText().toString();
        if(checkForEmptyString(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // If sign in succeeds, go to Home screen.
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Invalid email/password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private boolean validateEmptyString(String curr){
        if(curr == null || curr.isEmpty())
            return true;
        return false;
    }

    private boolean checkForEmptyString(String email, String password) {
        if(validateEmptyString(email)){
            userEmail.setError("Email is required");
            userEmail.requestFocus();
            return false;
        }
        if(validateEmptyString(password)){
            userPass.setError("Password is required");
            userPass.requestFocus();
            return false;
        }

        return true;
    }

    public void registration(View view) {
        Intent register = new Intent(this, RegistrationActivity.class);
        startActivity(register);
    }
}
