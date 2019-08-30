package com.branwilliams.bundi.engine.shader;

import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.engine.core.Window;
import org.joml.Matrix4f;

/**
 * Created by Brandon Williams on 1/2/2018.
 */
public class Projection {

    private final Matrix4f projectionMatrix = new Matrix4f();

    private final Window window;

    private int fov;

    private float near, far;

    private boolean ortho;

    private boolean dirty = true;

    public Projection(Window window, int fov, float near, float far) {
        this.window = window;
        this.fov = fov;
        this.near = near;
        this.far = far;
        this.ortho = false;
    }

    /**
     * Creates an orthographic projection, based on the width and height of the window.
     * */
    public Projection(Window window) {
        this.window = window;
        this.ortho = true;
    }

    /**
     * Updates the matrix of this projection.
     * */
    public void update() {
        if (ortho) {
            Mathf.createOrtho2dMatrix(projectionMatrix, window);
        } else {
            Mathf.createPerspectiveMatrix(window, projectionMatrix, fov, near, far);
        }
        dirty = false;
    }

    public int getFov() {
        return fov;
    }

    public void setFov(int fov) {
        this.fov = fov;
        this.dirty = true;
    }

    public float getNear() {
        return near;
    }

    public void setNear(float near) {
        this.near = near;
        this.dirty = true;
    }

    public float getFar() {
        return far;
    }

    public void setFar(float far) {
        this.far = far;
        this.dirty = true;
    }

    public boolean isOrtho() {
        return ortho;
    }

    public Matrix4f toProjectionMatrix() {
        if (dirty) {
            update();
        }
        return projectionMatrix;
    }

}
