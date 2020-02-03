package com.branwilliams.cubes.world;

import org.joml.Vector3i;

import static com.branwilliams.bundi.engine.util.Mathf.getTwosPower;
import static com.branwilliams.bundi.engine.util.Mathf.isPowerOfTwo;

/**
 * @author Brandon
 * @since January 26, 2020
 */
public class WorldProperties {

    private Vector3i worldDimensions;

    private Vector3i chunkDimensions;

    private float cubeSize;

    private float isoLevel;

    private final int widthBitshift;

    private final int heightBitshift;

    private final int depthBitshift;

    /**
     * @param worldDimensions The dimensions in numbers of chunks for this world. Values must all be a powers of two.
     * @param chunkDimensions The dimensions of each chunk in cubes. Values must all be a powers of two.
     * @param cubeSize The size of the individual cubes for each chunk.
     * @param isoLevel The minimum iso level for each cube.
     * */
    public WorldProperties(Vector3i worldDimensions, Vector3i chunkDimensions, float cubeSize, float isoLevel) {
        this.worldDimensions = worldDimensions;
        this.chunkDimensions = chunkDimensions;
        this.cubeSize = cubeSize;
        this.isoLevel = isoLevel;

        if (!isPowerOfTwo(worldDimensions.x) || !isPowerOfTwo(worldDimensions.y) || !isPowerOfTwo(worldDimensions.z)) {
            System.err.println(isPowerOfTwo(worldDimensions.x) + ", " + isPowerOfTwo(worldDimensions.y) + ", z=" + isPowerOfTwo(worldDimensions.z));
            throw new IllegalArgumentException("World dimensions must be in powers of two!");
        }

        if (!isPowerOfTwo(chunkDimensions.x) || !isPowerOfTwo(chunkDimensions.y) || !isPowerOfTwo(chunkDimensions.z)) {
            throw new IllegalArgumentException("Chunk dimensions must be in powers of two!");
        }

        this.widthBitshift = getTwosPower(chunkDimensions.x);
        this.heightBitshift = getTwosPower(chunkDimensions.y);
        this.depthBitshift = getTwosPower(chunkDimensions.z);
    }

    public Vector3i getWorldDimensions() {
        return worldDimensions;
    }

    public void setWorldDimensions(Vector3i worldDimensions) {
        this.worldDimensions = worldDimensions;
    }

    public Vector3i getChunkDimensions() {
        return chunkDimensions;
    }

    public void setChunkDimensions(Vector3i chunkDimensions) {
        this.chunkDimensions = chunkDimensions;
    }

    public float getCubeSize() {
        return cubeSize;
    }

    public void setCubeSize(float cubeSize) {
        this.cubeSize = cubeSize;
    }

    public float getIsoLevel() {
        return isoLevel;
    }

    public void setIsoLevel(float isoLevel) {
        this.isoLevel = isoLevel;
    }

    public int getWidthBitshift() {
        return widthBitshift;
    }

    public int getHeightBitshift() {
        return heightBitshift;
    }

    public int getDepthBitshift() {
        return depthBitshift;
    }
}
