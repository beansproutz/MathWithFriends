package com.example.mathwithfriends;

import com.example.server.Room;
import com.example.utility.EquationSolver;

import com.example.utility.FullScreenModifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends Activity {
    private final String TAG = "GameActivity";

    private Button operandButtons[];
    private Button operationButtons[];
    private Button selectedButton = null;

    private String roomID;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference roomsRef = database.getReference("Rooms");
    private String userID = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());
        assignButtons();
        generateGame();

        roomID = getIntent().getStringExtra("ROOM_ID");

        new CountDownTimer(90000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, String.valueOf(millisUntilFinished / 1000) + " seconds remaining");
            }

            @Override
            public void onFinish() {
                Log.d(TAG, "Done!");
                Intent intent = new Intent(GameActivity.this, ResultsActivity.class);
                intent.putExtra("ROOM_ID", roomID);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    // Invoked when the send button is clicked.
    // Computes equation, then sends results out to the server.
    public void clickSend(View view) {
        String[] equation = convertToEquation();
        EquationSolver solver = new EquationSolver(this.getApplicationContext());

        final long result = solver.solve(equation);
        final long goalNumber = Long.parseLong(((TextView)findViewById(R.id.goalNumberText)).getText().toString());
        Log.d(TAG, "Result and goal are " + String.valueOf(result) + " " + String.valueOf(goalNumber));

        roomsRef.child(roomID).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // No need to update questions solved if the answer is incorrect
                if (result != goalNumber) {
                    return Transaction.success(mutableData);
                }

                Room room = mutableData.getValue(Room.class);

                if (room == null) {
                    return Transaction.success(mutableData);
                }

                if (userID.equals(room.getFirstUserID())) {
                    Long questionsSolved = room.getFirstUserQuestionsSolved();
                    room.setFirstUserQuestionsSolved(questionsSolved + 1);
                }
                else if (userID.equals(room.getSecondUserID())) {
                    Long questionsSolved = room.getSecondUserQuestionsSolved();
                    room.setSecondUserQuestionsSolved(questionsSolved + 1);
                }

                mutableData.setValue(room);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                // User made their attempt, so create a new set of numbers to play with
                generateGame();
            }
        });
    }

    // Invoked when an operand button is clicked.
    // If there is no selected operand button at the moment, select this.
    // Otherwise, swap values with the selected operand button and deselect.
    public void toggleOperand(View view) {
        Button clickedButton = (Button)view;

        if (selectedButton == null) {
            selectedButton = clickedButton;
        }
        else {
            swapButtonValues(clickedButton, selectedButton);
            selectedButton = null;
        }
    }

    // Invoked when an operation button is clicked.
    // Iterates through operators for the clicked button.
    public void toggleOperation(View view) {
        Button clickedButton = (Button)view;
        String currentOperation = clickedButton.getText().toString();
        String updatedOperation = updateOperation(currentOperation);

        clickedButton.setText(updatedOperation);
    }

    // Converts the operands and operators into a string array
    private String[] convertToEquation() {
        String[] equation = new String[operandButtons.length + operationButtons.length];

        // Place the first operand into the equation
        equation[0] = operandButtons[0].getText().toString();

        // Place the remaining operations and operands
        for (int i = 0; i < operationButtons.length; i++) {
            equation[i * 2 + 1] = operationButtons[i].getText().toString();
            equation[i * 2 + 2] = operandButtons[i + 1].getText().toString();
        }

        return equation;
    }

    // Collects operandButtons into an array for easier access
    private void assignButtons() {
        operandButtons = new Button[5];
        operandButtons[0] = findViewById(R.id.operandButton1);
        operandButtons[1] = findViewById(R.id.operandButton2);
        operandButtons[2] = findViewById(R.id.operandButton3);
        operandButtons[3] = findViewById(R.id.operandButton4);
        operandButtons[4] = findViewById(R.id.operandButton5);

        operationButtons = new Button[4];
        operationButtons[0] = findViewById(R.id.operationButton1);
        operationButtons[1] = findViewById(R.id.operationButton2);
        operationButtons[2] = findViewById(R.id.operationButton3);
        operationButtons[3] = findViewById(R.id.operationButton4);
    }

    // Resets operations, and randomly assigns operands and goal number
    private void generateGame() {
        for (Button operation : operationButtons) {
            operation.setText(getApplicationContext().getString(R.string.addition));
        }

        for (Button operand : operandButtons) {
            Long randOperand = ThreadLocalRandom.current().nextLong(1, 10);
            operand.setText(String.valueOf(randOperand));
        }

        TextView goalNumberView = findViewById(R.id.goalNumberText);
        Long randomGoal = ThreadLocalRandom.current().nextLong(1, 10);
        goalNumberView.setText(String.valueOf(randomGoal));
    }

    // Swaps the values of two operandButtons.
    private void swapButtonValues(Button firstButton, Button secondButton) {
        CharSequence firstButtonText = firstButton.getText();
        CharSequence secondButtonText = secondButton.getText();

        firstButton.setText(secondButtonText);
        secondButton.setText(firstButtonText);
    }

    // Tasty, hardcoded goodness
    // Iterates through operators as: +, -, *, /, then back to +
    private String updateOperation(String operation) {
        String addition = getApplicationContext().getString(R.string.addition);
        String subtraction = getApplicationContext().getString(R.string.subtraction);
        String multiplication = getApplicationContext().getString(R.string.multiplication);
        String division = getApplicationContext().getString(R.string.division);

        if (operation.equals(addition))
            return subtraction;

        if (operation.equals(subtraction))
            return multiplication;

        if (operation.equals(multiplication))
            return division;

        return addition;
    }
}
