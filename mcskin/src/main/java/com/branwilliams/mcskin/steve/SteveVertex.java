package com.branwilliams.mcskin.steve;

import org.joml.Vector3f;

/**
 * @author Brandon
 * @since November 26, 2019
 */
public final class SteveVertex {

    public Vector3f position;
    public float u;
    public float v;


    public SteveVertex(float x, float y, float z, float u, float v) {
        this(new Vector3f(x, y, z), u, v);
    }

    public final SteveVertex create(float u, float v) {
        return new SteveVertex(this, u, v);
    }

    private SteveVertex(SteveVertex position, float u, float v) {
        this.position = position.position;
        this.u = u;
        this.v = v;
    }

    private SteveVertex(Vector3f position, float u, float v) {
        this.position = position;
        this.u = u;
        this.v = v;
    }
}