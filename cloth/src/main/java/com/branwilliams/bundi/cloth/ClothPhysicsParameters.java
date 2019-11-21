package com.branwilliams.bundi.cloth;

/**
 * @author Brandon
 * @since November 20, 2019
 */
public class ClothPhysicsParameters {

    private float damping;

    private float timeStepSize2;

    private int constraintIterations;

    public ClothPhysicsParameters(float damping, float timeStepSize2, int constraintIterations) {
        this.damping = damping;
        this.timeStepSize2 = timeStepSize2;
        this.constraintIterations = constraintIterations;
    }

    public float getDamping() {
        return damping;
    }

    public void setDamping(float damping) {
        this.damping = damping;
    }

    public float getTimeStepSize2() {
        return timeStepSize2;
    }

    public void setTimeStepSize2(float timeStepSize2) {
        this.timeStepSize2 = timeStepSize2;
    }

    public int getConstraintIterations() {
        return constraintIterations;
    }

    public void setConstraintIterations(int constraintIterations) {
        this.constraintIterations = constraintIterations;
    }
}
