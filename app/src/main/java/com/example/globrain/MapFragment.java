package com.example.globrain;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.globrain.R;

public class MapFragment extends Fragment {

//    FRANCE
    private ImageButton franceButton;
    private String[] franceWords = {"EIFFEL", "CROISSANT", "BAGUETTE", "MACARON"};
    private String franceLettersTable = "VIEMACARONBVLV" +
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

//    ARMENIA
    private ImageButton armeniaButton;
    private String[] armeniaWords = {"ARARAT", "MATENADARAN", "YEREVAN", "APRICOT", "DUDUK", "GATA", "KHACHKAR", "MASHTOTS", "PREGOMESH", "GARNI"};
    private String armeniaLettersTable = "TYYDUDUKPTFUJP" +
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
            "QEPREGOMESHCWZ";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);


//        FRANCE
        franceButton = view.findViewById(R.id.FranceIcon);
        franceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity("France", franceWords, franceLettersTable);
            }
        });

//        ARMENIA
        armeniaButton = view.findViewById(R.id.ArmeniaIcon);
        armeniaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity("Armenia", armeniaWords, armeniaLettersTable);
            }
        });

//        Italy

        return view;
    }

    private void startGameActivity(String country, String[] words, String lettersTable){
        Intent intent = new Intent(getActivity(), WordsGameActivity.class);
        intent.putExtra("country", country);
        intent.putExtra("words", words);
        intent.putExtra("lettersTable", lettersTable);
        startActivity(intent);
    }

}