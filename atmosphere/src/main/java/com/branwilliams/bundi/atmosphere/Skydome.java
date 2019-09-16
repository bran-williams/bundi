package com.branwilliams.bundi.atmosphere;

import com.branwilliams.bundi.engine.mesh.primitive.SphereMesh;
import org.joml.Vector4f;

/**
 * Created by Brandon Williams on 10/23/2018.
 */
public class Skydome {

    private SphereMesh skydomeSphere;

    private Vector4f apexColor;

    private Vector4f centerColor;

    public Skydome(SphereMesh skydomeSphere, Vector4f apexColor, Vector4f centerColor) {
        this.skydomeSphere = skydomeSphere;
        this.apexColor = apexColor;
        this.centerColor = centerColor;
    }

    public SphereMesh getSkydomeSphere() {
        return skydomeSphere;
    }

    public void setSkydomeSphere(SphereMesh skydomeSphere) {
        this.skydomeSphere = skydomeSphere;
    }

    public Vector4f getApexColor() {
        return apexColor;
    }

    public Vector4f getCenterColor() {
        return centerColor;
    }
}
