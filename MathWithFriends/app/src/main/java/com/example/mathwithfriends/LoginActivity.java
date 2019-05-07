package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.server.User;
import com.example.utility.FullScreenModifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends Activity {

    public final static String TAG = "LoginActivity";

    private TextView userLogin;
    private TextView userPass;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());
        FirebaseApp.initializeApp(this);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userLogin = findViewById(R.id.et_user_login);
        userPass = findViewById(R.id.et_user_password);
    }

    public void login(View view) {
        String loginCredential = userLogin.getText().toString();
        String password = userPass.getText().toString();

        if (checkForEmptyString(loginCredential, password)) {
            loginCredential = loginCredential.trim();
            password = password.trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(loginCredential).matches()) {
                loginWithUsernameAndPassword(loginCredential, password);
            }
            else {
                loginWithEmailAndPassword(loginCredential, password);
            }
        }
    }

    private void loginWithUsernameAndPassword(final String userName, final String password) {
        database.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    if (user == null) {
                        continue;
                    }

                    if (user.getUserName().equals(userName)) {
                        loginWithEmailAndPassword(user.getUserEmail(), password);
                        return;
                    }
                }

                Toast.makeText(LoginActivity.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loginWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            openHomeScreen();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Invalid login credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openHomeScreen() {
        String userID = mAuth.getUid();

        if (userID == null) {
            Log.e(TAG, "Failed to authenticate user despite successful login.");
            return;
        }

        DatabaseReference userRef = database.getReference("Users").child(userID);

        userRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                User user = mutableData.getValue(User.class);

                if (user == null) {
                    user = new User();
                }

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                // Sign in succeeded and user is successfully up on the database, so go to the Home screen
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }
        });
    }

    private boolean validateEmptyString(String curr) {
        return (curr == null) || curr.isEmpty();
    }

    private boolean checkForEmptyString(String email, String password) {
        if (validateEmptyString(email)) {
            userLogin.setError("Email/username is required");
            userLogin.requestFocus();
            return false;
        }
        if (validateEmptyString(password)) {
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
