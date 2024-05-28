
package com.example.globrain;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.flexbox.FlexboxLayout;

public class WordsGameActivity extends AppCompatActivity {

    private GridLayout grid;
    private FlexboxLayout wordsContainer;

    private StringBuilder selectedWord;
    private List<TextView> selectedCells;
    private List<TextView> foundCells;
    private TextView previousCell;
    private List<TextView> previousFoundCells;

    private CountDownTimer timer;
    private TextView timerTextView;
    private long timeLeftInMillis = 50000; //

    private String country;
    private String[] words;
    private String lettersTable;

    private boolean isGameFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words_game);

        Intent intent = getIntent();
        country = intent.getStringExtra("country");
        words = intent.getStringArrayExtra("words");
        lettersTable = intent.getStringExtra("lettersTable");

        timerTextView = findViewById(R.id.timerTextView);

        timer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                if(!isGameFinished){
                    showGameOverDialog();
                }
            }
        };

        timer.start();

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

                        checkGameStatus();

                        break;
                }

                prevX = x;
                prevY = y;
                return true;
            }
        });
    }

    private void createGrid() {
        int gridSize = (int) Math.sqrt(lettersTable.length());

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cellSize = screenWidth / gridSize;

        for (int i = 0; i < lettersTable.length(); i++) {
            TextView textView = new TextView(this);
            textView.setText(String.valueOf(lettersTable.charAt(i)));
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

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showWordInfoDialog(((TextView) v).getText().toString());
                }
            });

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

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void showGameOverDialog() {
        isGameFinished = true;
        Intent intent = new Intent(WordsGameActivity.this, GameOverActivity.class);
        intent.putExtra("country", country);
        intent.putExtra("words", words);
        intent.putExtra("lettersTable", lettersTable);
        startActivity(intent);
        finish();
    }

    private void checkGameStatus() {
        int totalWords = words.length;
        int foundWords = 0;

        for (String word : words) {
            boolean isWordFound = false;
            for (int i = 0; i < wordsContainer.getChildCount(); i++) {
                TextView wordTextView = (TextView) wordsContainer.getChildAt(i);
                if (wordTextView.getText().toString().equalsIgnoreCase(word) &&
                        (wordTextView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) != 0) {
                    isWordFound = true;
                    break;
                }
            }
            if (isWordFound) {
                foundWords++;
            }
        }

        if (foundWords == totalWords) {
            isGameFinished = true;
            timer.cancel();
            Intent intent = new Intent(WordsGameActivity.this, LevelCompletedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showGameOverDialog();
    }

    private void showWordInfoDialog(String word) {

        timer.cancel();

        // Create a dialog instance
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.word_info_dialog);

        // Get references to the dialog views
        ImageView wordImageView = dialog.findViewById(R.id.wordImageView);
        TextView wordNameTextView = dialog.findViewById(R.id.wordNameTextView);
        TextView wordDescriptionTextView = dialog.findViewById(R.id.wordDescriptionTextView);
        Button readMoreButton = dialog.findViewById(R.id.readMoreButton);

        // Set the word image, name, and description
        wordImageView.setImageResource(getImageLink(word));
        wordNameTextView.setText(word);
        wordDescriptionTextView.setText(getWordDescription(word));

        // Set click listener for the read more button
        readMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(getWordReadMoreLink(word));
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

                timer = new CountDownTimer(timeLeftInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeftInMillis = millisUntilFinished;
                        updateTimer();
                    }

                    @Override
                    public void onFinish() {
                        if(!isGameFinished){
                            showGameOverDialog();
                        }
                    }
                };

                timer.start();
            }
        });

        // Show the dialog
        dialog.show();
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

