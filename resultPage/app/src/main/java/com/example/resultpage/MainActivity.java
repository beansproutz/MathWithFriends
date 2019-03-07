package com.example.resultpage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;

import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
    }

    public void addListenerOnButton() {

        ImageButton playAgain = (ImageButton) findViewById(R.id.playAgainButton);
        ImageButton homeButton = (ImageButton) findViewById(R.id.homeButton);

        playAgain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Toast.makeText(MainActivity.this,
                    "Let's Play Again!", Toast.LENGTH_SHORT).show();

            }

        });

        homeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Toast.makeText(MainActivity.this,
                        "Let's Go Home Then...", Toast.LENGTH_SHORT).show();

            }

        });

    }
}
