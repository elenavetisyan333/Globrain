package com.example.globrain;

import android.content.Intent;
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
    private ImageButton italyButton;
    private ImageButton russiaButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        levels = getLevels();
        currentUnlockedLevelIndex = getUnlockedLevelIndex();
        getUnlockedLevelIndexFromFirestore();

        franceButton = view.findViewById(R.id.FranceIcon);
        armeniaButton = view.findViewById(R.id.ArmeniaIcon);
        italyButton = view.findViewById(R.id.ItalyIcon);
        russiaButton = view.findViewById(R.id.RussiaIcon);

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

        italyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUnlockedLevelIndex >= 2) {
                    startGameActivity(levels.get(2));
                } else {
                    Toast.makeText(getActivity(), "Level is locked. Complete previous levels to unlock.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        russiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUnlockedLevelIndex >= 3) {
                    startGameActivity(levels.get(3));
                } else {
                    Toast.makeText(getActivity(), "Level is locked. Complete previous levels to unlock.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        lockLevels();

        return view;
    }

    private void startGameActivity(Level level) {
        Intent intent = new Intent(getActivity(), WordsGameActivity.class);
        intent.putExtra("country", level.getCountry());
        intent.putExtra("words", level.getWords());
        intent.putExtra("lettersTable", level.getLettersTable());
        startActivity(intent);
    }

    private void lockLevels() {
        // Lock all levels except the highest unlocked level
        for (int i = 0; i < levels.size(); i++) {
            ImageButton levelButton = getLevelButton(i);
            if (i > highestUnlockedLevelIndex) {
                levelButton.setEnabled(false);
                levelButton.setAlpha(0.6f);
            } else {
                levelButton.setEnabled(true);
                levelButton.setAlpha(1f);
            }
        }
    }

    private ImageButton getLevelButton(int levelIndex) {
        switch (levelIndex) {
            case 0:
                return franceButton;
            case 1:
                return armeniaButton;
            case 2:
                return italyButton;
            case 3:
                return russiaButton;
            default:
                return null;
        }
    }

    private int getUnlockedLevelIndex() {
        // Get the unlocked level index from Firestore or use a default value
        return highestUnlockedLevelIndex;
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

    // ------------------------------------- LEVELS -------------------------------------------------

    protected static class Level {
        private String country;
        private String[] words;
        private String lettersTable;
        private Map<String, String> wordDescriptions;
        private Map<String, String> readMoreLinks;

        public Level(String country, String[] words, String lettersTable, String[] wordsInfo, String[] wordsImageLinks, String[] readMoreLinks) {
            this.country = country;
            this.words = words;
            this.lettersTable = lettersTable;
            this.wordDescriptions = createWordDescriptionsMap(words, wordsInfo);
            this.readMoreLinks = createReadMoreLinksMap(words, readMoreLinks);
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


        public String getWordDescriptions() {
            return String.valueOf(wordDescriptions);
        }


        public String getReadMoreLinks() {
            return String.valueOf(readMoreLinks);
        }

        private Map<String, String> createWordDescriptionsMap(String[] words, String[] wordDescriptions) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < words.length; i++) {
                map.put(words[i], wordDescriptions[i]);
            }
            return map;
        }

        private Map<String, String> createReadMoreLinksMap(String[] words, String[] readMoreLinks) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < words.length; i++) {
                map.put(words[i], readMoreLinks[i]);
            }
            return map;
        }
    }


    protected static List<Level> getLevels() {
        List<Level> levels = new ArrayList<>();
        levels.add(new Level("France",
                new String[]{"EIFFEL", "CROISSANT", "PARIS", "BAGUETTE", "LOUVRE", "DIOR", "MACARON", "CHAMPAGNE", "COGNAC"},
                "VIEMACARONBVLV" +
                        "UECROISSANTCQS" +
                        "CCHTONVGCJFADN" +
                        "OMVIVGXRAAVIBA" +
                        "GLZYBAGUETTECU" +
                        "NEIFFELHIUEERC" +
                        "AVSKWDKDSFIROH" +
                        "CUBXDNOLOUVREA" +
                        "MNGTIJBPNHAATM" +
                        "REYZOBDLRQPCAP" +
                        "TPQTRGMUDBAKAA" +
                        "CBAMUHWBRYRPDG" +
                        "BGRBLMXEOAINKN" +
                        "LRINCEDYNDSMSE",

                new String[]{
                        "The Eiffel Tower, an iconic symbol of Paris, was completed in 1889 as the centerpiece of the 1889 Exposition Universelle. It's the tallest structure in Paris.",
                        "A croissant is a French pastry made from puff pastry in a crescent shape. It is a buttery, flaky, viennoiserie pastry inspired by the shape of the Austrian kipferl.",
                        "Paris is the capital and most populous city of France. Paris is especially known for its museums and architectural landmarks.",
                        "A baguette is a long, thin type of bread of French origin that is commonly made from basic lean dough. It is distinguishable by its length and crisp crust.",
                        "The Louvre,or the Louvre Museum, is a national art museum in Paris, France. The museum opened on 10 August 1793.",
                        "Christian Dior SE, commonly known as Dior (stylized DIOR), is a French multinational luxury fashion house founded in 1946 by French fashion designer Christian Dior.",
                        "A macaron or French macaroon is a sweet meringue-based confection. These colorful treats are a testament to the artistry of French patisserie.",
                        "Champagne is a sparkling wine originated and produced in the Champagne wine region of France. In France, the first sparkling champagne was created accidentally.",
                        "Cognac is a variety of brandy named after the commune of Cognac, France. Revered worldwide, cognac embodies luxury and craftsmanship."
                },

                new String[]{
                        "@drawable/eiffel",
                        "@drawable/croissant",
                        "@drawable/paris",
                        "@drawable/baguette",
                        "@drawable/louvre",
                        "@drawable/dior",
                        "@drawable/macaron",
                        "@drawable/champagne",
                        "@drawable/cognac"
                },

                new String[]{
                        "https://en.wikipedia.org/wiki/Eiffel_Tower",
                        "https://en.wikipedia.org/wiki/Croissant",
                        "https://en.wikipedia.org/wiki/Paris",
                        "https://en.wikipedia.org/wiki/Baguette",
                        "https://en.wikipedia.org/wiki/Louvre",
                        "https://en.wikipedia.org/wiki/Dior",
                        "https://en.wikipedia.org/wiki/Macaron",
                        "https://en.wikipedia.org/wiki/Champagne",
                        "https://en.wikipedia.org/wiki/Cognac"
                })
        );

        levels.add(new Level("Armenia",
                new String[]{"YEREVAN", "MATENADARAN", "APRICOT", "GATA", "DUDUK", "KHACHKAR", "MASHTOTS", "PREGOMESH", "GARNI"},
                "TYYDUDUKPTFUJP" +
                        "EGKHNYYCVFOQVN" +
                        "KAMATENADARANY" +
                        "GTEBMYEONGNNAE" +
                        "AAWNMJMCCPYXOR" +
                        "RZPFANXFEUNLNE" +
                        "AEFHSAJLMJWKKV" +
                        "POOHHVPYCIPCZA" +
                        "RNPNTZSRGARNIN" +
                        "ITVCOWWDZPDNCC" +
                        "AJTDTQCCJOPIIH" +
                        "BGUPSKHACHKARL" +
                        "ULLQAAPRICOTAJ" +
                        "QEPREGOMESHCWZ",

                new String[]{
                        "Yerevan is the capital and largest city of Armenia, as well as one of the world's oldest continuously inhabited cities.",
                        "The Matenadaran is a museum, repository of manuscripts, and a research institute in Yerevan, Armenia. It is the world's largest repository of Armenian manuscripts.",
                        "The apricot, known scientifically as Prunus armeniaca, is deeply rooted in Armenian heritage.",
                        "Gata is an Armenian pastry or sweet bread. One popular variety of it is koritz (khoriz), a filling that consists of flour, butter and sugar.",
                        "The duduk or tsiranapogh is a double reed woodwind musical instrument made of apricot wood originating from Armenia. It is commonly played in pairs",
                        "A khachkar or Armenian cross-stone is a carved, memorial stele bearing a cross. Khachkars are characteristic of medieval Christian Armenian art.",
                        "Mesrop Mashtots was an Armenian linguist, composer, theologian, statesman, and hymnologist in the Sasanian Empire. He is best known for inventing the Armenian alphabet.",
                        "Pregomesh is a captivating Armenian jewelry brand founded by Sirusho. It revives traditional Armenian craftsmanship with a modern twist.",
                        "The Garni Temple is the only standing Greco-Roman colonnaded building in Armenia. The site is in the village of Garni, in Armenia's Kotayk Province."
                },

                new String[]{
                        "@drawable/yerevan",
                        "@drawable/matenadaran",
                        "@drawable/apricot",
                        "@drawable/gata",
                        "@drawable/duduk",
                        "@drawable/khachkar",
                        "@drawable/mashtots",
                        "@drawable/pregomesh",
                        "@drawable/garni"
                },

                new String[]{
                        "https://en.wikipedia.org/wiki/Yerevan",
                        "https://en.wikipedia.org/wiki/Matenadaran",
                        "https://en.wikipedia.org/wiki/Apricot",
                        "https://en.wikipedia.org/wiki/Gata_(food)",
                        "https://en.wikipedia.org/wiki/Duduk",
                        "https://en.wikipedia.org/wiki/Khachkar",
                        "https://en.wikipedia.org/wiki/Mesrop_Mashtots",
                        "https://pregomesh.com/en-am/pages/about-us",
                        "https://en.wikipedia.org/wiki/Garni_Temple"
                })
        );


        levels.add(new Level("Italy",
                new String[]{"VENICE", "COLOSSEUM", "PASTA", "PIZZA", "DOLOMITES", "ROME", "GUCCI", "BRUSCHETTA", "MILAN"},
                "PYMDIANFAFBJGD" +
                        "WNABBCVXPBFAVO" +
                        "WUMRSSEMWEPKXL" +
                        "GUDUIQNZPQAUDO" +
                        "UYBSITIMVASZPM" +
                        "CQBCBYCVGVTWUI" +
                        "CVKHAZEDJYATHT" +
                        "IOGEBQAMEHPUAE" +
                        "BCVTAVPIZZAUJS" +
                        "SSYTQHXXYFUQDD" +
                        "DDZAMILANBCBFP" +
                        "JIIZTPRTXZVHVO" +
                        "WTAFAROMEFACVD" +
                        "URMDCOLOSSEUME",

                new String[]{
                        "Venice is a city in northeastern Italy and the capital of the Veneto region. With its winding canals, striking architecture, and beautiful bridges, Venice is a popular destination for travel.",
                        "The Colosseum is an elliptical amphitheatre in the centre of the city of Rome, Italy. It is the largest ancient amphitheatre ever built, and is still the largest standing one in the world.",
                        "Pasta, a staple of Italian cuisine, is loved for its versatility and comforting taste. Made from durum wheat flour and water, it comes in various shapes and sizes.",
                        "Pizza is a traditional Italian dish typically consisting of a flat base of leavened wheat-based dough topped with tomato, cheese, and other ingredients, baked at a high temperature.",
                        "The Dolomites, also known as the Dolomite Mountains, Dolomite Alps or Dolomitic Alps, are a mountain range in northeastern Italy.",
                        "Rome is the capital and largest city of Italy. It is the country's most populated comune. ",
                        "Guccio Gucci S.p.A., is an Italian luxury fashion house based in Florence, Italy. Its product lines include handbags, ready-to-wear, footwear, accessories, and home decoration.",
                        "Bruschetta is an Italian antipasto consisting of grilled bread often topped with olive oil and salt. Most commonly it is served with toppings of tomato, vegetables, beans, cured meat, and/or cheese.",
                        "Milan is a city in northern Italy, regional capital of Lombardy, and the second-most-populous city proper in Italy after Rome. It is a leading alpha global city."
                },

                new String[]{
                        "@drawable/venice",
                        "@drawable/colosseum",
                        "@drawable/pasta",
                        "@drawable/pizza",
                        "@drawable/dolomites",
                        "@drawable/rome",
                        "@drawable/gucci",
                        "@drawable/bruschetta",
                        "@drawable/milan"
                },

                new String[]{
                        "https://en.wikipedia.org/wiki/Venice",
                        "https://en.wikipedia.org/wiki/Colosseum",
                        "https://en.wikipedia.org/wiki/Pasta",
                        "https://en.wikipedia.org/wiki/Pizza",
                        "https://en.wikipedia.org/wiki/Dolomites",
                        "https://en.wikipedia.org/wiki/Rome",
                        "https://en.wikipedia.org/wiki/Gucci",
                        "https://en.wikipedia.org/wiki/Bruschetta",
                        "https://en.wikipedia.org/wiki/Milan"
                })
        );

        levels.add(new Level("Russia",
                new String[]{"MOSCOW", "PUSHKIN", "MATRYOSHKA", "KREMLIN", "PELMENI", "YANDEX", "HERMITAGE", "BAIKAL", "SAMOVAR"},
                "BAIKALQZRLMZAH" +
                        "HAULYABUPRIAMK" +
                        "XXMOSCOWILNABK" +
                        "MATRYOSHKAAZSS" +
                        "MEMCZKASRQYYAI" +
                        "ZYANDEXKGZAMMA" +
                        "CNNPKHMARKTMOK" +
                        "PCPELMENIEBGVR" +
                        "UBKXQVOJJZCEAE" +
                        "SWSVFOJDRAALRM" +
                        "HLDHERMITAGEKL" +
                        "KJKAFRDUIHLIYI" +
                        "IADIQCSVPXUGGN" +
                        "NINGPSMPXKIULF",

                new String[]{
                        "Moscow is the capital and largest city of Russia. It is among the world's largest cities, being the most populous city in its entirety in Europe, the largest urban and metropolitan area in Europe.",
                        "Alexander Sergeyevich Pushkin was a Russian poet, playwright, and novelist of the Romantic era. He is considered by many to be the greatest Russian poet, as well as the founder of modern Russian literature.",
                        "Matryoshka dolls, also known as nesting dolls, Russian tea dolls, or Russian dolls, are a set of wooden dolls of decreasing size placed one inside another.",
                        "The Moscow Kremlin, or simply the Kremlin, a historic fortified complex in the heart of Moscow, serves as the political and administrative center of Russia. It is a symbol of Russian power and heritage.",
                        "Pelmeni are dumplings of Russian cuisine that consist of a filling wrapped in thin, unleavened dough. Pelmeni have been described as \"the heart of Russian cuisine\".",
                        "Yandex LLC is a Russian multinational technology company providing Internet-related products and services, information services, e-commerce, maps and navigation, mobile applications and much more. As of 2017, It was the largest technology company in Russia.",
                        "The Hermitage Museum in Russia, located in St. Petersburg, is one of the largest and most prestigious museums in the world. It was founded by Catherine the Great in 1764.",
                        "Lake Baikal is a large rift lake in Russia. It is the oldest and deepest lake in the world. Lake Baikal contains 20% of the world's total unfrozen freshwater reserve.",
                        "A samovar (\"self-brewer\") is a metal container traditionally used to heat and boil water. Although originating in Russia, the samovar is well known outside of Russia. "
                },

                new String[]{
                        "@drawable/moscow",
                        "@drawable/pushkin",
                        "@drawable/matryoshka",
                        "@drawable/kremlin",
                        "@drawable/pelmeni",
                        "@drawable/yandex",
                        "@drawable/hermitage",
                        "@drawable/baikal",
                        "@drawable/samovar"
                },

                new String[]{
                        "https://en.wikipedia.org/wiki/Moscow",
                        "https://en.wikipedia.org/wiki/Alexander_Pushkin",
                        "https://en.wikipedia.org/wiki/Matryoshka_doll",
                        "https://en.wikipedia.org/wiki/Kremlin",
                        "https://en.wikipedia.org/wiki/Pelmeni",
                        "https://en.wikipedia.org/wiki/Yandex",
                        "https://en.wikipedia.org/wiki/Hermitage_Museum",
                        "https://en.wikipedia.org/wiki/Lake_Baikal",
                        "https://en.wikipedia.org/wiki/Samovar"
                })
        );


        return levels;

    }
}