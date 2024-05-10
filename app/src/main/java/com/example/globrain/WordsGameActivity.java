package com.example.globrain;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class WordsGameActivity extends AppCompatActivity {

    private GridLayout grid;
    private LinearLayout wordsContainer;

    private String letters = "VIEMACARONBVLV" +
            "UECROISSANTCQS" +
            "CCHTONVGCJFADN" +
            "OMVIVGXRAAVIBA" +
            "GLZYBAGUETTEMU" +
            "NEIFFELHIUEEOJ" +
            "AVSKWDKDSFIRNY" +
            "CUBXINILOUVREB" +
            "MNGTNJBONHAATE" +
            "REYZEBDLRQPCAI" +
            "TPQTWGMUDBAKAD" +
            "CBAMUHWBRYRPDJ" +
            "BGRBLMXEOAINKE" +
            "FRANCEDYNDSMSW";

    private String[] words = {"EIFFEL", "CROISSANT", "BAGUETTE", "MACARON"};

    private StringBuilder selectedWord;
    private List<TextView> selectedCells;
    private List<TextView> foundCells;
    private TextView previousCell;
    private List<TextView> previousFoundCells;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_game);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if(!user.isEmailVerified() && mAuth.getCurrentUser() == null){
            Intent intent = new Intent(WordsGameActivity.this, EmailVerificationActivity.class);
            startActivity(intent);
            finish();
        }

        grid = findViewById(R.id.grid);
        wordsContainer = findViewById(R.id.words_container);

        createGrid();
        createWords();

        selectedWord = new StringBuilder();
        selectedCells = new ArrayList<>();
        foundCells = new ArrayList<>();
        previousCell = null;
        previousFoundCells = new ArrayList<>();

        grid.setOnTouchListener(new View.OnTouchListener() {
            float startX = 0;
            float startY = 0;
            float prevX = 0;
            float prevY = 0;
            String direction = "";

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        startY = y;
                        prevX = x;
                        prevY = y;
                        direction = "";
                        selectedWord.setLength(0);
                        selectedCells.clear();
                        previousCell = null;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (direction.equals("")) {
                            if (Math.abs(x - startX) > Math.abs(y - startY)) {
                                direction = "horizontal";
                            } else {
                                direction = "vertical";
                            }
                        }

                        if (direction.equals("horizontal")) {
                            y = prevY;
                        } else if (direction.equals("vertical")) {
                            x = prevX;
                        }

                        for (int i = 0; i < grid.getChildCount(); i++) {
                            TextView child = (TextView) grid.getChildAt(i);

                            if (x > child.getLeft() && x < child.getRight() && y > child.getTop() && y < child.getBottom()) {
                                if (!selectedCells.contains(child)) {
                                    if (previousCell == null || isAdjacent(child, previousCell)) {
                                        selectedWord.append(child.getText());
                                        selectedCells.add(child);
                                        child.setBackgroundColor(Color.parseColor("#94FFFFFF"));
                                        previousCell = child;
                                    }
                                }
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        // Check if selectedWord is a valid word in the word list
                        String word = selectedWord.toString().trim();
                        if (word.length() > 0) {
                            boolean isWordFound = false;
                            for (int i = 0; i < words.length; i++) {
                                if (words[i].equalsIgnoreCase(word)) {
                                    isWordFound = true;
                                    TextView wordTextView = (TextView) wordsContainer.getChildAt(i);
                                    wordTextView.setPaintFlags(wordTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    wordTextView.setTextColor(Color.GRAY);
                                    wordTextView.setAlpha(0.5f);
                                    break;
                                }
                            }
                            if (isWordFound) {
                                // Word is found in the word list
                                foundCells.addAll(selectedCells);
                                previousFoundCells.addAll(selectedCells);
                                for (TextView cell : selectedCells) {
                                    cell.setBackgroundColor(Color.WHITE);
                                }
                            } else {
                                // Word is not found in the word list
                                for (TextView cell : selectedCells) {
                                    if (!foundCells.contains(cell)) {
                                        cell.setBackgroundColor(Color.TRANSPARENT);
                                    }
                                }
                                for (TextView cell : previousFoundCells) {
                                    cell.setBackgroundColor(Color.WHITE);
                                }
                            }
                        }

                        selectedWord.setLength(0);
                        selectedCells.clear();
                        previousCell = null;
                        break;
                }

                prevX = x;
                prevY = y;
                return true;
            }
        });
    }

    private void createGrid() {
        int gridSize = (int) Math.sqrt(letters.length());

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cellSize = screenWidth / gridSize;

        for (int i = 0; i < letters.length(); i++) {
            TextView textView = new TextView(this);
            textView.setText(String.valueOf(letters.charAt(i)));
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 10, 10, 10);
            textView.setWidth(cellSize);
            textView.setHeight(cellSize);
            grid.addView(textView);
        }
    }

    private void createWords() {
        for (String word : words) {
            TextView textView = new TextView(this);
            textView.setText(word);
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            textView.setLayoutParams(params);

            wordsContainer.addView(textView);
        }
    }

    private boolean isAdjacent(TextView currentCell, TextView previousCell) {
        int currentCellIndex = grid.indexOfChild(currentCell);
        int previousCellIndex = grid.indexOfChild(previousCell);

        int currentRow = currentCellIndex / grid.getColumnCount();
        int currentColumn = currentCellIndex % grid.getColumnCount();

        int previousRow = previousCellIndex / grid.getColumnCount();
        int previousColumn = previousCellIndex % grid.getColumnCount();

        return Math.abs(currentRow - previousRow) <= 1 && Math.abs(currentColumn - previousColumn) <= 1;
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }
}