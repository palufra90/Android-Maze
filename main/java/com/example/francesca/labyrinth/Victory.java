package com.example.francesca.labyrinth;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Victory extends AppCompatActivity {

    private int level;
    private int result;
    private String mazeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        level = b.getInt("level");
        result = b.getInt("result");
        mazeType = b.getString("maze");

        setContentView(R.layout.activity_victory);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Resources res = getResources();
        String message;

        if (result == 1) {
            message = res.getString(R.string.victory);
        } else {
            message = res.getString(R.string.gameover);
        }
        TextView textView = (TextView) findViewById(R.id.result);
        textView.setTextSize(60);
        Typeface font = Typeface.createFromAsset(getAssets(), "good.ttf");
        textView.setTypeface(font);
        textView.setText(message);
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        textView.setTextColor(color);

    }

    public void playNext(View view){
        Intent intent = new Intent(this, Game.class);
        Bundle b = new Bundle();
        b.putInt("level", level + 1); //Your id
        b.putString("maze", mazeType);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }
    public void home(View view){
        Intent intent = new Intent(this, Levels.class);
        startActivity(intent);
    }

}
