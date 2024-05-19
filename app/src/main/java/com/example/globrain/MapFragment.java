package com.example.globrain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {

    private List<Level> levels;
    private int currentUnlockedLevelIndex = 0;
    protected static int highestUnlockedLevelIndex = 0;

    private ImageButton franceButton;
    private ImageButton armeniaButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        levels = getLevels();
        currentUnlockedLevelIndex = getUnlockedLevelIndex();
        getUnlockedLevelIndexFromFirestore();

        franceButton = view.findViewById(R.id.FranceIcon);
        armeniaButton = view.findViewById(R.id.ArmeniaIcon);

        franceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUnlockedLevelIndex >= 0) {
                    startGameActivity(levels.get(0));
                } else {
                    Toast.makeText(getActivity(), "Level is locked. Complete previous levels to unlock.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        armeniaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUnlockedLevelIndex >= 1) {
                    startGameActivity(levels.get(1));
                } else {
                    Toast.makeText(getActivity(), "Level is locked. Complete previous levels to unlock.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void startGameActivity(Level level) {
        Intent intent = new Intent(getActivity(), WordsGameActivity.class);
        intent.putExtra("country", level.getCountry());
        intent.putExtra("words", level.getWords());
        intent.putExtra("lettersTable", level.getLettersTable());
        startActivity(intent);
    }

    private void saveUnlockedLevelIndex(int unlockedLevelIndex) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("UnlockedLevelIndex", unlockedLevelIndex);
        editor.apply();
    }
    private int getUnlockedLevelIndex() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("UnlockedLevelIndex", 0);
    }


    private void getUnlockedLevelIndexFromFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Integer unlockedLevelIndex = documentSnapshot.getLong("unlockedLevelIndex").intValue();

                            if (unlockedLevelIndex > highestUnlockedLevelIndex) {
                                highestUnlockedLevelIndex = unlockedLevelIndex;
                                saveUnlockedLevelIndex(unlockedLevelIndex);
                            }
                        } else {
                            // Document does not exist
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to retrieve the unlocked level index from Firestore
                    }
                });
    }

    protected static class Level {
        private String country;
        private String[] words;
        private String lettersTable;

        public Level(String country, String[] words, String lettersTable) {
            this.country = country;
            this.words = words;
            this.lettersTable = lettersTable;
        }

        public String getCountry() {
            return country;
        }

        public String[] getWords() {
            return words;
        }

        public String getLettersTable() {
            return lettersTable;
        }
    }

    protected static List<Level> getLevels() {
        List<Level> levels = new ArrayList<>();
        levels.add(new Level("France", new String[]{"EIFFEL", "CROISSANT", "BAGUETTE", "MACARON"}, "VIEMACARONBVLV" +
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
                "FRANCEDYNDSMSW"));

        levels.add(new Level("Armenia", new String[]{"ARARAT", "MATENADARAN", "YEREVAN", "APRICOT", "DUDUK"}, "TYYDUDUKPTFUJP" +
                "EGKHNYYCVFOQVN" +
                "KAMATENADARANY" +
                "GTEBMYEONGNNAE" +
                "AAWNMJMCCPYXOR" +
                "RZPFANXFEUNLNE" +
                "AEFHSAJLMJWKKV" +
                "ROOHHVPYCIPCZA" +
                "APPNTZSRGARNIN" +
                "TTVCOWWDZPDNCC" +
                "AJTDTQCCJOPIIH" +
                "BGUPSKHACHKARL" +
                "ULLQAAPRICOTAJ" +
                "QEPREGOMESHCWZ"));

        return levels;
    }
}