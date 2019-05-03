package com.example.mathwithfriends;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.server.Room;
import com.example.utility.FullScreenModifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private final String TAG = "GameActivity";
    final int VALUE_COUNT = 5;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String userID = FirebaseAuth.getInstance().getUid();

    private String roomID;
    private int[] initialValues;
    private TextView[] operands;
    private long goalValue;
    private TextView currentView;
    private int previousPosition;
    private boolean isFirstUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        FullScreenModifier.setFullscreen(getWindow().getDecorView());

        roomID = getIntent().getStringExtra("ROOM_ID");
        operands = new TextView[] {
                findViewById(R.id.operand1),
                findViewById(R.id.operand2),
                findViewById(R.id.operand3),
                findViewById(R.id.operand4),
                findViewById(R.id.operand5)
        };
        initialValues = new int[VALUE_COUNT];

        generateNewGame();
        startGame();

        // Set up listener for changes in life points
        database.getReference("Rooms").child(roomID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer life;

                if (isFirstUser) {
                    life = dataSnapshot.child("firstUserLife").getValue(Integer.class);
                }
                else {
                    life = dataSnapshot.child("secondUserLife").getValue(Integer.class);
                }

                if (life == null) {
                    Log.e(TAG, "Failed to obtain life points of player");
                    return;
                }

                if (life == 0) {
                    Intent intent = new Intent(GameActivity.this, ResultsActivity.class);
                    intent.putExtra("RESULT", false);
                    intent.putExtra("ROOM_ID", roomID);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // Invoked when the SKIP button is clicked
    // Generates a new game
    public void clickSkip(View view) {
        generateNewGame();
    }

    // Invoked when the RESET button is clicked
    // Resets this game to its initial state
    public void clickReset(View view) {
        resetGame();
    }

    // Invoked when any possible operand position is clicked
    // Places a selected operand into a position
    // If that position was taken, swap it out
    public void clickOperand(View view) {
        TextView clickedView = (TextView)view;

        // If swapping to operand of operation position
        // Make the current view invisible and give the clicked view its value
        if (isOperationPosition(view)) {
            if (currentView == null) {
                return;
            }

            if (currentView == clickedView) {
                resetOperand(clickedView);
            }
            else if (isOperationPosition(currentView)) {
                CharSequence temp = currentView.getText();
                currentView.setText(clickedView.getText());
                clickedView.setText(temp);
            }
            else if (clickedView.getText().length() == 0) {
                clickedView.setText(currentView.getText());
                operands[previousPosition].setVisibility(View.INVISIBLE);
                attemptOperation();
            }
            else {
                resetOperand(clickedView);
                clickedView.setText(currentView.getText());
                currentView.setVisibility(View.INVISIBLE);
            }

            currentView = null;
        }

        if (currentView == null) {
            currentView = clickedView;
            previousPosition = getPosition(clickedView);
            // TODO Highlight operands[previousPosition] to indicate it is current view
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

    // Invoked when the operation button is clicked
    // Cycles through operations (+ > - > × > ÷ > + > …)
    public void clickOperation(View view) {
        Button clickedButton = (Button)view;
        String currentOperation = clickedButton.getText().toString();
        String updatedOperation = getNextOperation(currentOperation);

        clickedButton.setText(updatedOperation);
    }

    private void resetOperand(TextView view) {
        for (int i = VALUE_COUNT - 1; i >= 0; i--) {
            if (operands[i].getVisibility() == View.INVISIBLE) {
                operands[i].setText(view.getText());
                operands[i].setVisibility(View.VISIBLE);
                view.setText("");
                break;
            }
        }
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

        if (hasSucceeded()) {
            updateOpponentLife();
            generateNewGame();
        }
    }

    private void updateOpponentLife() {
        database.getReference("Rooms").child(roomID).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Long newLife;

                if (isFirstUser) {
                    newLife = mutableData.child("secondUserLife").getValue(Long.class);
                }
                else {
                    newLife = mutableData.child("firstUserLife").getValue(Long.class);
                }

                if (newLife == null) {
                    return Transaction.success(mutableData);
                }

                if (isFirstUser) {
                    mutableData.child("secondUserLife").setValue(newLife - 1);
                }
                else {
                    mutableData.child("firstUserLife").setValue(newLife - 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }

                Long opponentLife;

                if (isFirstUser) {
                    opponentLife = dataSnapshot.child("secondUserLife").getValue(Long.class);
                }
                else {
                    opponentLife = dataSnapshot.child("firstUserLife").getValue(Long.class);
                }

                if (opponentLife == null) {
                    Log.e(TAG, "Opponent life was null");
                    return;
                }

                if (opponentLife == 0) {
                    Intent intent = new Intent(GameActivity.this, ResultsActivity.class);
                    intent.putExtra("RESULT", true);
                    intent.putExtra("ROOM_ID", roomID);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean hasSucceeded() {
        boolean singleOperandVisible = false;
        long answer = 0;

        for (TextView operand : operands) {
            if (operand.getVisibility() == View.VISIBLE) {
                if (singleOperandVisible) {
                    return false;
                }
                singleOperandVisible = true;
                answer = Long.valueOf(operand.getText().toString());
            }
        }

        return answer == goalValue;
    }

    private long performOperation(long leftVal, long rightVal) {
        String operation = ((TextView)findViewById(R.id.operation)).getText().toString();
        long result = 0;

        if (operation.equals(getApplicationContext().getString(R.string.addition))) {
            result = leftVal + rightVal;
        }
        else if (operation.equals(getApplicationContext().getString(R.string.subtraction))) {
            result = leftVal - rightVal;
        }
        else if (operation.equals(getApplicationContext().getString(R.string.multiplication))) {
            result = leftVal * rightVal;
        }
        else if (operation.equals(getApplicationContext().getString(R.string.division))) {
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
        setupViews();
    }

    private void resetGame() {
        setupViews();
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

    private void setupViews() {
        // Update operands
        for (int i = 0; i < VALUE_COUNT; i++) {
            String valueString = String.valueOf(initialValues[i]);
            operands[i].setText(valueString);
            operands[i].setVisibility(View.VISIBLE);
        }

        // Reset operation to addition by default
        TextView operation = findViewById(R.id.operation);
        operation.setText(R.string.addition);

        // Update goal
        String goalString = String.valueOf(goalValue);
        TextView goal = findViewById(R.id.goal);
        goal.setText(goalString);

        // Reset operation positions
        ((TextView)findViewById(R.id.firstNumber)).setText("");
        ((TextView)findViewById(R.id.secondNumber)).setText("");
    }

    private void startGame() {
        DatabaseReference roomRef = database.getReference("Rooms").child(roomID);

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
                    Log.e(TAG, "Game room not found for user " + userID);
                    return;
                }

                isFirstUser = userID.equals(room.getFirstUserID());
                getAvatars();
            }
        });
    }

    private void getAvatars() {
        DatabaseReference roomRef = database.getReference("Rooms").child(roomID);

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
        database.getReference("Users").runTransaction(new Transaction.Handler() {
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

                ImageView playerAvatar = findViewById(R.id.player1ImageView);
                ImageView opponentAvatar = findViewById(R.id.player2ImageView);

                playerAvatar.setImageResource(findAvatarFromID(playerAvatarID));
                opponentAvatar.setImageResource(findAvatarFromID(opponentAvatarID));
            }
        });
    }

    private int findAvatarFromID(int avatarID) {
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
}