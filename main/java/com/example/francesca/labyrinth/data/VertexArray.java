package com.example.francesca.labyrinth.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import static com.example.francesca.labyrinth.Constants.BYTES_PER_FLOAT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by francesca on 19/01/2016.
 */

public class VertexArray {

    private final FloatBuffer floatBuffer;
    private final FloatBuffer colorBuffer;
    public FloatBuffer vertexData;
    public FloatBuffer colorData;

    //convert float[] to FloatBuffer
    public VertexArray(float[] coords, float[] colors) {
        //position buffer
        vertexData = ByteBuffer
                .allocateDirect(coords.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //color buffer
        colorData = ByteBuffer
                .allocateDirect(colors.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        floatBuffer = vertexData.put(coords);
        colorBuffer = colorData.put(colors);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount) {
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, 0, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }
     public void setColorAttribPointer(int dataOffset, int attributeLocation, int componentCount) {
         colorBuffer.position(dataOffset);
         glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, 0, colorBuffer);
         glEnableVertexAttribArray(attributeLocation);
         colorBuffer.position(0);
     }
}