package com.example.francesca.labyrinth;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button play = (Button) findViewById(R.id.button_play);
        Typeface font = Typeface.createFromAsset(getAssets(), "good.ttf");
        play.setTypeface(font);
    }

    public void startGame(View view){
        Intent intent = new Intent(this, Levels.class);
        startActivity(intent);
    }
}
