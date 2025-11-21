package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setupUI();
    }

    private void setupUI() {
        Button startGameButton = findViewById(R.id.start_game_button);
        Button statisticsButton = findViewById(R.id.statistics_button);

        startGameButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });

        statisticsButton.setOnClickListener(v -> {
            // переход к статистике
        });
    }
}