package com.example.utility;

import com.example.mathwithfriends.R;

import java.util.Stack;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class EquationSolver {
    private Context context;          // Required for accessing application resources
    private Stack<Long> operands;     // Holds numbers in the equation to be solved
    private Stack<String> operations; // Holds operators in the equation to be solved

    public EquationSolver(Context context) {
        this.context = context;
        this.operands = new Stack<>();
        this.operations = new Stack<>();
    }

    // Computes the equation formed by the buttons
    // Uses the Shunting Yard algorithm for computation
    public Long solve(String[] equation) {
        Log.d("EquationSolver", "Strings are:");

        for (String token : equation) {
            // Token is an operand
            if (Character.isDigit(token.charAt(0))) {
                operands.push(Long.parseLong(token));
            }
            // Token is an operation
            else if (operations.empty()) {
                operations.push(token);
            }
            else {
                String previousOperation = operations.peek();

                while (hasHigherPrecedence(previousOperation)) {
                    computeOperation(previousOperation);

                    if (operations.empty()) {
                        break;
                    }

                    previousOperation = operations.peek();
                }

                operations.push(token);
            }
        }

        while (!operations.empty()) {
            computeOperation(operations.peek());
        }

        return operands.peek();
    }

    // Computes based on the top two operands and top operation
    // Result gets pushed as an operand
    private void computeOperation(String operation) {
        long rightOperand = operands.pop();
        long leftOperand = operands.pop();
        long result = 0L;

        if (operation.equals(context.getString(R.string.addition))) {
            result = leftOperand + rightOperand;
        }
        else if (operation.equals(context.getString(R.string.subtraction))) {
            result = leftOperand - rightOperand;
        }
        else if (operation.equals(context.getString(R.string.multiplication))) {
            result = leftOperand * rightOperand;
        }
        else if (operation.equals(context.getString(R.string.division))) {
            result = leftOperand / rightOperand;
        }
        else {
            Log.e("EquationSolver", "Improper operation found: " + operation);
        }

        operations.pop();
        operands.push(result);
    }

    // Checks if the previous operation has precedence over the current operation
    // Because equations are so simple, only the previous operation needs to be checked
    // For simple equations, multiplication and division have the highest precedence
    private Boolean hasHigherPrecedence(String previousOperation) {
        String multiplication = context.getString(R.string.multiplication);
        String division = context.getString(R.string.division);

        return previousOperation.equals(multiplication) || previousOperation.equals(division);
    }
}