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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
    private DatabaseReference usersRef = database.getReference("Users");
    private String userID = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        roomID = getIntent().getStringExtra("ROOM_ID");

        assignButtons();
        startGame();

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
        final long goalNumber = Long.parseLong(((TextView)findViewById(R.id.goal)).getText().toString());
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

    private void startGame() {
        DatabaseReference roomRef = roomsRef.child(roomID);

        roomRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                // Just accessing room data
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }

                Room room = dataSnapshot.getValue(Room.class);

                if (room == null) {
                    Log.e(TAG, "Game room not found for user " + userID);
                    return;
                }

                boolean isFirstUser = userID.equals(room.getFirstUserID());
                getAvatars(isFirstUser);
            }
        });
    }

    private void getAvatars(final boolean isFirstUser) {
        DatabaseReference roomRef = roomsRef.child(roomID);

        roomRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }

                Room room = dataSnapshot.getValue(Room.class);

                if (room == null) {
                    return;
                }

                String playerID;
                String opponentID;

                if (isFirstUser) {
                    playerID = room.getFirstUserID();
                    opponentID = room.getSecondUserID();
                }
                else {
                    playerID = room.getSecondUserID();
                    opponentID = room.getFirstUserID();
                }

                setAvatars(playerID, opponentID);
            }
        });
    }

    private void setAvatars(final String playerID, final String opponentID) {
        usersRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }

                Integer playerAvatarID = dataSnapshot.child(playerID).child("avatarID").getValue(Integer.class);
                Integer opponentAvatarID = dataSnapshot.child(opponentID).child("avatarID").getValue(Integer.class);

                if (playerAvatarID == null || opponentAvatarID == null) {
                    return;
                }

                ImageView playerAvatar = findViewById(R.id.player1Avatar);
                ImageView opponentAvatar = findViewById(R.id.player2Avatar);

                playerAvatar.setImageResource(getAvatar(playerAvatarID));
                opponentAvatar.setImageResource(getAvatar(opponentAvatarID));
            }
        });
    }

    public int getAvatar(Integer avatarID) {
        int avatarSource = 0;

        switch(avatarID) {
            case 1:
                avatarSource = R.drawable.cloud_regular;
                break;
            case 2:
                avatarSource = R.drawable.square_regular;
                break;
            case 3:
                avatarSource = R.drawable.triangle_regular;
                break;
            case 4:
                avatarSource = R.drawable.circle_regular;
                break;
            case 5:
                avatarSource = R.drawable.cloud_special;
                break;
            case 6:
                avatarSource = R.drawable.square_special;
                break;
            case 7:
                avatarSource = R.drawable.triangle_special;
                break;
            case 8:
                avatarSource = R.drawable.circle_special;
                break;
            default:
                /* EMPTY */
        }

        return avatarSource;
    }

    // Resets operations, and randomly assigns operands and goal number
    private void generateGame() {
        for (Button operation : operationButtons) {
            operation.setText(getApplicationContext().getString(R.string.addition));
        }

        final int LOWER_GAME_NUMBER_BOUND = 1;
        final int UPPER_GAME_NUMBER_BOUND = 10;

        for (Button operand : operandButtons) {
            Long randOperand = ThreadLocalRandom.current().nextLong(LOWER_GAME_NUMBER_BOUND, UPPER_GAME_NUMBER_BOUND);
            operand.setText(String.valueOf(randOperand));
        }

        TextView goalNumberView = findViewById(R.id.goal);
        Long randomGoal = ThreadLocalRandom.current().nextLong(LOWER_GAME_NUMBER_BOUND, UPPER_GAME_NUMBER_BOUND);
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
