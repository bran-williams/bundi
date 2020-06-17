package com.branwilliams.bundi.water;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.material.Material;
import com.branwilliams.bundi.engine.shader.Transformable;
import com.branwilliams.bundi.engine.shader.Transformation;
import com.branwilliams.bundi.engine.texture.CubeMapTexture;
import com.branwilliams.bundi.water.pipeline.WaterNormalBuffer;
import org.joml.Vector4f;

/**
 * @author Brandon
 * @since September 03, 2019
 */
public class Water implements Destructible {

    public static final int NUMBERWAVES = 4;

    private Transformable transformable;

    private WaterNormalBuffer normalBuffer;

    private Mesh waterMesh;

    private Material material;

    private int planeLength;

    private float passedTime;

    private Vector4f color;

    private Wave[] normalWaves;

    private Wave[] surfaceWaves;

    private boolean isCopy;

    public Water(Wave[] normalWaves, Wave[] surfaceWaves, int planeLength) {
        if (normalWaves.length != NUMBERWAVES || surfaceWaves.length != NUMBERWAVES) {
            throw new IllegalArgumentException("Water tiles must have " + NUMBERWAVES + " waves.");
        }
        this.transformable = new Transformation();
        this.color = new Vector4f();
        this.normalWaves = normalWaves;
        this.surfaceWaves = surfaceWaves;
        this.planeLength = planeLength;
    }

    public void initialize(CubeMapTexture environment, int width, int height) {
        normalBuffer = new WaterNormalBuffer(width, height);

        material = new Material(environment, normalBuffer.getNormal());
        waterMesh = new PlaneMesh(planeLength);
    }

    public Water copy() {
        Water water = new Water(this.normalWaves, this.surfaceWaves, this.planeLength);
        water.normalBuffer = this.normalBuffer;
        water.material = this.material;
        water.waterMesh = this.waterMesh;
        water.color = this.color;
        water.isCopy = true;
        return water;
    }

    public boolean isCopy() {
        return isCopy;
    }

    public WaterNormalBuffer getNormalBuffer() {
        return normalBuffer;
    }

    public Transformable getTransformable() {
        return transformable;
    }

    public Mesh getWaterMesh() {
        return waterMesh;
    }

    public Material getMaterial() {
        return material;
    }

    public Vector4f getColor() {
        return color;
    }

    public void setColor(Vector4f color) {
        this.color = color;
    }

    public Wave[] getNormalWaves() {
        return normalWaves;
    }

    public void setNormalWaves(Wave[] normalWaves) {
        this.normalWaves = normalWaves;
    }

    public Wave[] getSurfaceWaves() {
        return surfaceWaves;
    }

    public void setSurfaceWaves(Wave[] surfaceWaves) {
        this.surfaceWaves = surfaceWaves;
    }

    public int getPlaneLength() {
        return planeLength;
    }

    public void setPlaneLength(int planeLength) {
        this.planeLength = planeLength;
    }

    public float getPassedTime() {
        return passedTime;
    }

    public void setPassedTime(float passedTime) {
        this.passedTime = passedTime;
    }

    @Override
    public void destroy() {
        normalBuffer.destroy();
        waterMesh.destroy();
        material.destroy();
    }
}
