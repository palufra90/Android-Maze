package com.example.francesca.labyrinth;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import com.example.francesca.labyrinth.maze.Maze;
import com.example.francesca.labyrinth.objects.*;
import com.example.francesca.labyrinth.programs.ColorShaderProgram;
import com.example.francesca.labyrinth.programs.TextureShaderProgram;
import com.example.francesca.labyrinth.util.Geometry;
import com.example.francesca.labyrinth.util.Geometry.*;

import com.example.francesca.labyrinth.util.TextureHelper;

/**
 * Created by francesca on 11/01/2016.
 */
public class MyRenderer implements MyGLSurfaceView.Renderer {

    private final Context context;
    private final int level;
    private final String mazeType;
    private Maze maze;
    private Intent intent;
    private final Handler handler;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private boolean landscape = false;

    private int uMatrixLocation;

    private Table table;
    private Ball ball;
    private Arrival arrival;
    private Lines lines;
    private ColorShaderProgram colorProgram;
    private TextureShaderProgram textureProgram;
    private int texture;

    public Point ballPosition;
    private Point startBallPosition;
    private Point arrivalPosition;
    public boolean goal;
    private Geometry.Circle ballBoundingSphere;
    private CountDown timer;
    final float halfDim = Constants.tableDim/2;

    private final float leftBound = -halfDim;
    private final float rightBound = halfDim;
    private final float topBound = halfDim;
    private final float bottomBound = -halfDim;

    public Vibrator v;

    public MyRenderer(Context context, int level, String mazeType, CountDown timer, Maze maze,
                      Handler handler, Point ballPosition) {
        this.context = context;
        this.level = level;
        this.mazeType = mazeType;
        this.timer = timer;
        this.maze = maze;
        this.handler = handler;
        this.ballPosition = ballPosition;
        //initialize vibration
        v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        //clean the surface
        GLES20.glClearColor(0.075f, 0.478f, 0.631f, 0.0f);

        table = new Table();
        float radius;
        radius = (Constants.tableDim / maze.x) / 4;
        arrival = new Arrival(radius);
        ball = new Ball(radius);
        lines = new Lines(maze);
        colorProgram = new ColorShaderProgram(context);
        textureProgram = new TextureShaderProgram(context);
        texture = TextureHelper.loadTexture(context, R.drawable.ball);

        startBallPosition = new Point ((-halfDim+(ball.radius*2)),(halfDim -ball.radius*2), 0f);
        if (ballPosition == null){
            ballPosition = startBallPosition;
        }
        arrivalPosition = new Point( (halfDim - arrival.radius * 2),
                 (-(halfDim - arrival.radius * 2)), 0f);
        goal = false;
        ballBoundingSphere = new Geometry.Circle(new Point(
                arrivalPosition.x,
                arrivalPosition.y,
                arrivalPosition.z),
                arrival.radius*1.5f);
    }


    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     *
     * @param width
     *            The new width, in pixels.
     * @param height
     *            The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        // Add orthographic projection
        final float aspectRatio = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;
        if (width > height) {
        // Landscape
            landscape = true;
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
        // Portrait or square
            landscape = false;
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // Send the projection matrix to the vertex shader
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        colorProgram.useProgram();
        positionTable();
        colorProgram.setUniforms(modelViewProjectionMatrix);
        table.bindData(colorProgram);
        table.draw();

        lines.bindData(colorProgram);
        lines.draw();


        positionObjectInScene(arrivalPosition.x, arrivalPosition.y, arrivalPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix);
        arrival.bindData(colorProgram);
        arrival.draw();

        positionObjectInScene(ballPosition.x, ballPosition.y, ballPosition.z);
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        ball.bindData(textureProgram);
        ball.draw();
    }

