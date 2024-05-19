package com.example.globrain;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class LevelCompletedActivity extends AppCompatActivity {

    private Button nextLevelButton;
    private Button returnHomeButton;

    private String country;
    private String[] words;
    private String lettersTable;

    private List<MapFragment.Level> levels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_completed);

        Intent intent = getIntent();
        country = intent.getStringExtra("country");
        words = intent.getStringArrayExtra("words");
        lettersTable = intent.getStringExtra("lettersTable");

        levels = MapFragment.getLevels();

        nextLevelButton = findViewById(R.id.nextLevelButton);
        returnHomeButton = findViewById(R.id.returnHomeButton);

        nextLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextLevel();
            }
        });

        returnHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnHome();
            }
        });
    }

    private void startNextLevel() {
        int currentUnlockedLevelIndex = MapFragment.highestUnlockedLevelIndex;

        if (currentUnlockedLevelIndex < levels.size()) {
            MapFragment.Level nextLevel = levels.get(currentUnlockedLevelIndex);
            startGameActivity(nextLevel.getCountry(), nextLevel.getWords(), nextLevel.getLettersTable());
        } else if (currentUnlockedLevelIndex == levels.size()) {
            Toast.makeText(this, "No more levels available", Toast.LENGTH_SHORT).show();
            returnHome();
        } else {
            Toast.makeText(this, "Invalid level index", Toast.LENGTH_SHORT).show();
        }
    }

    private void returnHome() {
        Intent intent = new Intent(LevelCompletedActivity.this, HomeActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void startGameActivity(String country, String[] words, String lettersTable) {
        Intent intent = new Intent(LevelCompletedActivity.this, WordsGameActivity.class);
        intent.putExtra("country", country);
        intent.putExtra("words", words);
        intent.putExtra("lettersTable", lettersTable);
        startActivity(intent);
        finish();
    }

    private void saveUnlockedLevelIndexToFirestore(int unlockedLevelIndex) {
        // Get the current user's document reference in Firestore
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        // Create a data object to update the unlockedLevelIndex field
        Map<String, Object> data = new HashMap<>();
        data.put("unlockedLevelIndex", unlockedLevelIndex);

        // Update the unlockedLevelIndex field in the user's document
        userRef.set(data, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully saved the unlocked level index to Firestore
                        Toast.makeText(LevelCompletedActivity.this, "Unlocked level index saved to Firestore", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save the unlocked level index to Firestore
                        Toast.makeText(LevelCompletedActivity.this, "Failed to save unlocked level index to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}