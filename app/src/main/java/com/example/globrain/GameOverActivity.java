package com.example.globrain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    private Button tryAgainButton;
    private Button returnHomeButton;

    private String country;
    private String[] words;
    private String lettersTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        country = intent.getStringExtra("country");
        words = intent.getStringArrayExtra("words");
        lettersTable = intent.getStringExtra("lettersTable");

        tryAgainButton = findViewById(R.id.tryAgainButton);
        returnHomeButton = findViewById(R.id.returnHomeButton);

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity(country, words, lettersTable);
            }
        });

        returnHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    private void startGameActivity(String country, String[] words, String lettersTable){
        Intent intent = new Intent(GameOverActivity.this, WordsGameActivity.class);
        intent.putExtra("country", country);
        intent.putExtra("words", words);
        intent.putExtra("lettersTable", lettersTable);
        startActivity(intent);
    }
}