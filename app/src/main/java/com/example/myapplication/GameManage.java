package com.example.myapplication;

import android.graphics.Color;
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

    private ImageButton dot;

    private boolean isRunning = false;
    private long lastDotTime;

    private long pauseBeginTime;
    private long fullPauseTime = 0;


    private final long PREAPRE_TIME = 4000;

    private boolean isDotActive = false;

    private boolean isPreparationPeriod = false;

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

    private void spawnNextDot() {
        if (!isRunning || isDotActive) return; // если пауза или точка заспавнена, то не метод не вызывается

        long delay = 300 + (long)(Math.random() * 1200);
        spawnHandler.postDelayed(() -> {
            if (isRunning) {
                createDot();
                lastDotTime = System.currentTimeMillis();
            }
        }, delay);
    }
    private void createDot() {
        isDotActive = true; // точка зспавнена
        gameField = activity.findViewById(R.id.game_field);
        if (gameField == null) {
            System.out.println("gameField is NULL!");
            return;
        }

        int width = gameField.getWidth();
        int height = gameField.getHeight();

        int dotSize = 100;
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
                System.out.println("!!!!!!!!!!!!!!!!! R E A C T I O N  T I M E: " + reactionTime);
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
        isPreparationPeriod = true; // Начинаем период подготовки

        /**
         * нажали resume -> вычитаем время, в которое пауза началась = время, затраченное на паузу
         * вычли PREPARE_TIME = вычли время, затраченное на обратный отсчет
         */
        fullPauseTime = resumeClickedTime - pauseBeginTime - PREAPRE_TIME;

        spawnHandler.postDelayed(() -> {
            isPreparationPeriod = false;
            System.out.println("Preparation period finished, clicks enabled");
            if (isDotActive){
                lastDotTime += PREAPRE_TIME;
            }
        }, PREAPRE_TIME);

        isRunning = true;
        if (!isDotActive) // если точка не заспавнена - то заспавнить. если заспавнена, то ждем нажатия
            spawnHandler.postDelayed(this::spawnNextDot, PREAPRE_TIME); // не спавнится сразу после паузы
        else
            // Если точка уже есть, обновляем слушатель с проверкой подготовки
            updateDotClickListener();
    }

    private void updateDotClickListener() {
        if (dot == null) return;

        dot.setOnClickListener(v -> {
            // Проверяем, не в периоде ли подготовки
            if (isPreparationPeriod) {
                System.out.println("Click blocked during preparation period!");
                return; // Игнорируем клик
            }

            isDotActive = false;
            long reactionTime = System.currentTimeMillis() - lastDotTime - fullPauseTime - PREAPRE_TIME;
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
    }
}
