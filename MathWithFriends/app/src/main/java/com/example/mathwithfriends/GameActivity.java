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

import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Hashtable;

public class GameActivity extends Activity {

    private Button operandButtons[];
    private Button operationButtons[];
    private Button selectedButton = null;
    private Hashtable<Integer, String> operationMap = new Hashtable<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference roomsRef = database.getReference("Rooms");
    private String roomID = getIntent().getStringExtra("ROOM_ID");
    private String userID = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());
        assignButtons();
        assignOperationButtonValues();
    }

    // Invoked when the send button is clicked.
    // Computes equation, then sends results out to the server.
    public void clickSend(View view) {
        String[] equation = convertToEquation();
        final long result = EquationSolver.solve(equation);
        final long goalNumber = Long.parseLong(((Button)findViewById(R.id.sendButton)).getText().toString());

        roomsRef.child(roomID).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Room room = mutableData.getValue(Room.class);

                if (room == null) {
                    return Transaction.abort();
                }

                if (result == goalNumber) {
                    if (userID.equals(room.getFirstUserID())) {
                        Long questionsSolved = room.getFirstUserQuestionsSolved();
                        room.setFirstUserQuestionsSolved(questionsSolved + 1);
                    }
                    if (userID.equals(room.getSecondUserID())) {
                        Long questionsSolved = room.getSecondUserQuestionsSolved();
                        room.setSecondUserQuestionsSolved(questionsSolved + 1);
                    }
                }

                mutableData.setValue(room);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    private String[] convertToEquation() {
        String[] equation = new String[9];

        equation[0] = operandButtons[0].getText().toString();

        for (int i = 0; i < 4; i++) {
            equation[i + 1] = operationButtons[i].getText().toString();
            equation[i + 2] = operandButtons[i + 1].getText().toString();
        }

        return equation;
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
        int clickedButtonID = clickedButton.getId();

        String currentOperation = operationMap.get(clickedButtonID);

        if (currentOperation == null) {
            Log.e("GAME", "Operation not found in mapping!");
            return;
        }

        String updatedOperation = updateOperation(currentOperation);

        operationMap.put(clickedButtonID, updatedOperation);
        clickedButton.setText(updatedOperation);
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

    // Maps each button to the plus operation
    private void assignOperationButtonValues() {
        operationMap.put(R.id.operationButton1, "+");
        operationMap.put(R.id.operationButton2, "+");
        operationMap.put(R.id.operationButton3, "+");
        operationMap.put(R.id.operationButton4, "+");
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
    private String updateOperation(String currentOperation) {
        char operation = currentOperation.charAt(0);

        if (operation == '+')
            return "-";

        if (operation == '-')
            return "*";

        if (operation == '*')
            return "/";

        return "+";
    }

    private long getOperand(Button operandButton) {
        return Long.parseLong(operandButton.getText().toString());
    }

    private char getOperation(Button operationButton) {
        return operationButton.getText().charAt(0);
    }
}
