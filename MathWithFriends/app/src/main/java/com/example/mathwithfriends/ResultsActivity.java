package com.example.mathwithfriends;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class ResultsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
        setCompletelyFullscreen(getWindow().getDecorView());
    }

    // Sets the screen to immersive fullscreen mode - hiding the system bars.
    private void setCompletelyFullscreen(View view) {
        view.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public void addListenerOnButton() {

        Button playAgain = findViewById(R.id.playAgainButton);
        Button homeButton = findViewById(R.id.homeButton);

        playAgain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Toast.makeText(ResultsActivity.this,
                        "Let's Play Again!", Toast.LENGTH_SHORT).show();

            }

        });

        homeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(ResultsActivity.this, HomeActivity.class);
                startActivity(intent); // Go to Home Screen

            }

        });

    }
}

