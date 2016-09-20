package com.example.francesca.labyrinth.objects;

/**
 * Created by francesca on 19/01/2016.
 */

import com.example.francesca.labyrinth.data.VertexArray;
import com.example.francesca.labyrinth.programs.ColorShaderProgram;
import static com.example.francesca.labyrinth.Constants.*;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;

public class Table {
    static float halfDim = tableDim/2;
    private static final float[] VERTEX_DATA = {
            // Triangle fan
            // Order of coordinates: X, Y
            0f, 0f,
            -halfDim, -halfDim,
            halfDim, -halfDim,
            halfDim, halfDim,
            -halfDim, halfDim,
            -halfDim, -halfDim,

    };

    private static final float[] COLOR_DATA = {
            // Order of coordinates: R, G, B
            1f, 1f, 1f,

            1f, 0.91f, 0.08f,
            1f, 0.91f, 0.08f,
            1f, 0.91f, 0.08f,
            1f, 0.91f, 0.08f,
            1f, 0.91f, 0.08f,
    };

    public VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA, COLOR_DATA);
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT);

        vertexArray.setColorAttribPointer(0, colorProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }

}

