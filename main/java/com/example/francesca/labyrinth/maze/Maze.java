package com.example.francesca.labyrinth.maze;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by francesca on 15/01/2016.
 */
public abstract class Maze {

    public final int x;
    public final int y;
    public int[][] grid;
    public String type;
    //protected Long seed = 300L;
    protected Random random = null;

    public Maze(int x, int y){
        this.x = x;
        this.y = y;
        grid = new int[x][y];
        this.random = new Random();
    }

    public static float[] concatAll(float[] first, float[]... rest) {
        int totalLength = first.length;
        for (float[] array : rest) {
            totalLength += array.length;
        }
        float[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (float[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

}
