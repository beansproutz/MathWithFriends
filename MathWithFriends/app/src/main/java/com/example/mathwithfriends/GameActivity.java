package com.example.mathwithfriends;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import java.util.Hashtable;

public class GameActivity extends Activity {

    Button selectedButton = null;
    Hashtable<Integer, String> operationMap = new Hashtable<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setCompletelyFullscreen(getWindow().getDecorView());
        assignOperationButtonValues();
    }

    // Sets the screen to immersive fullscreen mode - hiding the system bars.
    private void setCompletelyFullscreen(View view) {
        view.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
              | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
              | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    // Assigns each
    private void assignOperationButtonValues() {
        operationMap.put(R.id.operationButton1, "+");
        operationMap.put(R.id.operationButton2, "+");
        operationMap.put(R.id.operationButton3, "+");
        operationMap.put(R.id.operationButton4, "+");
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

    // Swaps the values of two buttons.
    private void swapButtonValues(Button firstButton, Button secondButton) {
        CharSequence firstButtonText = firstButton.getText();
        CharSequence secondButtonText = secondButton.getText();

        firstButton.setText(secondButtonText);
        secondButton.setText(firstButtonText);
    }

    // Invoked when an operation button is clicked.
    // Iterates through operators for the clicked button.
    public void toggleOperation(View view) {
        Button clickedButton = (Button)view;
        int clickedButtonID = clickedButton.getId();

        String currentOperation = operationMap.get(clickedButtonID);
        String updatedOperation = updateOperation(currentOperation);

        operationMap.put(clickedButtonID, updatedOperation);
        clickedButton.setText(updatedOperation);
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
}
