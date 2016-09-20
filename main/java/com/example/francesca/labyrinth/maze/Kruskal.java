package com.example.francesca.labyrinth.maze;

/**
 * Created by francesca on 02/02/2016.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Kruskal extends Maze {

    // Define class variables
    public static final int N = 1;
    public static final int S = 2;
    public static final int E = 4;
    public static final int W = 8;


    private List<List<Tree>> _sets;
    private Stack<Edge> _edges;

    // Define class methods
    public static int DX(int direction) {
        switch ( direction ) {
            case E:
                return +1;
            case W:
                return -1;
            case N:
            case S:
                return 0;
        }
        // error condition, but should never reach here
        return -1;
    }
    public static int DY(int direction) {
        switch ( direction ) {
            case E:
            case W:
                return 0;
            case N:
                return -1;
            case S:
                return 1;
        }
        // error condition, but should never reach here
        return -1;
    }

    public static int OPPOSITE(int direction) {
        switch ( direction ) {
            case E:
                return W;
            case W:
                return E;
            case N:
                return S;
            case S:
                return N;
        }
        // error condition, but should never reach here
        return -1;
    }

    //
    // Standard Constructors
    //
    public Kruskal(int x,int y) {
        super(x, y);
        type = "Kruskal";
        initialize(x,y);
    }
    private void initialize(int w, int h) {
        for ( int j=0; j < h; ++j ) {
            for ( int i=0; i < w; ++i ) {
                grid[j][i] = 0;
            }
        }
        // Initialize the sets to the same dimension as the maze.
        // We use Tree objects to represent the sets to be joined.
        _sets = new ArrayList<>();
        for ( int j=0; j < y; ++j ) {
            List<Tree> tmp = new ArrayList<>();
            for ( int i=0; i < x; ++i ) {
                tmp.add(new Tree());
            }
            _sets.add(tmp);
        }

        // Build the collection of edges and randomize.
        // Edges are "north" and "west" sides of cell,
        // if index is greater than 0.
        _edges = new Stack<>();
        for ( int j=0; j < y; ++j ) {
            for (int i=0; i < x; ++i ) {
                if ( j > 0 ) 	{ _edges.add(new Edge(i,j,N)); }
                if ( i > 0 ) 	{ _edges.add(new Edge(i,j,W)); }
            }
        }
        shuffle(_edges);
        carvePassages();
    }

    /*************************************************
     * Implement Kruskal's algorithm.
     *
     * (1) Randomly select an edge.
     * (2) If the sets are not already connected, then
     * (3) Connect the sets, and
     * (4) Knock down the wall between the sets.
     * (5) Repeat at Step 1.
     *************************************************/
    private void carvePassages() {
        while ( _edges.size() > 0 ) {
            // Select the next edge, and decide which direction we are going in.
            Edge tmp = _edges.pop();
            int x = tmp.getX();
            int y = tmp.getY();
            int direction = tmp.getDirection();
            int dx = x + DX(direction), dy = y + DY(direction);

            // Pluck out the corresponding sets
            Tree set1 = (_sets.get(y)).get(x);
            Tree set2 = (_sets.get(dy)).get(dx);

            if ( !set1.connected(set2) ) {
                // Connect the two sets and "knock down" the wall between them.
                set1.connect(set2);
                grid[y][x] |= direction;
                grid[dy][dx] |= OPPOSITE(direction);
            }
        }
    }

    /**
     * Randomly shuffle a List.
     *
     * @param args List (of Edges) to be randomly shuffled.
     */
    private void shuffle(List<Edge> args) {
        for ( int i=0; i < args.size(); ++i ) {
            int pos = random.nextInt(args.size());
            Edge tmp1 = args.get(i);
            Edge tmp2 = args.get(pos);
            args.set(i,tmp2);
            args.set(pos,tmp1);
        }
    }
}

/***********************************************************************
 * We will use a tree structure to model the "set" (or "vertex")
 * that is used in Kruskal to build the graph.
 *
 * @author psholtz
 ***********************************************************************/
class Tree {

    private Tree _parent = null;

    //
    // Build a new tree object
    //
    public Tree() {

    }

    //
    // If we are joined, return the root. Otherwise, return this object instance.
    //
    public Tree root() {
        return _parent != null ? _parent.root() : this;
    }

    //
    // Are we connected to this tree?
    //
    public boolean connected(Tree tree) {
        return this.root() == tree.root();
    }

    //
    // Connect to the tree
    //
    public void connect(Tree tree) {
        tree.root().setParent(this);
    }

    //
    // Set the parent of the object instance
    public void setParent(Tree parent) {
        this._parent = parent;
    }
}

/*********************************************************************************************
 * Encapsulates the x,y coord of where the edge starts, and the direction in which it points.
 *
 * @author psholtz
 *********************************************************************************************/
class Edge {
    private int _x;
    private int _y;
    private int _direction;

    public Edge(int x, int y, int direction) {
        _x = x;
        _y = y;
        _direction = direction;
    }

    public int getX() { return _x; }
    public int getY() { return _y; }
    public int getDirection() { return _direction; }
}