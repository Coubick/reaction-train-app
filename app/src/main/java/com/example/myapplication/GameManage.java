package com.example.myapplication;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameManage{
    private int totalClicks = 0;
    private long totalReactionTime = 0;
    ConstraintLayout gameField;
    private final Random random = new Random();
    private final GameActivity activity;
    private ImageButton dot;
    private boolean isRunning = false;
    private long lastDotTime;
    private long pauseBeginTime;
    private int clickedDotsAmount = 0;
    private long fullPauseTime = 0;
    private final long PREPARE_TIME = 4000;
    private boolean isDotActive = false;
    private boolean isPreparationPeriod = false;
    private final Handler spawnHandler = new Handler(Looper.getMainLooper());

    private List<Long> reactionTimes = new ArrayList<>();

    private void spawnNextDot() {
        if (!isRunning || isDotActive) return;

        long delay = 300 + (long)(Math.random() * 1200);
        spawnHandler.postDelayed(() -> {
            if (isRunning) {
                createDot();
                lastDotTime = System.currentTimeMillis();
            }
        }, delay);
    }
    private void createDot() {
        isDotActive = true;
        gameField = activity.findViewById(R.id.game_field);
        if (gameField == null) {
            System.out.println("gameField is NULL!");
            return;
        }

        int width = gameField.getWidth();
        int height = gameField.getHeight();

        int dotSize = 300;
        int maxX = Math.max(1, width - dotSize);
        int maxY = Math.max(1, height - dotSize);

        int x = random.nextInt(maxX + 1);
        int y = random.nextInt(maxY + 1);

        dot = new ImageButton(activity);
        dot.setImageResource(R.drawable.dot);
        dot.setBackgroundColor(Color.TRANSPARENT);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(dotSize, dotSize);
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;

        params.setMargins(x, y, 0, 0); // left, top, right, bottom

        dot.setLayoutParams(params);

        if (isRunning) {
            dot.setOnClickListener(v -> {
                isDotActive = false;
                long reactionTime = System.currentTimeMillis() - lastDotTime - fullPauseTime;
                clickedDotsAmount++;
                activity.updateClickedDotsAmountText(clickedDotsAmount);
                reactionTimes.add(reactionTime);
                fullPauseTime = 0;
                recordReactionTime(reactionTime);
                gameField.removeView(v);
                spawnNextDot();
            });
        }

        gameField.addView(dot);
        lastDotTime = System.currentTimeMillis();
    }

    private void recordReactionTime(long time) {
        totalClicks++;
        totalReactionTime += time;
        activity.updateAverageReaction(totalReactionTime / totalClicks);
    }

    public GameManage(GameActivity activity){
        this.activity = activity;
    }

    public void start(){
        resetGameStats();
        isRunning = true;
        spawnHandler.postDelayed(this::createDot, 1000);
    }

    public void pause(){
        pauseBeginTime = System.currentTimeMillis();
        isRunning = false;
        spawnHandler.removeCallbacksAndMessages(null);
    }

    public void resume(){
        long resumeClickedTime = System.currentTimeMillis();
        isPreparationPeriod = true;
        fullPauseTime = resumeClickedTime - pauseBeginTime - PREPARE_TIME;

        spawnHandler.postDelayed(() -> {
            isPreparationPeriod = false;
            System.out.println("Preparation period finished, clicks enabled");
            if (isDotActive){
                lastDotTime += PREPARE_TIME;
            }
        }, PREPARE_TIME);

        isRunning = true;
        if (!isDotActive)
            spawnHandler.postDelayed(this::spawnNextDot, PREPARE_TIME);
        else
            updateDotClickListener();
    }

    private void updateDotClickListener() {
        if (dot == null) return;

        dot.setOnClickListener(v -> {
            if (isPreparationPeriod) {
                System.out.println("Click blocked during preparation period!");
                return;
            }

            isDotActive = false;
            long reactionTime = System.currentTimeMillis() - lastDotTime - fullPauseTime - PREPARE_TIME;
            System.out.println("Reaction time: " + reactionTime);
            fullPauseTime = 0;
            recordReactionTime(reactionTime);
            gameField.removeView(v);
            spawnNextDot();
        });
    }

    private void resetGameStats(){
        totalClicks = 0;
        totalReactionTime = 0;
        clickedDotsAmount = 0;

    }

    public void removeDot(){
        if (dot != null && gameField != null) {
            gameField.removeView(dot);
            dot = null;
            isDotActive = false;
        }

        spawnHandler.removeCallbacksAndMessages(null);
    }

    public ArrayList<Long> getReactionsList() {
        return new ArrayList<>(reactionTimes);
    }
}
