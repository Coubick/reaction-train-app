package com.example.myapplication;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import java.util.Locale;

public class GameActivity extends AppCompatActivity implements PauseDialogFragment.PauseMenuListener {
    private CountDownTimer initialCountDown;
    private long initialTimeLeft;
    private TextView textViewInitialCountDown;
    private AppCompatImageButton pauseButton;
    private GameManage gameManage;
    private static final long START_TIME = 30000;
    private TextView textViewCountDown;
    private long timeLeft = START_TIME;
    private CountDownTimer countDownTimer;
    private boolean isPaused = false;
    private boolean isTimerRunning;

    private TextView averageReactionView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_ground);

        gameManage = new GameManage(this);

        textViewInitialCountDown = findViewById(R.id.initial_countdown);
        textViewCountDown = findViewById(R.id.timer_text_view);
        textViewCountDown.setText("00:30:00");
        averageReactionView = findViewById(R.id.average_reaction_text_view);
        pauseButton = findViewById(R.id.pause_button);

        startInitialTimer();
        setupUI();
    }

    @SuppressLint("WrongViewCast")
    private void setupUI(){
        pauseButton = findViewById(R.id.pause_button);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimerRunning){
                    pauseTimer();
                } else{
                    startTimer();
                }
                openDialog();
            }
        });
    }


    private void showPauseMenu() {
        if (!isPaused) {
            pauseGame();
            PauseDialogFragment dialog = PauseDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "pause_menu");
        }
    }

    private void startGame(){
        isPaused = false;
        gameManage.start();
    }

    private void pauseGame(){
        isPaused = true;
        gameManage.pause();
    }

    private void resumeGame(){
        isPaused = false;
        startInitialTimer();
        gameManage.resume();
    }

    @Override
    public void onResumeGame() {
        resumeGame();
    }

    @Override
    public void onRestartGame() {
        resetTimer();
        startInitialTimer();
        gameManage.start();
    }

    @Override
    public void onExitGame() {
        // 1. Остановите все игровые процессы
        if (gameManage != null) {
            gameManage.pause();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (initialCountDown != null) {
            initialCountDown.cancel();
        }

        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void openDialog() {
        DialogFragment dialogFragment = new PauseDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "example");
    }

    private void startInitialTimer(){
        textViewInitialCountDown.setVisibility(VISIBLE);
        initialTimeLeft = 4000;
        initialCountDown = new CountDownTimer(initialTimeLeft, 1000) {
            @Override
            public void onFinish() {
                textViewInitialCountDown.setVisibility(INVISIBLE);
                startTimer();
                startGame();
            }

            @Override
            public void onTick(long millisUntilFinished) {
                initialTimeLeft = millisUntilFinished;
                updateInitialCountdownText();
            }
        }.start();
    }

    private void updateInitialCountdownText(){
        int seconds = (int) (initialTimeLeft / 1000);
        String time = String.valueOf(seconds);
        textViewInitialCountDown.setText(time);
    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(timeLeft, 1) {
            @Override
            public void onFinish() {
                isTimerRunning = false;
            }

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateCountDownText();
            }
        }.start();

        isTimerRunning = true;
    }

    private void pauseTimer(){
        initialCountDown.cancel();
        countDownTimer.cancel();
        isTimerRunning = false;
    }

    private void resetTimer(){
        timeLeft = START_TIME;
        updateCountDownText();
    }

    private void updateCountDownText(){
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        int milliseconds = (int) (timeLeft % 1000) / 10;
        String timeLeft = String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, milliseconds);
        textViewCountDown.setText(timeLeft);
    }

    public void updateAverageReaction(long time) {
        String avgReactTime = "Average " + time + " ms";
        averageReactionView.setText(avgReactTime);
    }
}