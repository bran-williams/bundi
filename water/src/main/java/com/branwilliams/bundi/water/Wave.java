package com.branwilliams.bundi.water;

import org.joml.Vector2f;

/**
 * @author Brandon
 * @since September 03, 2019
 */
public class Wave {

    private float speed;

    private float amplitude;

    private float wavelength;

    private float steepness;

    private Vector2f direction;

    public Wave(float speed, float amplitude, float wavelength, float steepness, Vector2f direction) {
        this.speed = speed;
        this.amplitude = amplitude;
        this.wavelength = wavelength;
        this.steepness = steepness;
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(float amplitude) {
        this.amplitude = amplitude;
    }

    public float getWavelength() {
        return wavelength;
    }

    public void setWavelength(float wavelength) {
        this.wavelength = wavelength;
    }

    public float getSteepness() {
        return steepness;
    }

    public void setSteepness(float steepness) {
        this.steepness = steepness;
    }

    public Vector2f getDirection() {
        return direction;
    }

    public void setDirection(Vector2f direction) {
        this.direction = direction;
    }
}
