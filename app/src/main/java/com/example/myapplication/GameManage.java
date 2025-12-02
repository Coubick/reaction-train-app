package com.example.myapplication;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;

public class GameManage{
    private int totalClicks = 0;
    private long totalReactionTime = 0;
    ConstraintLayout gameField;
    private final Random random = new Random();
    private final GameActivity activity;

    private boolean isRunning;

    private long lastDotTime;

    /**
     * spawnHandler - аналог Timer, но в UI
     *
     * Handler - это механизм, который позволяет работать с очередью сообщений.
     * Он привязан к конкретному потоку и работает с его очередью.
     * Handler умеет помещать сообщения в очередь. При этом он ставит самого
     * себя в качестве получателя этого сообщения. И когда приходит время,
     * система достает сообщение из очереди и отправляет его адресату (т.е. в
     * Handler) на обработку.
     *
     * позволяет выполнить код в отложенное время и выполнить код не в своем потоке
     */
    private final Handler spawnHandler = new Handler(Looper.getMainLooper());

    private long getRandomDelay(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            return random.nextLong(1000);
        }

        return 555;
    }

    private void spawnNextDot() {
        if (!isRunning) return;

        long delay = 300 + (long)(Math.random() * 1200);
        spawnHandler.postDelayed(() -> {
            if (isRunning) {
                createDot();
                lastDotTime = System.currentTimeMillis();
            }
        }, delay);
    }
    private void createDot() {
        gameField = activity.findViewById(R.id.game_field);
        if (gameField == null) {
            System.out.println("gameField is NULL!");
            return;
        }

        // Проверяем, известны ли размеры
        if (gameField.getWidth() == 0 || gameField.getHeight() == 0) {
            // Откладываем вызов до следующего кадра, когда размеры будут известны
            gameField.post(this::createDot);
            return;
        }

        int width = gameField.getWidth();
        int height = gameField.getHeight();
        System.out.println("GameField size: " + width + " x " + height);

        int dotSize = 100;
        int maxX = Math.max(1, width - dotSize);
        int maxY = Math.max(1, height - dotSize);

        int x = random.nextInt(maxX + 1);
        int y = random.nextInt(maxY + 1);

        ImageButton dot = new ImageButton(activity);
        dot.setImageResource(R.drawable.dot);
        dot.setBackgroundColor(Color.TRANSPARENT);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(dotSize, dotSize);
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;

        params.setMargins(x, y, 0, 0); // left, top, right, bottom

        dot.setLayoutParams(params);

        dot.setOnClickListener(v -> {
            long reactionTime = System.currentTimeMillis() - lastDotTime;
            recordReactionTime(reactionTime);
            gameField.removeView(v);
            spawnNextDot();
        });

        gameField.addView(dot);
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
        spawnHandler.postDelayed(this::createDot, 0);
    }

    public void pause(){
        spawnHandler.removeCallbacksAndMessages(null);
    }

    public void resume(){
        spawnHandler.postDelayed(this::spawnNextDot, getRandomDelay());
    }

    public void reset(){

    }


}
