package com.example.francesca.labyrinth.objects;

/**
 * Created by francesca on 19/01/2016.
 */

import com.example.francesca.labyrinth.data.VertexArray;
import com.example.francesca.labyrinth.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.example.francesca.labyrinth.Constants.*;

public class Ball {

    public final VertexArray vertexArray;
    public float[] vertexData;
    public float[] textureData;
    public final float radius;
    private final int numPointsAroundBall = 50;

    public Ball(float radius) {
        vertexData = new float[2*(numPointsAroundBall+2)];
        this.radius = radius;
        int l = vertexData.length;
        textureData = new float[l];
        vertexData[0] = 0f;
        vertexData[1] = 0f;
        textureData[0] = 0.5f;
        textureData[1] = 0.5f;

        for (int i = 2; i<l; i=i+2){
            vertexData[i] = (radius*(float)Math.cos(i*(2*Math.PI)/numPointsAroundBall));
            textureData[i] = 0.5f*(float)Math.cos(i*(2*Math.PI)/numPointsAroundBall)+0.5f;
            vertexData[i + 1] = (radius*(float) Math.sin(i*(2*Math.PI)/numPointsAroundBall));
            textureData[i+1] = -(0.5f*(float) Math.sin(i*(2*Math.PI)/numPointsAroundBall))+0.5f;
        }
        vertexArray = new VertexArray(vertexData, textureData);
    }

    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttribPointer(0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT);

        vertexArray.setColorAttribPointer(0,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COMPONENT_COUNT);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, (numPointsAroundBall/2)+2);
    }
}