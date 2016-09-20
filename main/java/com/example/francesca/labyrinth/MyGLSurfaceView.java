package com.example.francesca.labyrinth;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by francesca on 25/01/2016.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    //private final MyRenderer mRenderer;

    public MyGLSurfaceView(Context context, AttributeSet attributes) {
        super(context, attributes);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
    }
}
