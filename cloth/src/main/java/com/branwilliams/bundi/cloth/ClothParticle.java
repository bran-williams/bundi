package com.branwilliams.bundi.cloth;

import org.joml.Vector3f;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothParticle {

    private final ClothPhysicsParameters parameters;

    private boolean movable;

    private float mass;

    private Vector3f position;

    private Vector3f oldPosition;

    private Vector3f acceleration;

    private Vector3f accumulatedNormal;

    public ClothParticle(ClothPhysicsParameters parameters, Vector3f position) {
        this.parameters = parameters;
        this.position = position;
        this.movable = true;
        this.mass = 5;
        this.oldPosition = new Vector3f(position);
        this.acceleration = new Vector3f();
        this.accumulatedNormal = new Vector3f();
    }

    public void addForce(Vector3f force) {
        this.acceleration.x += force.x / mass;
        this.acceleration.y += force.y / mass;
        this.acceleration.z += force.z / mass;
    }

    public void update() {
        if (movable) {
            Vector3f tempForOldPos = new Vector3f(position);

            float dx = (position.x - oldPosition.x) * (1F - parameters.getDamping()) + acceleration.x * parameters.getTimeStepSize2();
            float dy = (position.y - oldPosition.y) * (1F - parameters.getDamping()) + acceleration.y * parameters.getTimeStepSize2();
            float dz = (position.z - oldPosition.z) * (1F - parameters.getDamping()) + acceleration.z * parameters.getTimeStepSize2();

            position.add(dx, dy, dz);

            oldPosition.set(tempForOldPos);
            acceleration.set(0);
        }
    }

    public void resetAcceleration() {
        this.acceleration.set(0);
    }

    public void offsetPosition(Vector3f amount) {
        if (this.movable)
            this.position.add(amount);
    }

    public void addToNormal(Vector3f normal) {
        this.accumulatedNormal.add(normal.normalize());
    }

    public void resetNormal() {
        this.accumulatedNormal.set(0);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getAccumulatedNormal() {
        return accumulatedNormal;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }
}
