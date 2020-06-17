package com.branwilliams.fog;

import org.joml.Vector3f;

public class RotationAnimation {

    private final Vector3f axis;

    private final float rotationSpeed;

    public RotationAnimation(Vector3f axis, float rotationSpeed) {
        this.axis = axis;
        this.rotationSpeed = rotationSpeed;
    }

    public Vector3f getAxis() {
        return axis;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }
}
