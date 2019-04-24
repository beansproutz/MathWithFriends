package com.example.utility;

import com.example.mathwithfriends.R;

import java.util.ArrayDeque;

import android.content.Context;
import android.util.Log;

public class EquationSolver {
    private Context context;               // Required for accessing application resources
    private ArrayDeque<Long> operands;     // Holds numbers in the equation to be solved
    private ArrayDeque<String> operations; // Holds operators in the equation to be solved

    public EquationSolver(Context context) {
        this.context = context;
        this.operands = new ArrayDeque<>();
        this.operations = new ArrayDeque<>();
    }

    // Computes the equation formed by the buttons
    // Uses the Shunting Yard algorithm for computation
    public Long solve(String[] equation) {
        Log.d("EquationSolver", "Strings are:");

        // Basically, evaluate multiplication and division
        for (String token : equation) {
            // Token is an operand
            if (Character.isDigit(token.charAt(0))) {
                operands.push(Long.parseLong(token));
            }
            // Token is an operation
            else if (operations.isEmpty()) {
                operations.push(token);
            }
            else {
                String previousOperation = operations.peek();

                while (hasHigherPrecedence(previousOperation)) {
                    long rightOperand = operands.pop();
                    long leftOperand = operands.pop();
                    operands.push(computeOperation(leftOperand, rightOperand, previousOperation));
                    operations.pop();

                    if (operations.isEmpty()) {
                        break;
                    }

                    previousOperation = operations.peek();
                }

                operations.push(token);
            }
        }

        if (!operations.isEmpty() && hasHigherPrecedence(operations.peek())) {
            long rightOperand = operands.pop();
            long leftOperand = operands.pop();
            operands.push(computeOperation(leftOperand, rightOperand, operations.peek()));
            operations.pop();
        }

        // Only addition/subtraction, so read equation from left-to-right
        while (!operations.isEmpty()) {
            long leftOperand = operands.removeLast();
            long rightOperand = operands.removeLast();
            operands.addLast(computeOperation(leftOperand, rightOperand, operations.removeLast()));
        }

        return operands.peek();
    }

    // Computes based on the top two operands and top operation
    // Result gets pushed as an operand
    private long computeOperation(long leftOperand, long rightOperand, String operation) {
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

        return result;
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