package com.example.francesca.labyrinth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by francesca on 26/01/2016.
 */
public class CountDown extends CountDownTimer{
    private final TextView textView ;
    public long millisUntilFinished = 0;
    private Context context;
    private int level;
    private final String mazeType;
    public CountDown(long millisInFuture, long countDownInterval, TextView textView,
                     Context context, int level, String mazeType){
        super(millisInFuture, countDownInterval);
        this.textView = textView;
        this.context = context;
        this.level = level;
        this.mazeType = mazeType;
    }
    public void onTick(long millisUntilFinished) {
        textView.setText(String.valueOf(millisUntilFinished / 1000));
        this.millisUntilFinished = millisUntilFinished;
    }
    public void onFinish() {
        Intent intent = new Intent(context, Victory.class);
        Bundle b = new Bundle();
        b.putInt("level", level - 1);
        b.putInt("result", 0);
        b.putString("maze", mazeType);
        intent.putExtras(b);
        context.startActivity(intent);
        cancel();
    }
}
