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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LevelCompletedActivity extends AppCompatActivity {

    private Button nextLevelButton;
    private Button returnHomeButton;

    private String country;
    private String[] words;
    private String lettersTable;

    private int lastPlayedLevelIndex;
    private int highestUnlockedLevelIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_completed);

        fetchHighestUnlockedLevelIndexFromFirestore();

        Intent intent = getIntent();
        country = intent.getStringExtra("country");
        words = intent.getStringArrayExtra("words");
        lettersTable = intent.getStringExtra("lettersTable");
        highestUnlockedLevelIndex = intent.getIntExtra("highestUnlockedLevelIndex", 0);

        lastPlayedLevelIndex = getLastPlayedLevelIndex();

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
//    private void startNextLevel() {
//        if (lastPlayedLevelIndex < MapFragment.getLevels().size() - 1) {
//            lastPlayedLevelIndex++;
//            MapFragment.Level nextLevel = MapFragment.getLevels().get(lastPlayedLevelIndex);
//            startGameActivity(nextLevel.getCountry(), nextLevel.getWords(), nextLevel.getLettersTable());
//            saveLastPlayedLevelIndex(lastPlayedLevelIndex);
//
//            if (lastPlayedLevelIndex > highestUnlockedLevelIndex) {
//                highestUnlockedLevelIndex = lastPlayedLevelIndex;
//                MapFragment.saveUnlockedLevelIndexToFirestore(highestUnlockedLevelIndex);
//            }
//        } else {
//            Toast.makeText(this, "No more levels available", Toast.LENGTH_SHORT).show();
//            returnHome();
//        }
//    }

    private void startNextLevel() {
        if (lastPlayedLevelIndex < MapFragment.getLevels().size() - 1) {
            lastPlayedLevelIndex++;
        } else {
            Toast.makeText(this, "No more levels available", Toast.LENGTH_SHORT).show();
            returnHome();
            return;
        }

        MapFragment.Level nextLevel = MapFragment.getLevels().get(lastPlayedLevelIndex);
        startGameActivity(nextLevel.getCountry(), nextLevel.getWords(), nextLevel.getLettersTable());
        saveLastPlayedLevelIndex(lastPlayedLevelIndex);

        if (lastPlayedLevelIndex > highestUnlockedLevelIndex) {
            highestUnlockedLevelIndex = lastPlayedLevelIndex;
            MapFragment.saveUnlockedLevelIndexToFirestore(highestUnlockedLevelIndex);
        }
    }

    private void fetchHighestUnlockedLevelIndexFromFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Integer unlockedLevelIndex = documentSnapshot.getLong("highestUnlockedLevelIndex").intValue();
                            highestUnlockedLevelIndex = unlockedLevelIndex;
                            lastPlayedLevelIndex = highestUnlockedLevelIndex;
                        } else {
                            highestUnlockedLevelIndex = 0; // Default to 0 if the document doesn't exist
                            lastPlayedLevelIndex = highestUnlockedLevelIndex;
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LevelCompletedActivity.this, "Failed to fetch unlocked level index from Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int getLastPlayedLevelIndex() {
        return highestUnlockedLevelIndex;
    }

    private void saveLastPlayedLevelIndex(int index) {
        // No need to save last played level index in this case
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
}