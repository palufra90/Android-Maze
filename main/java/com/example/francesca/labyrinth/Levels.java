package com.example.francesca.labyrinth;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Levels extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Typeface font = Typeface.createFromAsset(getAssets(), "good.ttf");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String message = getResources().getString(R.string.chooseMaze);
        TextView textView = (TextView) findViewById(R.id.chooseMaze);
        textView.setTypeface(font);
        textView.setText(message);
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        textView.setTextColor(color);

        Button[] buttons = new Button[4];
        buttons[0] = (Button) findViewById(R.id.button_classicDFS);
        buttons[1] = (Button) findViewById(R.id.button_classicKruskal);
        buttons[2] = (Button) findViewById(R.id.button_advDFS);
        buttons[3] = (Button) findViewById(R.id.button_advKruskal);

        // set buttons appearance
        for (int i=0; i<4; i++){
            buttons[i].setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
            buttons[i].setTypeface(font);
        }
    }
    public void level1(View view){
        Intent intent = new Intent(this, Levels1.class);
        startActivity(intent);
    }
    public void level2(View view){
        Intent intent = new Intent(this, Levels2.class);
        startActivity(intent);
    }
    public void level3(View view){
        Intent intent = new Intent(this, Levels3.class);
        startActivity(intent);
    }
    public void level4(View view){
        Intent intent = new Intent(this, Levels4.class);
        startActivity(intent);
    }
}