    // The ball is positioned on the same plane as the table.
    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionTable(){
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0,0,0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix,
                0, modelMatrix, 0);
    }

    //Handle tilt movement in portrait and landscape mode
    public void handleTilt(float pitch, float roll) {
        Point touch = new Point(ballPosition.x, ballPosition.y, ballPosition.z);
        //gravitational acceleration
        Double g = 9.8;
        float portPitch = pitch + 15;
        float landRoll = roll + 15;
        //mass of the ball
        float m = ball.radius/10;
        //pitch
        if (landscape){
            //left right landscape mode
            float deltaY = m*(float)(g*(Math.sin(Math.toRadians(pitch))));
            touch.x = ballPosition.x - deltaY;
        }else {
            //top bottom portrait mode
            float deltaY = m*(float)(g*(Math.sin(Math.toRadians(portPitch))));
            touch.y = ballPosition.y + deltaY;
        }
        //roll
        if (landscape) {
            //top bottom landscape mode
            float deltaX = m*(float)(g*(Math.sin(Math.toRadians(landRoll))));
            touch.y = ballPosition.y + deltaX;
        }else {
            //left right portrait mode
            float deltaX = m*(float)(g*(Math.sin(Math.toRadians(roll))));
            touch.x = ballPosition.x + deltaX;
        }
        //check the maze wall
        edgeDetect(touch);
        ballPosition = new Point
                (clamp(ballPosition.x, leftBound + ball.radius, rightBound - ball.radius),
                clamp(ballPosition.y, bottomBound + ball.radius, topBound - ball.radius),
                0f);

        // If the ball intersect the arrival, goal = true
        goal = Geometry.intersects(ballBoundingSphere, ballPosition);
        if (goal){
            v.vibrate(50);
            // Visualize the ball in the arrival position
            ballPosition = arrivalPosition;
            timer.cancel();
            // Call new activity if there are other level
            if (level<Constants.maxLevels) {
                intent = new Intent(context, Victory.class);
                Bundle b = new Bundle();
                b.putInt("level", level);
                b.putInt("result", 1);
                b.putString("maze", mazeType);
                intent.putExtras(b);
            } else {
                intent = new Intent(context, Levels.class);
            }
            // Execute some code after 100 milliseconds have passed
            handler.postDelayed(new Runnable() {
                public void run() {
                    // invoke startActivity on the context
                    context.startActivity(intent);
                }
            }, 100);
        }
    }

    public void edgeDetect(Point touch){
        float z = 0;
        float x = touch.x;
        float y = touch.y;
        float deltaT;
        float deltaB;
        float deltaR;
        float deltaL;
        boolean edge = false;
        for (int i = 0; i < lines.horLines.length; i+=4){
            if ((lines.horLines[i]<=touch.x) && (touch.x<=lines.horLines[i+2])){
                //top bound
                if (ballPosition.y <= lines.horLines[i+1]){
                    deltaT = lines.horLines[i+1] - touch.y;
                    if (deltaT <= ball.radius) {
                        y = lines.horLines[i + 1] - ball.radius;
                        edge = true;
                    }
                }
                //bottom bound
                else{
                    deltaB = touch.y - lines.horLines[i+1];
                    if (deltaB <= ball.radius){
                        y = lines.horLines[i+1] + ball.radius;
                        edge = true;
                    }
                }
            }
            if ((lines.verLines[i+3]<=touch.y) && (touch.y<=lines.verLines[i+1])){
                // right bound
                if (ballPosition.x <= lines.verLines[i]){
                    deltaR = lines.verLines[i] - touch.x;
                    if (deltaR <= ball.radius) {
                        x = (lines.verLines[i] - ball.radius);
                        edge = true;
                    }
                }
                //left bound
                else {
                    deltaL = touch.x - lines.verLines[i];
                    if (deltaL <= ball.radius) {
                        x = lines.verLines[i] + ball.radius;
                        edge = true;
                    }
                }
            }
        }
        //new rule for advanced level
        if ((mazeType.equals("advancedDFS") || mazeType.equals("advancedKruskal")) && edge){
            v.vibrate(50);
            timer.cancel();
            //if the ball touch the edges, call game over
            intent = new Intent(context, Victory.class);
            Bundle b = new Bundle();
            b.putInt("level", level - 1);
            b.putInt("result", 0);
            b.putString("maze", mazeType);
            intent.putExtras(b);
            // Execute some code after 100 milliseconds have passed
            handler.postDelayed(new Runnable() {
                public void run() {
                    // invoke startActivity on the context
                    context.startActivity(intent);
                }
            }, 100);
        }
        ballPosition = new Point(x, y, z);
    }
    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    // methods for restoring the ball position
    public float getBallPosX(){ return ballPosition.x; }

    public float getBallPosY(){ return ballPosition.y; }

    public void setBallPosX(float x){ ballPosition.x = x; }

    public void setBallPosY(float y){ ballPosition.y = y; }
}