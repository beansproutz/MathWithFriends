package com.example.utility;

import com.example.mathwithfriends.R;

import java.util.Stack;
import android.content.res.Resources;
import android.util.Log;

public class EquationSolver {
    private static Stack<Long> operands = new Stack<>();
    private static Stack<String> operations = new Stack<>();

    // Computes the equation formed by the buttons
    // Uses the Shunting Yard algorithm for computation
    public static Long solve(String[] equation) {
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

                while (hasHigherPrecedence(previousOperation, token)) {
                    computeOperation(previousOperation);

                    if (operations.empty()) {
                        break;
                    }

                    previousOperation = operations.pop();
                }

                operations.push(token);
            }
        }

        while (!operations.empty()) {
            computeOperation(operations.pop());
        }

        return operands.peek();
    }

    static private void computeOperation(String operation) {
        long rightOperand = operands.pop();
        long leftOperand = operands.pop();
        long result = 0L;

        Resources resources = Resources.getSystem();

        if (operation.equals(resources.getString(R.string.addition))) {
            result = leftOperand + rightOperand;
        }
        else if (operation.equals(resources.getString(R.string.subtraction))) {
            result = leftOperand - rightOperand;
        }
        else if (operation.equals(resources.getString(R.string.multiplication))) {
            result = leftOperand * rightOperand;
        }
        else if (operation.equals(resources.getString(R.string.division))) {
            result = leftOperand / rightOperand;
        }
        else {
            Log.e("EquationSolver", "Improper operation found: " + operation);
        }

        operands.push(result);
    }

    // Multiplication, Division > Addition, Subtraction
    // Example 1: 3 * 4 * 5 + 1 = 12 * 5 + 1 = 60 + 1 = 61
    // Example 2: 6 / 3 / 2 * 4 = 2 / 2 * 4 = 1 * 4 = 4
    // Example 3: 1 + 2 * 3 - 4 = 1 + 6 - 4 = 7 - 4 = 3
    static private Boolean hasHigherPrecedence(String leftOperation, String rightOperation) {
        Resources resources = Resources.getSystem();

        String multiplication = resources.getString(R.string.multiplication);
        String division = resources.getString(R.string.division);

        return leftOperation.equals(multiplication) || leftOperation.equals(division);
    }
}