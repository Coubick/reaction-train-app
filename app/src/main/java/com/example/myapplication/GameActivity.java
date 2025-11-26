package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;

import java.util.Locale;

public class GameActivity extends AppCompatActivity implements PauseDialogFragment.PauseMenuListener {
    private AppCompatImageButton pauseButton;
    private GameManage gameManager;
    private static final long START_TIME = 30000;
    private TextView textViewCountDown;
    private long timeLeft = START_TIME;
    private CountDownTimer countDownTimer;
    private boolean isPaused = false;
    private boolean isTimerRunning;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_ground);

        textViewCountDown = findViewById(R.id.timer_text_view);

        pauseButton = findViewById(R.id.pause_button);

        startTimer();
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

    //TODO
    /*
    1) обратный отсчет
    1.1) время с обратным отсчетом, меню паузы - в этом классе
    2) при нажатии на паузу отсчёт останавливается и продолжается
    2.1) кнопки продолжить/выйти - в меню паузы!
    2.2) надо будет отдельно прописать логику остановки спавна точек

    3) exit
     */


    private void showPauseMenu() {
        if (!isPaused) {
            pauseGame();
            PauseDialogFragment dialog = PauseDialogFragment.newInstance();
            dialog.show(getSupportFragmentManager(), "pause_menu");
        }
    }

    private void pauseGame(){
        isPaused = true;
    }

    private void resumeGame(){
        isPaused = false;
    }

    private void openDialog() {
        DialogFragment dialogFragment = new PauseDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "example");
    }

    @Override
    public void onResumeGame() {
//        resumeGame();
        // TODO
        finish();
    }

    @Override
    public void onRestartGame() {
        // TODO
        finish();
    }

    private void startTimer(){
        countDownTimer = new CountDownTimer(timeLeft, 10) {
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
}