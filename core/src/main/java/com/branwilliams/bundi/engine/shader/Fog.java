package com.branwilliams.bundi.engine.shader;

import org.joml.Vector4f;

public class Fog {

    private float density;

    private Vector4f color;

    public Fog(float density) {
        this(density, new Vector4f(1));
    }

    public Fog(float density, Vector4f color) {
        this.density = density;
        this.color = color;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }
}
