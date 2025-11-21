package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initializeGame();
    }

    private void initializeGame() {
        // инициализация игровой логики
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pause_menu, menu);
        return true;
    }
}