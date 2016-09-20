package com.example.francesca.labyrinth.objects;

/**
 * Created by francesca on 20/01/2016.
 */

import android.graphics.Color;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glLineWidth;
import static com.example.francesca.labyrinth.Constants.*;

import com.example.francesca.labyrinth.maze.Maze;
import com.example.francesca.labyrinth.data.VertexArray;
import com.example.francesca.labyrinth.programs.ColorShaderProgram;

public class Lines {
    public VertexArray vertexArray;
    public float[] horLines;
    public float[] verLines;
    public int dim;
    // Define class variables
    public static final int S = 2;
    public static final int E = 4;

    public Lines(Maze maze){

        float red = (float)Color.red(colorPrimary)/255;
        float green = (float)Color.green(colorPrimary)/255;
        float blue = (float)Color.blue(colorPrimary)/255;

        dim = 4+(4*maze.x*maze.y);
        horLines = new float[dim];
        verLines = new float[dim];
        int i = 0;
        int h = 0;
        float step = tableDim/maze.x;
        int raw = maze.x;
        int col = maze.y;
        float halfDim = tableDim/2;


        if (maze.type == "DFS" ) {
            // DFS maze
            // Horizontal segments
            for (int k = 0; k<raw; k++){
                for (int j = 0; j<col; j++) {
                    if (((maze.grid[j][k]) & 1) == 0) {
                        horLines[i] = -halfDim + (step * j);
                        horLines[i + 1] = halfDim - (step * k);
                        horLines[i + 2] = -halfDim + step + (step * j);
                        horLines[i + 3] = halfDim - (step * k);
                        i += 4;
                    }
                    //Vertical segments
                    if (((maze.grid[j][k]) & 8) == 0) {
                        verLines[h] = -halfDim + (step * j);
                        verLines[h + 1] = halfDim - (step * k);
                        verLines[h + 2] = -halfDim + (step * j);
                        verLines[h + 3] = halfDim - step - (step * k);
                        h += 4;
                    }
                }
            }
            //bottom line
            horLines[i] = -halfDim;
            horLines[i+1] = -halfDim;
            horLines[i+2] = halfDim;
            horLines[i+3] = -halfDim;
            //right line
            verLines[h] = halfDim;
            verLines[h+1] = halfDim;
            verLines[h+2] = halfDim;
            verLines[h+3] = -halfDim;
        } else {
            // Kruskal maze
            // Horizontal segments
            for (int k = 0; k < raw; k++) {
                for (int j = 0; j < col; j++) {
                    if (((maze.grid[k][j]) & S) == 0) {
                        horLines[i] = -halfDim + (step * j);
                        horLines[i + 1] = halfDim - step - (step * k);
                        horLines[i + 2] = -halfDim + step + (step * j);
                        horLines[i + 3] = halfDim - step - (step * k);
                        i += 4;
                    }
                    //Vertical segments
                    if (((maze.grid[k][j]) & E) == 0) {
                        verLines[h] = -halfDim + step + (step * j);
                        verLines[h + 1] = halfDim - (step * k);
                        verLines[h + 2] = -halfDim + step + (step * j);
                        verLines[h + 3] = halfDim - step - (step * k);
                        h += 4;
                    }
                }
            }
            //top line
            horLines[i] = -halfDim;
            horLines[i + 1] = halfDim;
            horLines[i + 2] = halfDim;
            horLines[i + 3] = halfDim;
            //left line
            verLines[h] = -halfDim;
            verLines[h + 1] = halfDim;
            verLines[h + 2] = -halfDim;
            verLines[h + 3] = -halfDim;
        }


        float[] linesColor = new float[3*dim];

        for (int f=0; f<3*dim; f=f+3){
            linesColor[f]= red;
            linesColor[f+1]= green;
            linesColor[f+2]= blue;
        }

        float[] lines = Maze.concatAll(horLines, verLines);

        vertexArray = new VertexArray(lines, linesColor);

    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT);

        vertexArray.setColorAttribPointer(0, colorProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT);
    }

    public void draw() {
        glLineWidth(5);
        glDrawArrays(GL_LINES, 0, dim);
    }
}
