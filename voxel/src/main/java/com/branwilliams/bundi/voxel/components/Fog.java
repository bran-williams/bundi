package com.branwilliams.bundi.voxel.components;

public class Fog {

    private float density;

    public Fog(float density) {
        this.density = density;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }
}
