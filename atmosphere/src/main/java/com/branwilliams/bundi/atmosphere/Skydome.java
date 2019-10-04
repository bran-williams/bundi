package com.branwilliams.bundi.atmosphere;

import com.branwilliams.bundi.engine.mesh.Mesh;
import org.joml.Vector4f;

/**
 * Created by Brandon Williams on 10/23/2018.
 */
public class Skydome {

    private Mesh skydomeMesh;

    private Vector4f apexColor;

    private Vector4f centerColor;

    public Skydome(Mesh skydomeMesh, Vector4f apexColor, Vector4f centerColor) {
        this.skydomeMesh = skydomeMesh;
        this.apexColor = apexColor;
        this.centerColor = centerColor;
    }

    public Mesh getSkydomeMesh() {
        return skydomeMesh;
    }

    public void setSkydomeMesh(Mesh skydomeMesh) {
        this.skydomeMesh = skydomeMesh;
    }

    public Vector4f getApexColor() {
        return apexColor;
    }

    public Vector4f getCenterColor() {
        return centerColor;
    }
}
