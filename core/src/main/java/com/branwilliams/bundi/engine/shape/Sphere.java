package com.branwilliams.bundi.engine.shape;

import org.joml.Vector3f;

public class Sphere implements Shape3f {

    private Vector3f center;

    private float radius;

    public Sphere(float radius) {
        this(new Vector3f(), radius);
    }

    public Sphere(Vector3f center, float radius) {
        this.center = center;
        this.radius = radius;
    }

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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
