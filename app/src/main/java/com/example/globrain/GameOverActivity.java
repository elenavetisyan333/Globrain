package com.example.globrain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    private Button tryAgainButton;
    private Button returnHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        tryAgainButton = findViewById(R.id.tryAgainButton);
        returnHomeButton = findViewById(R.id.returnHomeButton);

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, WordsGameActivity.class);
                startActivity(intent);
                finish();
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
}