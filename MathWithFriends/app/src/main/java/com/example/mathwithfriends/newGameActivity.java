package com.example.mathwithfriends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.utility.FullScreenModifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class newGameActivity extends AppCompatActivity {
    private final String TAG = "GameActivity";
    final int VALUE_COUNT = 5;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    // private DatabaseReference roomsRef = database.getReference("Rooms");
    // private DatabaseReference usersRef = database.getReference("Users");
    // private String userID = FirebaseAuth.getInstance().getUid();

    private String roomID;
    private int[] initialValues = new int[VALUE_COUNT];
    private TextView[] operands;
    private long goalValue;
    private TextView currentView = null;
    private int previousPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // roomID = getIntent().getStringExtra("ROOM_ID");

        operands = getOperands();
        generateNewGame();
    }

    public void clickSkip(View view) {
        generateNewGame();
    }

    public void clickReset(View view) {
        resetGame();
    }

    public void clickOperand(View view) {
        TextView clickedView = (TextView)view;

        // If swapping to operand of operation position
        // Make the current view invisible and give the clicked view its value
        if (isOperationPosition(view)) {
            if (currentView == null) {
                return;
            }
            // Swap text if clicking between operation positions
            if (isOperationPosition(currentView)) {
                CharSequence temp = currentView.getText();
                currentView.setText(clickedView.getText());
                clickedView.setText(temp);
            }
            else {
                clickedView.setText(currentView.getText());
                operands[previousPosition].setVisibility(View.INVISIBLE);
                attemptOperation();
            }
            currentView = null;
        }
        // Otherwise, a different operand was clicked
        // Make that operand the current view
        else {
            currentView = clickedView;
            // TODO Unhighlight operands[previousPosition] to indicate there is a new view
            previousPosition = getPosition(clickedView);
            // TODO Highlight operands[previousPosition] to indicate it is current view
        }
    }

    public void clickOperation(View view) {
        Button clickedButton = (Button)view;
        String currentOperation = clickedButton.getText().toString();
        String updatedOperation = getNextOperation(currentOperation);

        clickedButton.setText(updatedOperation);
    }

    private void attemptOperation() {
        TextView leftOp = findViewById(R.id.firstNumber);
        TextView rightOp = findViewById(R.id.secondNumber);

        // Do not go through with operation if at least one operand is missing
        if (leftOp.getText().length() == 0 || rightOp.getText().length() == 0) {
            return;
        }

        long leftVal = Long.valueOf(leftOp.getText().toString());
        long rightVal = Long.valueOf(rightOp.getText().toString());
        long result = performOperation(leftVal, rightVal);

        for (int i = VALUE_COUNT - 1; i >= 0; i--) {
            if (operands[i].getVisibility() == View.INVISIBLE) {
                operands[i].setText(String.valueOf(result));
                operands[i].setVisibility(View.VISIBLE);
                break;
            }
        }

        leftOp.setText("");
        rightOp.setText("");
    }

    private long performOperation(long leftVal, long rightVal) {
        String addition = getApplicationContext().getString(R.string.addition);
        String subtraction = getApplicationContext().getString(R.string.subtraction);
        String multiplication = getApplicationContext().getString(R.string.multiplication);
        String division = getApplicationContext().getString(R.string.division);
        String operation = ((TextView)findViewById(R.id.operation)).getText().toString();

        long result = 0;

        if (operation.equals(addition)) {
            result = leftVal + rightVal;
        }
        else if (operation.equals(subtraction)) {
            result = leftVal - rightVal;
        }
        else if (operation.equals(multiplication)) {
            result = leftVal * rightVal;
        }
        else if (operation.equals(division)) {
            result = leftVal / rightVal;
        }

        return result;
    }

    private int getPosition(TextView view) {
        for (int i = 0; i < VALUE_COUNT; i++) {
            if (view == operands[i]) {
                return i;
            }
        }
        return -1;
    }

    private boolean isOperationPosition(View view) {
        return (view == findViewById(R.id.firstNumber)) || (view == findViewById(R.id.secondNumber));
    }

    private String getNextOperation(String operation) {
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

    private void generateNewGame() {
        generateValues();
        updateOperands();
        updateOperation();
        updateGoal();
    }

    private void resetGame() {
        updateOperands();
        updateOperation();
    }

    // Random generates values and updates the operand text views
    private void generateValues() {
        final int MIN_VALUE = 1;
        final int MAX_VALUE = 9;

        Random rand = new Random();

        for (int i = 0; i < VALUE_COUNT; i++) {
            initialValues[i] = rand.nextInt(MAX_VALUE) + MIN_VALUE;
        }

        goalValue = rand.nextInt(MAX_VALUE) + MIN_VALUE;
    }

    // Updates the operand text views to initial operand values
    private void updateOperands() {
        for (int i = 0; i < VALUE_COUNT; i++) {
            String valueString = String.valueOf(initialValues[i]);
            operands[i].setText(valueString);
            operands[i].setVisibility(View.VISIBLE);
        }
    }

    // Returns array of operands
    private TextView[] getOperands() {
        TextView[] operands = new TextView[VALUE_COUNT];

        operands[0] = findViewById(R.id.operand1);
        operands[1] = findViewById(R.id.operand2);
        operands[2] = findViewById(R.id.operand3);
        operands[3] = findViewById(R.id.operand4);
        operands[4] = findViewById(R.id.operand5);

        return operands;
    }

    // Resets operation to the default - addition
    private void updateOperation() {
        TextView operation = findViewById(R.id.operation);
        operation.setText(R.string.addition);
    }

    // Updates the goal text to initial goal
    private void updateGoal() {
        String goalString = String.valueOf(goalValue);
        TextView goal = findViewById(R.id.goal);
        goal.setText(goalString);
    }
}
