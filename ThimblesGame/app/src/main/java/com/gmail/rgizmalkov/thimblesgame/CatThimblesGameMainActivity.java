package com.gmail.rgizmalkov.thimblesgame;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;

/**
 * Задание:
 * 1. Три квадра, под одним из них генератор прячет шарит
 * 2. По клику на одному из них - открываются все три
 * 3. Добавить кнопку "Новая игра" - появляется после открытия "наперстков"
 * 4. Любое вращение экрана должно сохранять состояние игры неизменным
 *
 *  P.S. Вместо квадратов коты - в случае успеха высветится довольный кот, в противном надпись No
 */
public class CatThimblesGameMainActivity extends Activity {
    private static final String USER_ANSWER = "USER_ANSWER";
    private static final String SYSTEM_ANSWER = "SYSTEM_ANSWER";
    private static final String SHOW_BUTTON_PRESSED = "SHOW_BUTTON_PRESSED";

    private static final int FIRST_MASK = 1;
    private static final int SECOND_MASK = 10;
    private static final int THIRD_MASK = 100;
    private static Random ANSWER_RANDOMIZER = new Random();

    private ImageView first;
    private ImageView second;
    private ImageView third;

    private Button rest;
    private Button show;

    private Integer userAnswer = null;
    private Integer systemAnswer = null;
    private boolean showButtonPressed = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.first = findViewById(R.id.firstCat);
        this.second = findViewById(R.id.secondCat);
        this.third = findViewById(R.id.thirdCat);

        this.first.setOnClickListener(new ThimbleClickHandler(FIRST_MASK));
        this.second.setOnClickListener(new ThimbleClickHandler(SECOND_MASK));
        this.third.setOnClickListener(new ThimbleClickHandler(THIRD_MASK));

        this.rest = findViewById(R.id.rest);
        this.show = findViewById(R.id.show);

        this.rest.setOnClickListener((view) -> {
            this.first.setImageDrawable(getDrawable(R.drawable.game_cat));
            this.second.setImageDrawable(getDrawable(R.drawable.game_cat));
            this.third.setImageDrawable(getDrawable(R.drawable.game_cat));
            this.userAnswer = null;
            showButtons(false);
            generateAnswer();
            this.showButtonPressed = false;
        });

        this.show.setOnClickListener((view) -> {
            this.first.setImageDrawable(getCurrentStatePicture(FIRST_MASK));
            this.second.setImageDrawable(getCurrentStatePicture(SECOND_MASK));
            this.third.setImageDrawable(getCurrentStatePicture(THIRD_MASK));
            this.showButtonPressed = true;
        });


        if(savedInstanceState != null){
            this.userAnswer = savedInstanceState.getInt(USER_ANSWER);
            this.systemAnswer = savedInstanceState.getInt(SYSTEM_ANSWER);
            this.showButtonPressed = savedInstanceState.getBoolean(SHOW_BUTTON_PRESSED);
            if(showButtonPressed){
                this.first.setImageDrawable(getCurrentStatePicture(FIRST_MASK));
                this.second.setImageDrawable(getCurrentStatePicture(SECOND_MASK));
                this.third.setImageDrawable(getCurrentStatePicture(THIRD_MASK));
            }else {
                this.first.setImageDrawable(getCurrentStatePictureOnRestart(FIRST_MASK));
                this.second.setImageDrawable(getCurrentStatePictureOnRestart(SECOND_MASK));
                this.third.setImageDrawable(getCurrentStatePictureOnRestart(THIRD_MASK));
            }
            if(userAnswer == 0) {
                showButtons(false);
                userAnswer = null;
            }
        }else {
            showButtons(false);
            generateAnswer();
            this.showButtonPressed = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        putIfNotAbsent(outState,USER_ANSWER, userAnswer);
        putIfNotAbsent(outState,SYSTEM_ANSWER, systemAnswer);
        outState.putBoolean(SHOW_BUTTON_PRESSED, showButtonPressed);
    }

    private void putIfNotAbsent(Bundle bundle, String key, Integer val){
        if(val != null) bundle.putInt(key, val);
    }

    private class ThimbleClickHandler implements View.OnClickListener{

        private final int systemAnswerValue;

        private ThimbleClickHandler(int systemAnswerValue) {
            this.systemAnswerValue = systemAnswerValue;
        }

        @Override
        public void onClick(View view) {
            if(userAnswer == null){
                userAnswer = systemAnswerValue;
                ((ImageView) view).setImageDrawable(getCurrentStatePicture(userAnswer));
                showButtons(true);
            }
        }
    }

    private Drawable getCurrentStatePicture(int val){
        return getDrawable((val & systemAnswer) == val ? R.drawable.success_cat : R.drawable.fail);
    }

    private Drawable getCurrentStatePictureOnRestart(int val){
        if(userAnswer - systemAnswer == 0){
            return getCurrentStatePicture(val);
        }else {
            if(val - userAnswer == 0){
                return getCurrentStatePicture(val);
            }else {
                return getDrawable(R.drawable.game_cat);
            }
        }
    }

    private void generateAnswer(){
        int rand = ANSWER_RANDOMIZER.nextInt(3);
        switch (rand){
            case 0: this.systemAnswer = FIRST_MASK; break;
            case 1: this.systemAnswer = SECOND_MASK; break;
            case 2: this.systemAnswer = THIRD_MASK; break;
            default:
                throw new UnsupportedOperationException("Generated number out of game bounds:" + rand);
        }
    }

    private void showButtons(boolean visible){
        int visibility = visible ? View.VISIBLE : View.INVISIBLE;
        this.rest.setVisibility(visibility);
        this.show.setVisibility(visibility);
    }


}
