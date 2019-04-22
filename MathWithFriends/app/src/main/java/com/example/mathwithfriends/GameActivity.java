package com.example.mathwithfriends;

import com.example.utility.EquationSolver;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import java.util.Hashtable;
import java.util.Stack;

public class GameActivity extends Activity {

    Button operandButtons[];
    Button operationButtons[];
    Button selectedButton = null;
    Hashtable<Integer, String> operationMap = new Hashtable<>();

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
        Long result = EquationSolver.solve(equation);
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