//    ------------------------------ Countries' data ---------------------------------------------

    private String getWordDescription(String word) {
        Map<String, String> wordDescriptions = new HashMap<>();

        // France
        wordDescriptions.put("EIFFEL", "The Eiffel Tower, an iconic symbol of Paris, was completed in 1889 as the centerpiece of the 1889 Exposition Universelle. It's the tallest structure in Paris.");
        wordDescriptions.put("CROISSANT", "A croissant is a French pastry made from puff pastry in a crescent shape. It is a buttery, flaky, viennoiserie pastry inspired by the shape of the Austrian kipferl.");
        wordDescriptions.put("PARIS", "Paris is the capital and most populous city of France. Paris is especially known for its museums and architectural landmarks.");
        wordDescriptions.put("BAGUETTE", "A baguette is a long, thin type of bread of French origin that is commonly made from basic lean dough. It is distinguishable by its length and crisp crust.");
        wordDescriptions.put("LOUVRE", "The Louvre, or the Louvre Museum, is a national art museum in Paris, France. The museum opened on 10 August 1793.");
        wordDescriptions.put("DIOR", "Christian Dior SE, commonly known as Dior (stylized DIOR), is a French multinational luxury fashion house founded in 1946 by French fashion designer Christian Dior.");
        wordDescriptions.put("MACARON", "A macaron or French macaroon is a sweet meringue-based confection. These colorful treats are a testament to the artistry of French patisserie.");
        wordDescriptions.put("CHAMPAGNE", "Champagne is a sparkling wine originated and produced in the Champagne wine region of France. In France, the first sparkling champagne was created accidentally.");
        wordDescriptions.put("COGNAC", "Cognac is a variety of brandy named after the commune of Cognac, France. Revered worldwide, cognac embodies luxury and craftsmanship.");

        // Armenia
        wordDescriptions.put("YEREVAN", "Yerevan is the capital and largest city of Armenia, as well as one of the world's oldest continuously inhabited cities.");
        wordDescriptions.put("MATENADARAN", "The Matenadaran is a museum, repository of manuscripts, and a research institute in Yerevan, Armenia. It is the world's largest repository of Armenian manuscripts.");
        wordDescriptions.put("APRICOT", "The apricot, known scientifically as Prunus armeniaca, is deeply rooted in Armenian heritage.");
        wordDescriptions.put("GATA", "Gata is an Armenian pastry or sweet bread. One popular variety of it is koritz (khoriz), a filling that consists of flour, butter and sugar.");
        wordDescriptions.put("DUDUK", "The duduk or tsiranapogh is a double reed woodwind musical instrument made of apricot wood originating from Armenia. It is commonly played in pairs.");
        wordDescriptions.put("KHACHKAR", "A khachkar or Armenian cross-stone is a carved, memorial stele bearing a cross. Khachkars are characteristic of medieval Christian Armenian art.");
        wordDescriptions.put("MASHTOTS", "Mesrop Mashtots was an Armenian linguist, composer, theologian, statesman, and hymnologist in the Sasanian Empire. He is best known for inventing the Armenian alphabet.");
        wordDescriptions.put("PREGOMESH", "Pregomesh is a captivating Armenian jewelry brand founded by Sirusho. It revives traditional Armenian craftsmanship with a modern twist.");
        wordDescriptions.put("GARNI", "The Garni Temple is the only standing Greco-Roman colonnaded building in Armenia. The site is in the village of Garni, in Armenia's Kotayk Province.");


        // Italy
        wordDescriptions.put("VENICE", "Venice is a city in northeastern Italy and the capital of the Veneto region. With its winding canals, striking architecture, and beautiful bridges, Venice is a popular destination for travel.");
        wordDescriptions.put("COLOSSEUM", "The Colosseum is an elliptical amphitheatre in the centre of the city of Rome, Italy. It is the largest ancient amphitheatre ever built, and is still the largest standing one in the world.");
        wordDescriptions.put("PASTA", "Pasta, a staple of Italian cuisine, is loved for its versatility and comforting taste. Made from durum wheat flour and water, it comes in various shapes and sizes.");
        wordDescriptions.put("PIZZA", "Pizza is a traditional Italian dish typically consisting of a flat base of leavened wheat-based dough topped with tomato, cheese, and other ingredients, baked at a high temperature.");
        wordDescriptions.put("DOLOMITES", "The Dolomites, also known as the Dolomite Mountains, Dolomite Alps or Dolomitic Alps, are a mountain range in northeastern Italy.");
        wordDescriptions.put("ROME", "Rome is the capital and largest city of Italy. It is the country's most populated comune.");
        wordDescriptions.put("GUCCI", "Guccio Gucci S.p.A., is an Italian luxury fashion house based in Florence, Italy. Its product lines include handbags, ready-to-wear, footwear, accessories, and home decoration.");
        wordDescriptions.put("BRUSCHETTA", "Bruschetta is an Italian antipasto consisting of grilled bread often topped with olive oil and salt. Most commonly it is served with toppings of tomato, vegetables, beans, cured meat, and/or cheese.");
        wordDescriptions.put("MILAN", "Milan is a city in northern Italy, regional capital of Lombardy, and the second-most-populous city proper in Italy after Rome. It is a leading alpha global city.");


        // Russia
        wordDescriptions.put("MOSCOW", "Moscow is the capital and largest city of Russia. It is among the world's largest cities, being the most populous city in its entirety in Europe, the largest urban and metropolitan area in Europe.");
        wordDescriptions.put("PUSHKIN", "Alexander Sergeyevich Pushkin was a Russian poet, playwright, and novelist of the Romantic era. He is considered by many to be the greatest Russian poet, as well as the founder of modern Russian literature.");
        wordDescriptions.put("MATRYOSHKA", "Matryoshka dolls, also known as nesting dolls, Russian tea dolls, or Russian dolls, are a set of wooden dolls of decreasing size placed one inside another.");
        wordDescriptions.put("KREMLIN", "The Moscow Kremlin, or simply the Kremlin, a historic fortified complex in the heart of Moscow, serves as the political and administrative center of Russia. It is a symbol of Russian power and heritage.");
        wordDescriptions.put("PELMENI", "Pelmeni are dumplings of Russian cuisine that consist of a filling wrapped in thin, unleavened dough. Pelmeni have been described as 'the heart of Russian cuisine'.");
        wordDescriptions.put("YANDEX", "Yandex LLC is a Russian multinational technology company providing Internet-related products and services, information services, e-commerce, maps and navigation, mobile applications, and much more. As of 2017, it was the largest technology company in Russia.");
        wordDescriptions.put("HERMITAGE", "The Hermitage Museum in Russia, located in St. Petersburg, is one of the largest and most prestigious museums in the world. It was founded by Catherine the Great in 1764.");
        wordDescriptions.put("BAIKAL", "Lake Baikal is a large rift lake in Russia. It is the oldest and deepest lake in the world. Lake Baikal contains 20% of the world's total unfrozen freshwater reserve.");
        wordDescriptions.put("SAMOVAR", "A samovar ('self-brewer') is a metal container traditionally used to heat and boil water. Although originating in Russia, the samovar is well known outside of Russia.");


        return wordDescriptions.get(word);
    }

    private String getWordReadMoreLink(String word) {
        Map<String, String> wordReadMoreLinks = new HashMap<>();

        wordReadMoreLinks.put("EIFFEL", "https://en.wikipedia.org/wiki/Eiffel_Tower");
        wordReadMoreLinks.put("CROISSANT", "https://en.wikipedia.org/wiki/Croissant");
        wordReadMoreLinks.put("PARIS", "https://en.wikipedia.org/wiki/Paris");
        wordReadMoreLinks.put("BAGUETTE", "https://en.wikipedia.org/wiki/Baguette");
        wordReadMoreLinks.put("LOUVRE", "https://en.wikipedia.org/wiki/Louvre");
        wordReadMoreLinks.put("DIOR", "https://en.wikipedia.org/wiki/Dior");
        wordReadMoreLinks.put("MACARON", "https://en.wikipedia.org/wiki/Macaron");
        wordReadMoreLinks.put("CHAMPAGNE", "https://en.wikipedia.org/wiki/Champagne");
        wordReadMoreLinks.put("COGNAC", "https://en.wikipedia.org/wiki/Cognac");


        wordReadMoreLinks.put("YEREVAN", "https://en.wikipedia.org/wiki/Yerevan");
        wordReadMoreLinks.put("MATENADARAN", "https://en.wikipedia.org/wiki/Matenadaran");
        wordReadMoreLinks.put("APRICOT", "https://en.wikipedia.org/wiki/Apricot");
        wordReadMoreLinks.put("GATA", "https://en.wikipedia.org/wiki/Gata_(food)");
        wordReadMoreLinks.put("DUDUK", "https://en.wikipedia.org/wiki/Duduk");
        wordReadMoreLinks.put("KHACHKAR", "https://en.wikipedia.org/wiki/Khachkar");
        wordReadMoreLinks.put("MASHTOTS", "https://en.wikipedia.org/wiki/Mesrop_Mashtots");
        wordReadMoreLinks.put("PREGOMESH", "https://pregomesh.com/en-am/pages/about-us");
        wordReadMoreLinks.put("GARNI", "https://en.wikipedia.org/wiki/Garni_Temple");


        wordReadMoreLinks.put("VENICE", "https://en.wikipedia.org/wiki/Venice");
        wordReadMoreLinks.put("COLOSSEUM", "https://en.wikipedia.org/wiki/Colosseum");
        wordReadMoreLinks.put("PASTA", "https://en.wikipedia.org/wiki/Pasta");
        wordReadMoreLinks.put("PIZZA", "https://en.wikipedia.org/wiki/Pizza");
        wordReadMoreLinks.put("DOLOMITES", "https://en.wikipedia.org/wiki/Dolomites");
        wordReadMoreLinks.put("ROME", "https://en.wikipedia.org/wiki/Rome");
        wordReadMoreLinks.put("GUCCI", "https://en.wikipedia.org/wiki/Gucci");
        wordReadMoreLinks.put("BRUSCHETTA", "https://en.wikipedia.org/wiki/Bruschetta");
        wordReadMoreLinks.put("MILAN", "https://en.wikipedia.org/wiki/Milan");


        wordReadMoreLinks.put("MOSCOW", "https://en.wikipedia.org/wiki/Moscow");
        wordReadMoreLinks.put("PUSHKIN", "https://en.wikipedia.org/wiki/Alexander_Pushkin");
        wordReadMoreLinks.put("MATRYOSHKA", "https://en.wikipedia.org/wiki/Matryoshka_doll");
        wordReadMoreLinks.put("KREMLIN", "https://en.wikipedia.org/wiki/Kremlin");
        wordReadMoreLinks.put("PELMENI", "https://en.wikipedia.org/wiki/Pelmeni");
        wordReadMoreLinks.put("YANDEX", "https://en.wikipedia.org/wiki/Yandex");
        wordReadMoreLinks.put("HERMITAGE", "https://en.wikipedia.org/wiki/Hermitage_Museum");
        wordReadMoreLinks.put("BAIKAL", "https://en.wikipedia.org/wiki/Lake_Baikal");
        wordReadMoreLinks.put("SAMOVAR", "https://en.wikipedia.org/wiki/Samovar");


        return wordReadMoreLinks.get(word);
    }

    private int getImageLink(String word) {
        Map<String, Integer> wordImageResources = new HashMap<>();

        wordImageResources.put("EIFFEL", R.drawable.eiffel);
        wordImageResources.put("CROISSANT", R.drawable.croissant);
        wordImageResources.put("PARIS", R.drawable.paris);
        wordImageResources.put("BAGUETTE", R.drawable.baguette);
        wordImageResources.put("LOUVRE", R.drawable.louvre);
        wordImageResources.put("DIOR", R.drawable.dior);
        wordImageResources.put("MACARON", R.drawable.macaron);
        wordImageResources.put("CHAMPAGNE", R.drawable.champagne);
        wordImageResources.put("COGNAC", R.drawable.cognac);


        wordImageResources.put("YEREVAN", R.drawable.yerevan);
        wordImageResources.put("MATENADARAN", R.drawable.matenadaran);
        wordImageResources.put("APRICOT", R.drawable.apricot);
        wordImageResources.put("GATA", R.drawable.gata);
        wordImageResources.put("DUDUK", R.drawable.duduk);
        wordImageResources.put("KHACHKAR", R.drawable.khachkar);
        wordImageResources.put("MASHTOTS", R.drawable.mashtots);
        wordImageResources.put("PREGOMESH", R.drawable.pregomesh);
        wordImageResources.put("GARNI", R.drawable.garni);


        wordImageResources.put("VENICE", R.drawable.venice);
        wordImageResources.put("COLOSSEUM", R.drawable.colosseum);
        wordImageResources.put("PASTA", R.drawable.pasta);
        wordImageResources.put("PIZZA", R.drawable.pizza);
        wordImageResources.put("DOLOMITES", R.drawable.colosseum);
        wordImageResources.put("ROME", R.drawable.rome);
        wordImageResources.put("GUCCI", R.drawable.gucci);
        wordImageResources.put("BRUSCHETTA", R.drawable.bruschetta);
        wordImageResources.put("MILAN", R.drawable.milan);


        wordImageResources.put("MOSCOW", R.drawable.moscow);
        wordImageResources.put("PUSHKIN", R.drawable.pushkin);
        wordImageResources.put("MATRYOSHKA", R.drawable.matryoshka);
        wordImageResources.put("KREMLIN", R.drawable.kremlin);
        wordImageResources.put("PELMENI", R.drawable.pelmeni);
        wordImageResources.put("YANDEX", R.drawable.yandex);
        wordImageResources.put("HERMITAGE", R.drawable.hermitage);
        wordImageResources.put("BAIKAL", R.drawable.baikal);
        wordImageResources.put("SAMOVAR", R.drawable.samovar);

        return wordImageResources.get(word);
    }

}