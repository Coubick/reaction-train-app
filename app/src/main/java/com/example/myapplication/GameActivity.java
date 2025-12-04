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
    private TextView textViewPrepareCountDown;
    private AppCompatImageButton pauseButton;
    private GameManage gameManage;
    private static long startTime = 120000;
    private TextView textViewCountDown;
    private long timeLeft = startTime;
    private CountDownTimer countDownTimer;
    private boolean isPaused = false;
    private boolean isTimerRunning;

    private boolean isPreparing = true;

    private static final long PREPARE_TIME = 4000;

    private long prepareTimeLeft = PREPARE_TIME;

    private long pauseBeginTime;

    private TextView averageReactionView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_ground);

        gameManage = new GameManage(this);

        textViewPrepareCountDown = findViewById(R.id.initial_countdown);
        textViewCountDown = findViewById(R.id.timer_text_view);
        textViewCountDown.setText("00:30:00");
        averageReactionView = findViewById(R.id.average_reaction_text_view);
        pauseButton = findViewById(R.id.pause_button);

        setupUI();
        startGame();
    }

    @SuppressLint("WrongViewCast")
    private void setupUI(){
        pauseButton = findViewById(R.id.pause_button);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
                openDialog();
                pauseBeginTime = System.currentTimeMillis();
                gameManage.pause();
            }
        });
    }


    private void startGame(){
        isPreparing = true;
        prepareTimeLeft = PREPARE_TIME; // 4000
        textViewPrepareCountDown.setVisibility(VISIBLE);

        countDownTimer = new CountDownTimer(startTime + PREPARE_TIME, 1) {

            @Override
            public void onTick(long millisecondsUntilFinish) {
                if (isPreparing){
                    prepareTimeLeft = millisecondsUntilFinish - startTime;
                    if (prepareTimeLeft <= 0){
                        isPreparing = false;
                        textViewPrepareCountDown.setVisibility(INVISIBLE);
                        textViewCountDown.setVisibility(VISIBLE);
                        gameManage.start();
                    } else {
                        updateInitialCountDownText();
                    }
                } else {
                    timeLeft = millisecondsUntilFinish;
                    updateCountDownText();
                }
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
            }

        }.start();
        isTimerRunning = true;
    }

    private void updateInitialCountDownText() {
        int seconds = (int) prepareTimeLeft / 1000;
        textViewPrepareCountDown.setText(String.valueOf(seconds));
    }

    private void resumeGame(){
        isPaused = false;
        startTime = timeLeft;
        startGame();
        gameManage.resume();
    }

    @Override
    public void onResumeGame() {
        resumeGame();
    }

    @Override
    public void onRestartGame() {
        // TODO
        resetTimer();
        gameManage.start();
    }

    @Override
    public void onExitGame() {
        // остановка всех игровых процессов
        if (gameManage != null) {
            gameManage.pause();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void openDialog() {
        DialogFragment dialogFragment = new PauseDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "example");
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        isTimerRunning = false;
    }

    private void resetTimer(){
        timeLeft = startTime;
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