package com.branwilliams.cubes.math;

/**
 * @author Brandon
 * @since January 26, 2020
 */
public class Torus {

    private float majorRadius;

    private float minorRadius;

    public Torus(float majorRadius, float minorRadius) {
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
    }

    public float getMajorRadius() {
        return majorRadius;
    }

    public void setMajorRadius(float majorRadius) {
        this.majorRadius = majorRadius;
    }

    public float getMinorRadius() {
        return minorRadius;
    }

    public void setMinorRadius(float minorRadius) {
        this.minorRadius = minorRadius;
    }

    @Override
    public String toString() {
        return "Torus{" +
                "majorRadius=" + majorRadius +
                ", minorRadius=" + minorRadius +
                '}';
    }
}
