package com.example.francesca.labyrinth;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.francesca.labyrinth.data.RetainedData;
import com.example.francesca.labyrinth.maze.DFS;
import com.example.francesca.labyrinth.maze.Kruskal;
import com.example.francesca.labyrinth.maze.Maze;
import com.example.francesca.labyrinth.util.Geometry.Point;

public class Game extends FragmentActivity implements SensorEventListener{

    private MyGLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    public CountDown timer;
    private TextView textTimer;
    private TextView textLevel;
    private boolean paused;
    private boolean sleep;
    private int level;
    private String mazeType;
    private long seconds;
    private Maze maze;
    private Point currentBallPos;
    private Point startBallPosition;
    private SharedPreferences currentLevel;

    private RetainedFragment dataFragment;
    private RetainedData retainedData;
    private static boolean isDataLoaded = false;

    // Reference to the renderer for touch events
    private MyRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        level = b.getInt("level");
        mazeType = b.getString("maze");
        currentBallPos = new Point(0, 0, 0);
        startBallPosition = null;

        currentLevel = getSharedPreferences(mazeType + level, 0);
        // handler to delay the victory message in the renderer
        Handler handler = new Handler();

        setContentView(R.layout.activity_game);
        glSurfaceView = (MyGLSurfaceView) findViewById(R.id.glSurfaceViewID);

        //get the saved fragment on activity restart
        FragmentManager fm = getSupportFragmentManager();
        dataFragment = (RetainedFragment) fm.findFragmentByTag("data");

        //running the activity for the first time
        if (savedInstanceState == null){
            //create fragment and data for the first time
            dataFragment = new RetainedFragment();
            //add the fragment
            fm.beginTransaction().add(dataFragment, "data").commit();
            generateMaze();
        }else{
            if(isDataLoaded){
                //we are recreating the activity
                //get the data object out of the fragment
                retainedData = dataFragment.getData();
                maze = retainedData.getMaze();
                seconds = retainedData.getSeconds();
                startBallPosition = retainedData.getBallPosition();
            } else {
                generateMaze();
            }
        }
        textLevel = (TextView)findViewById(R.id.level);
        textTimer = (TextView)findViewById(R.id.timer);
        timer = new CountDown(seconds, 1000, textTimer, this, level, mazeType);

        renderer = new MyRenderer(this, level, mazeType, timer, maze, handler, startBallPosition);

        // Check if the system supports OpenGL ES 2.0.
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = activityManager
                .getDeviceConfigurationInfo();
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            //glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.
            glSurfaceView.setRenderer(renderer);

            // Render the view only when there is a change in the drawing data
            //glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

            rendererSet = true;
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since
             * we're not doing anything, the app will crash if the device
             * doesn't support OpenGL ES 2.0. If we publish on the market, we
             * should also add the following to AndroidManifest.xml:
             *
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             *
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }


        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        textTimer.setTextSize(40);
        Typeface font = Typeface.createFromAsset(getAssets(), "good.ttf");
        textTimer.setTypeface(font);
        int color = ContextCompat.getColor(this, R.color.colorAccent);
        textTimer.setTextColor(color);
        textTimer.setGravity(Gravity.CENTER_VERTICAL);

        textLevel.setTypeface(font);
        textLevel.setTextColor(color);
        textLevel.setGravity(Gravity.CENTER_VERTICAL);
        textLevel.setText(mazeType + " : " + level);

        sleep = true;
        Handler handler1 = new Handler();
        // Execute some code after 1000 milliseconds have passed
        handler1.postDelayed(new Runnable() {
            public void run() {
                // invoke startActivity on the context
                sleep = false;
            }
        }, 1000);
        timer.start();
        paused = false;
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor arg0, int arg1) {

    }

    float[] mGravity;
    float[] mGeomagnetic;
    float pitch;
    float roll;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = sensorEvent.values.clone();
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = sensorEvent.values.clone();
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            //when the surface is created the game starts after one second
            if (success && !sleep) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                pitch = (float) Math.toDegrees(orientation[1]);
                roll = (float) Math.toDegrees(orientation[2]);

                glSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        renderer.handleTilt(pitch, roll);
                    }
                });
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (rendererSet) {
            glSurfaceView.onPause();
        }
        timer.cancel();
        paused = true;
        currentBallPos.x = renderer.getBallPosX();
        currentBallPos.y = renderer.getBallPosY();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        if (rendererSet) {
            glSurfaceView.onResume();
        }
        if (paused) {
            long millisInFuture = timer.millisUntilFinished;
            timer = new CountDown(millisInFuture, 1000, textTimer, this, level, mazeType);
            timer.start();
            paused = false;
            renderer.setBallPosX(currentBallPos.x);
            renderer.setBallPosY(currentBallPos.y);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (accelerometer != null || magnetometer != null) {
            sensorManager.unregisterListener(this);
        }
        timer.cancel();
        // Save level completed
        // if game won
        if (renderer.goal) {
            // Save completed level on preferences
            SharedPreferences.Editor editor = currentLevel.edit();
            editor.putBoolean("completed", true);
            // Commit the edits!
            editor.commit();
        }
        currentBallPos.x = renderer.getBallPosX();
        currentBallPos.y = renderer.getBallPosY();
        paused = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
        if (rendererSet) {
            glSurfaceView.onResume();
        }
        if (paused) {
            long millisInFuture = timer.millisUntilFinished;
            timer = new CountDown(millisInFuture, 1000, textTimer, this, level, mazeType);
            timer.start();
            paused = false;
            renderer.setBallPosX(currentBallPos.x);
            renderer.setBallPosY(currentBallPos.y);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (accelerometer != null || magnetometer != null) {
            sensorManager.unregisterListener(this);
        }
        long millisUntilFinished = timer.millisUntilFinished;
        timer.cancel();
        retainedData.setSeconds(millisUntilFinished);
        currentBallPos.x = renderer.getBallPosX();
        currentBallPos.y = renderer.getBallPosY();
        retainedData.setBallPosition(currentBallPos);
        dataFragment.setData(retainedData);
    }

    // Buttons' actions
    public void stop(View view) {
        timer.cancel();
        if (accelerometer != null || magnetometer != null) {
            sensorManager.unregisterListener(this);
        }
        Intent intent = new Intent(this, Levels.class);
        startActivity(intent);
        finish();
    }

    public void pause(View view) {
        timer.cancel();
        if (accelerometer != null || magnetometer != null) {
            sensorManager.unregisterListener(this);
        }
        paused = true;
    }

    public void play(View view) {
        if (paused) {
            long millisInFuture = timer.millisUntilFinished;
            timer = new CountDown(millisInFuture, 1000, textTimer, this, level, mazeType);
            timer.start();
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
            paused = false;
        }
    }

    //generate the maze according to the level and the maze type
    public void generateMaze() {
        switch (mazeType) {
            case ("classicDFS"): {
                System.out.println(mazeType);
                maze = new DFS((level + 1) * 2, (level + 1) * 2);
                break;
            }
            case ("classicKruskal"): {
                System.out.println(mazeType);
                maze = new Kruskal((level + 1) * 2, (level + 1) * 2);
                break;
            }
            case ("advancedDFS"): {
                System.out.println(mazeType);
                maze = new DFS((level + 1) * 2, (level + 1) * 2);
                break;

            }
            case ("advancedKruskal"): {
                System.out.println(mazeType);
                maze = new Kruskal((level + 1) * 2, (level + 1) * 2);
                break;
            }
        }
        seconds = level * 10000 + 1000;
        retainedData = new RetainedData(maze, seconds, null);
        dataFragment.setData(retainedData);
        isDataLoaded = true;
    }
}
