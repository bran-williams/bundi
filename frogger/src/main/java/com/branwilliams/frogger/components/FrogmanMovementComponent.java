package com.branwilliams.frogger.components;

import org.joml.Vector3f;

public class FrogmanMovementComponent {

    private Vector3f velocity;

    private Vector3f acceleration;

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3f acceleration) {
        this.acceleration = acceleration;
    }
}
