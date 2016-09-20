package com.example.francesca.labyrinth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Levels2 extends AppCompatActivity {

    private int level;
    private String mazeType = "classicKruskal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface font = Typeface.createFromAsset(getAssets(), "good.ttf");

        String message = getResources().getString(R.string.instructions);
        TextView textView = (TextView) findViewById(R.id.instructions);
        textView.setTypeface(font);
        textView.setText(message);
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        textView.setTextColor(color);

        Button[] buttons = new Button[Constants.maxLevels];
        buttons[0] = (Button) findViewById(R.id.button_first);
        buttons[1] = (Button) findViewById(R.id.button_second);
        buttons[2] = (Button) findViewById(R.id.button_third);
        buttons[3] = (Button) findViewById(R.id.button_fourth);
        buttons[4] = (Button) findViewById(R.id.button_fifth);
        buttons[5] = (Button) findViewById(R.id.button_sixth);
        buttons[6] = (Button) findViewById(R.id.button_seventh);
        buttons[7] = (Button) findViewById(R.id.button_eighth);

        // Restore preferences
        for (int i=0; i<Constants.maxLevels; i++){
            SharedPreferences level = getSharedPreferences(mazeType + (i+1), 0);
            boolean completed = level.getBoolean("completed", false);
            //change buttons appearance if level is completed
            if (completed) {
                buttons[i].setBackground(ContextCompat.getDrawable(this, R.drawable.levelcompl));
            }
            else {
                buttons[i].setBackground(ContextCompat.getDrawable(this, R.drawable.level));
            }
            buttons[i].setTypeface(font);
        }

        View fullScreen = findViewById(R.id.screen);
        fullScreen.setOnTouchListener(new OnSwipeTouchListener(this) {

            public void onSwipeRight() {
                Intent nextActivity = new Intent(Levels2.this, Levels1.class);
                startActivity(nextActivity);
                finish();
                //slide from left to right
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

            public void onSwipeLeft() {
                Intent nextActivity = new Intent(Levels2.this, Levels3.class);
                startActivity(nextActivity);
                finish();
                //slide from right to left
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        });
    }

    public void game(View view){
        level = Integer.parseInt(view.getTag().toString());
        Intent intent = new Intent(this, Game.class);
        Bundle b = new Bundle();
        b.putInt("level", level); //Your id
        b.putString("maze", mazeType);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);

    }
}