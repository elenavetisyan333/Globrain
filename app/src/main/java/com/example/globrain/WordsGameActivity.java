package com.example.globrain;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    //    private String[] words = {"EIFFEL", "CROISSANT", "PARIS", "FRANCE", "COGNAC", "BAGUETTE", "DIOR", "MACARON", "WINE", "MONET"};
    private String[] words = {"  EIFFEL", "CROISSANT", "BAGUETTE", "MACARON"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_game);

        grid = findViewById(R.id.grid);
        wordsContainer = findViewById(R.id.words_container);

        createGrid();
        createWords();

        grid.setOnTouchListener(new View.OnTouchListener() {
            float startX = 0;
            float startY = 0;
            float prevX = 0;
            float prevY = 0;
            String direction = "";
            StringBuilder selectedWord = new StringBuilder();
            List<TextView> selectedCells = new ArrayList<>();

            @SuppressLint("ClickableViewAccessibility")
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
                                    selectedWord.append(child.getText());
                                    selectedCells.add(child);
                                    child.setBackgroundColor(Color.WHITE);
                                }
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:

                        for (TextView cell : selectedCells) {
                            cell.setBackgroundColor(Color.TRANSPARENT);
                        }
                        selectedWord.setLength(0);
                        selectedCells.clear();
                        break;
                }

                prevX = x;
                prevY = y;
                return true;
            }
        });


    }



    private void createGrid() {
        for (int i = 0; i < letters.length(); i++) {
            TextView textView = new TextView(this);
            textView.setText(String.valueOf(letters.charAt(i)));
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 10, 10, 10);
            textView.setWidth(50);
            textView.setHeight(50);
            textView.setFontFeatureSettings("@font/candal");
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
}


