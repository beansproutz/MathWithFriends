package com.example.mathwithfriends;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.utility.FullScreenModifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Random;

public class newGameActivity extends AppCompatActivity {
    private final String TAG = "GameActivity";
    final int VALUE_COUNT = 5;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    // private DatabaseReference roomsRef = database.getReference("Rooms");
    // private DatabaseReference usersRef = database.getReference("Users");
    // private String userID = FirebaseAuth.getInstance().getUid();

    private String roomID;
    private int[] initialValues;
    private TextView[] operands;
    private int goalValue;
    private TextView currentView;
    private int previousPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        // roomID = getIntent().getStringExtra("ROOM_ID");

        generateNewGame();
        operands = getOperands();
    }

    public void clickSkip(View view) {
        generateNewGame();
    }

    public void clickReset(View view) {
        resetGame();
    }

    public void clickOperand(View view) {
        TextView clickedView = (TextView)view;

        // Set back to initial position if we clicked current button
        if (clickedOperationPosition(view)) {
            if (viewSelected()) {
                clickedView.setText("");
                operands[previousPosition].setText(clickedView.getText());
                operands[previousPosition].setVisibility(View.VISIBLE);
            }
        }
        else {

        }
    }

    public void clickOperation(View view) {
        Button clickedButton = (Button)view;
        String currentOperation = clickedButton.getText().toString();
        String updatedOperation = getNextOperation(currentOperation);

        clickedButton.setText(updatedOperation);
    }

    private boolean clickedOperationPosition(View view) {
        return (view == findViewById(R.id.firstNumber)) || (view == findViewById(R.id.secondNumber));
    }

    private boolean viewSelected() {
        return currentView != null;
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

        initialValues = new int[VALUE_COUNT];
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
