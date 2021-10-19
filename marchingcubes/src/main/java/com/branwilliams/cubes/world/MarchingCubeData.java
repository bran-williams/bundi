package com.branwilliams.cubes.world;

public class MarchingCubeData implements PointData {

    private float isoValue;

    @Override
    public void setIsoValue(float value) {
        this.isoValue = value;
    }

    @Override
    public float getIsoValue() {
        return isoValue;
    }
}
