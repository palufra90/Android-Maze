package com.example.francesca.labyrinth.objects;

import com.example.francesca.labyrinth.data.VertexArray;
import com.example.francesca.labyrinth.programs.ColorShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.example.francesca.labyrinth.Constants.COLOR_COMPONENT_COUNT;
import static com.example.francesca.labyrinth.Constants.POSITION_COMPONENT_COUNT;

/**
 * Created by francesca on 25/01/2016.
 */
public class Arrival {
    public final VertexArray vertexArray;
    public float[] vertexData;
    public float[] colorData;
    public final float radius;
    private final int numPointsAroundBall = 50;

    public Arrival(float radius) {
        vertexData = new float[2*(numPointsAroundBall+2)];
        this.radius = radius;
        int l = vertexData.length;
        vertexData[0] = 0f;
        vertexData[1] = 0f;

        for (int i = 2; i<l; i=i+2){
            vertexData[i] = (radius*(float)Math.cos(i*(2*Math.PI)/numPointsAroundBall));
            vertexData[i + 1] = (radius*(float) Math.sin(i*(2*Math.PI)/numPointsAroundBall));
        }

        colorData = new float[l*3];

        colorData[0] = 0.5f;
        colorData[1] = 0.5f;
        colorData[2] = 0.1f;

        for (int i = 3; i<l*3; i=i+3){
            colorData[i] = 0.7f;
            colorData[i+1] = 0.7f;
            colorData[i+2] = 0f;
        }
        vertexArray = new VertexArray(vertexData, colorData);

    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT);

        vertexArray.setColorAttribPointer(0, colorProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, (numPointsAroundBall/2)+2);
    }
}
