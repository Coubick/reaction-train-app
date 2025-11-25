package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

public class GameActivity extends AppCompatActivity implements PauseDialogFragment.PauseMenuListener {
    private AppCompatImageButton pauseButton;
    private GameManage gameManager;
    private boolean isPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_ground);

        setupUI();
    }
    private void setupUI(){
        pauseButton = findViewById(R.id.pause_button);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void showPauseMenu() {
        if (!isPaused) {
//            pauseGame();
            PauseDialogFragment dialog = PauseDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "pause_menu");
        }
    }

//    private void pauseGame(){
//        isPaused = true;
//        gameManager.pauseGame();
//    }
//
//    private void resumeGame(){
//        isPaused = false;
//        gameManager.resumeGame();
//    }

    private void openDialog() {
        DialogFragment dialogFragment = new PauseDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "example");
    }

    @Override
    public void onResumeGame() {
//        resumeGame();
    }

    @Override
    public void onExitToMenu() {
        onExitToMenu();
    }
}