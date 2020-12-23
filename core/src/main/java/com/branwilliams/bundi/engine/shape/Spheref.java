package com.branwilliams.bundi.engine.shape;

import org.joml.Vector3f;

public class Spheref implements Shape3f {

    private Vector3f center;

    @Override
    public boolean collides(Shape3f other) {
        return false;
    }

    @Override
    public Vector3f intersection(Shape3f other) {
        return null;
    }

    @Override
    public boolean contains(Shape3f other) {
        return false;
    }

    @Override
    public boolean contains(Vector3f point) {
        return false;
    }

    @Override
    public Vector3f getCenter() {
        return center;
    }
}
