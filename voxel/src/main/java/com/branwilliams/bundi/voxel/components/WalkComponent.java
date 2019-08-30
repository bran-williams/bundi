package com.branwilliams.bundi.voxel.components;

public class WalkComponent {

    private float accelerationFactor;

    public WalkComponent(float accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
    }

    public float getAccelerationFactor() {
        return accelerationFactor;
    }

    public void setAccelerationFactor(float accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
    }
}
