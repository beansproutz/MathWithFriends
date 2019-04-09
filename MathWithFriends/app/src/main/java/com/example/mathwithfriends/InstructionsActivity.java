package com.example.mathwithfriends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class InstructionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());
    }

    // Invoked when the queue button is clicked.
    // Starts queueing for the game from the instruction page.
    public void onInstructionsQueueClick(View view) {
        Intent intent = new Intent(InstructionsActivity.this, GameActivity.class);
        startActivity(intent);
    }

    public void goBack2Home (View view) {
        Intent intent = new Intent(InstructionsActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}
