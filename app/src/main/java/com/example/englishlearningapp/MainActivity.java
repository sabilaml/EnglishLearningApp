package com.example.englishlearningapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.RecognizerIntent;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private TextToSpeech textToSpeech;
    private ActivityResultLauncher<Intent> speechLauncher;
    private TextView speechResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.animal_sound);

        // Inisialisasi TextToSpeech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        speechResultText = findViewById(R.id.speechResultText);

        speechLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> speechResult = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (speechResult != null && !speechResult.isEmpty()) {
                            String spokenText = speechResult.get(0);
                            Toast.makeText(this, "You said: " + spokenText, Toast.LENGTH_SHORT).show();

                            // Tampilab teks hasil suara di TextView
                            speechResultText.setText(spokenText);
                        }
                    }
                }
        );

        // gambar diklik
        ImageView imageView = findViewById(R.id.animal_image);
        imageView.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            }
            mediaPlayer.start();
        });

        // Text-to-Speech
        findViewById(R.id.story_button).setOnClickListener(v -> {
            String story = "Once upon a time, in a faraway land, there was a little cat who loved to explore.";
            textToSpeech.speak(story, TextToSpeech.QUEUE_FLUSH, null, null);
        });

        // Speech-to-Text
        findViewById(R.id.speech_button).setOnClickListener(v -> promptSpeechInput());
    }

    // Fungsi memulai input suara
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        speechLauncher.launch(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
}