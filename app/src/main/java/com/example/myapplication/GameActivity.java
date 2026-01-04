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

public class GameActivity extends AppCompatActivity implements PauseDialogFragment.PauseMenuListener, FinishDialogFragment.FinishMenuListener{
    private TextView textViewPrepareCountDown;
    private AppCompatImageButton pauseButton;
    private GameManage gameManage;
    private static long startTime = 60000;
    private TextView textViewCountDown;
    private TextView textViewClickedDotsAmount;
    private long timeLeft = startTime;
    private CountDownTimer mainCountDownTimer;
    private boolean isTimerRunning;
    private boolean isPreparing = true;
    private static final long PREPARE_TIME = 4000;

    private long prepareTimeLeft = PREPARE_TIME;
    private TextView averageReactionView;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_ground);

        gameManage = new GameManage(this);

        textViewPrepareCountDown = findViewById(R.id.initial_countdown);
        textViewCountDown = findViewById(R.id.timer_text_view);
        textViewClickedDotsAmount = findViewById(R.id.dots_amount_text_view);
        textViewCountDown.setText("00:60:00");
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
                openPauseDialog();
                gameManage.pause();
            }
        });
    }

    private void startGame(){
        gameManage = new GameManage(this);
        isPreparing = true;
        prepareTimeLeft = PREPARE_TIME; // 4000
        textViewPrepareCountDown.setVisibility(VISIBLE);

        mainCountDownTimer = new CountDownTimer(startTime + PREPARE_TIME, 1) {

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
                gameManage.pause();
                openFinishDialog();
            }

        }.start();
        isTimerRunning = true;
    }

    private void updateInitialCountDownText() {
        int seconds = (int) prepareTimeLeft / 1000;
        textViewPrepareCountDown.setText(String.valueOf(seconds));
    }

    private void resumeGame(){
        isPreparing = true;
        startTime = timeLeft;
        prepareTimeLeft = PREPARE_TIME;
        textViewPrepareCountDown.setVisibility(VISIBLE);
        mainCountDownTimer = new CountDownTimer(PREPARE_TIME + timeLeft, 1) {

            @Override
            public void onTick(long millisecondsUntilFinish) {
                if (isPreparing) {
                    prepareTimeLeft = millisecondsUntilFinish - startTime;
                    if (prepareTimeLeft <= 0) {
                        isPreparing = false;
                        textViewPrepareCountDown.setVisibility(INVISIBLE);
                        textViewCountDown.setVisibility(VISIBLE);
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
                gameManage.pause();
                openFinishDialog();
            }

        }.start();
        isTimerRunning = true;
        gameManage.resume();
    }

    @Override
    public void onResumeGame() {
        resumeGame();
    }

    @Override
    public void onRestartGame() {
        gameManage.removeDot();
        updateAverageReaction(0);
        updateClickedDotsAmountText(0);
        resetTimer();
        startGame();
    }

    @Override
    public void onExitGame() {
        startTime = 60000;
        if (gameManage != null) {
            gameManage.pause();
        }

        if (mainCountDownTimer != null) {
            mainCountDownTimer.cancel();
        }

        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void openPauseDialog() {
        DialogFragment dialogFragment = new PauseDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "example");
    }

    private void openFinishDialog() {
        DialogFragment dialogFragment = new FinishDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "example");
    }

    private void pauseTimer() {
        if (mainCountDownTimer != null) {
            mainCountDownTimer.cancel();
            mainCountDownTimer = null;
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

    public void updateClickedDotsAmountText(int c){
        textViewClickedDotsAmount.setText(String.format("dots clicked: %s", c));
    }

    public void updateAverageReaction(long time) {
        String avgReactTime = "Average " + time + " ms";
        averageReactionView.setText(avgReactTime);
    }
}