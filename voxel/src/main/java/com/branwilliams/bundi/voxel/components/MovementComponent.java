package com.branwilliams.bundi.voxel.components;

import org.joml.Vector3f;

/**
 * Created by Brandon Williams on 6/24/2018.
 */
public class MovementComponent {

    private Vector3f velocity;

    private Vector3f acceleration;

    private float movementSpeed;

    public MovementComponent() {
        this(new Vector3f(), new Vector3f(), 1F);
    }

    public MovementComponent(float movementSpeed) {
        this(new Vector3f(), new Vector3f(), movementSpeed);
    }

    public MovementComponent(Vector3f acceleration, float movementSpeed) {
        this(new Vector3f(), acceleration, movementSpeed);
    }

    public MovementComponent(Vector3f velocity, Vector3f acceleration, float movementSpeed) {
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.movementSpeed = movementSpeed;
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

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

    @Override
    public String toString() {
        return "MovementComponent{" +
                "velocity=" + velocity +
                ", acceleration=" + acceleration +
                ", movementSpeed=" + movementSpeed +
                '}';
    }
}
