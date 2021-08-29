package com.branwilliams.fog;

import org.joml.AxisAngle4f;

public class RotationAnimation {

    private AxisAngle4f axisAngle;

    private final float rotationSpeed;

    public RotationAnimation(AxisAngle4f axisAngle, float rotationSpeed) {
        this.axisAngle = axisAngle;
        this.rotationSpeed = rotationSpeed;
    }

    public AxisAngle4f getAxisAngle() {
        return axisAngle;
    }

    public void setAxisAngle(AxisAngle4f axisAngle) {
        this.axisAngle = axisAngle;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }
}
