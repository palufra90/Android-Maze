package com.example.francesca.labyrinth.data;

import com.example.francesca.labyrinth.maze.Maze;
import com.example.francesca.labyrinth.util.Geometry.Point;

/**
 * Created by francesca on 05/02/2016.
 */
public class RetainedData {
    public Maze maze;
    public long seconds;
    public Point ballPosition;
    public RetainedData(Maze maze, long seconds, Point ballPosition){
        this.maze = maze;
        this.seconds = seconds;
        this.ballPosition = ballPosition;
    }

    public void setMaze(Maze maze){
        this.maze = maze;
    }
    public void setSeconds(long seconds){
        this.seconds = seconds;
    }
    public void setBallPosition(Point ballPosition){
        this.ballPosition = ballPosition;
    }
    public Maze getMaze(){ return maze;}
    public long getSeconds() { return seconds; }
    public Point getBallPosition() {return ballPosition;}
}
