package com.branwilliams.bundi.voxel.world.chunk;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.util.Grid3i;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.VoxelConstants;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import org.joml.Vector3i;

/**
 * Represents a chunk of a VoxelWorld
 *
 * @author Brandon
 * @since July 21, 2019
 */
public class VoxelChunk implements Destructible {

    public final ChunkPos chunkPos;

    private Grid3i<Voxel> kernel;

    private Grid3i<Integer> lightmap;

    private boolean dirty = true;

    private AABB aabb;

    public VoxelChunk(ChunkPos chunkPos, Grid3i<Voxel> kernel) {
        this.chunkPos = chunkPos;
        this.kernel = kernel;
        this.lightmap = new Grid3i<>(Integer[]::new, kernel.getWidth(), kernel.getHeight(), kernel.getDepth());
        this.aabb = new AABB(chunkPos.getWorldX(), 0F, chunkPos.getWorldZ(),
                chunkPos.getWorldX() + VoxelConstants.CHUNK_X_SIZE,
                VoxelConstants.CHUNK_Y_SIZE,
                chunkPos.getWorldZ() + VoxelConstants.CHUNK_Z_SIZE);
    }

    /**
     *
     * */
    public Voxel getVoxelAtPosition(float x, float y, float z) {
        return getVoxelAtPosition(Mathf.floor(x), Mathf.floor(y), Mathf.floor(z));
    }

    /**
     *
     * */
    public Voxel getVoxelAtPosition(int x, int y, int z) {
        return getVoxel(ChunkPos.toKernelX(x), y, ChunkPos.toKernelZ(z));
    }

    /**
     * Finds the voxel within this chunks kernel at the provided position plus the face offset. All coordinates outside
     * of the kernel will be clamped to the edge.
     *
     * @param x The x position within this chunks kernel (MIN_KERNEL_X ~ MAX_KERNEL_X)
     * @param y The y position within this chunks kernel (MIN_KERNEL_Y ~ MAX_KERNEL_Y)
     * @param z The z position within this chunks kernel (MIN_KERNEL_Z ~ MAX_KERNEL_Z)
     * @return The {@link Voxel} stored within this chunks kernel at the kernel x, y, and z coordinates.
     * */
    public int getLightFacingPosition(int x, int y, int z, VoxelFace face) {
        return getLightAtPosition(x + face.xDirection, y + face.yDirection, z + face.zDirection);
    }

    public int getLightAtPosition(float x, float y, float z) {
        return getLightAtPosition(Mathf.floor(x), Mathf.floor(y), Mathf.floor(z));
    }

    public int getLightAtPosition(int x, int y, int z) {
        return getLight(ChunkPos.toKernelX(x), y, ChunkPos.toKernelZ(z));
    }

    public boolean setLightAtPosition(int light, float x, float y, float z) {
        return setLightAtPosition(light, Mathf.floor(x), Mathf.floor(y), Mathf.floor(z));
    }

    public boolean setLightAtPosition(int light, int x, int y, int z) {
        return setLight(light, ChunkPos.toKernelX(x), y, ChunkPos.toKernelZ(z));
    }

    /**
     * Finds the voxel within this chunks kernel at the provided position plus the face offset. All coordinates outside
     * of the kernel will be clamped to the edge.
     *
     * @param x The x position within this chunks kernel (MIN_KERNEL_X ~ MAX_KERNEL_X)
     * @param y The y position within this chunks kernel (MIN_KERNEL_Y ~ MAX_KERNEL_Y)
     * @param z The z position within this chunks kernel (MIN_KERNEL_Z ~ MAX_KERNEL_Z)
     * @return The {@link Voxel} stored within this chunks kernel at the kernel x, y, and z coordinates.
     * */
    public Voxel getVoxelFacingPosition(int x, int y, int z, VoxelFace face) {
        return getVoxel(x + face.xDirection, y + face.yDirection, z + face.zDirection);
    }

    /**
     * Finds the voxel within this chunks kernel at the provided position. All coordinates outside of the kernel will be
     * clamped to the edge.
     *
     * @param x The x position within this chunks kernel (MIN_KERNEL_X ~ MAX_KERNEL_X)
     * @param y The y position within this chunks kernel (MIN_KERNEL_Y ~ MAX_KERNEL_Y)
     * @param z The z position within this chunks kernel (MIN_KERNEL_Z ~ MAX_KERNEL_Z)
     * @return The {@link Voxel} stored within this chunks kernel at the kernel x, y, and z coordinates.
     * */
    public Voxel getVoxel(int x, int y, int z) {
        x = Mathf.clamp(x, 0, kernel.getWidth() - 1);
        y = Mathf.clamp(y, 0, kernel.getHeight() - 1);
        z = Mathf.clamp(z, 0, kernel.getDepth() - 1);

        return kernel.getValue(x, y, z);
    }


    /**
     * Sets the voxel at the position within this chunk. If any changes are made, this chunk is marked as dirty.
     *
     * @param voxel The voxel to set at this chunk position.
     * @param x The x position within this chunks kernel (MIN_KERNEL_X ~ MAX_KERNEL_X)
     * @param y The y position within this chunks kernel (MIN_KERNEL_Y ~ MAX_KERNEL_Y)
     * @param z The z position within this chunks kernel (MIN_KERNEL_Z ~ MAX_KERNEL_Z)
     * @return True if any changes occurred to this chunk.
     * */
    public boolean setVoxel(Voxel voxel, int x, int y, int z) {
        x = Mathf.clamp(x, 0, kernel.getWidth() - 1);
        y = Mathf.clamp(y, 0, kernel.getHeight() - 1);
        z = Mathf.clamp(z, 0, kernel.getDepth() - 1);

        // Mark this chunk as dirty if any changes occurred.
        if (kernel.getValue(x, y, z) != voxel)
            markDirty();

        kernel.setValue(voxel, x, y, z);

        return dirty;
    }

    /**
     * Returns true if the following positions plus the face offset are within the chunk kernel.
     *
     * @param x The x position within this chunks kernel (MIN_KERNEL_X ~ MAX_KERNEL_X)
     * @param y The y position within this chunks kernel (MIN_KERNEL_Y ~ MAX_KERNEL_Y)
     * @param z The z position within this chunks kernel (MIN_KERNEL_Z ~ MAX_KERNEL_Z)
     * @param face The face of the x, y, z position to check
     * */
    public boolean withinChunk(int x, int y, int z, VoxelFace face) {
        return withinChunk(x + face.xDirection, y + face.yDirection, z + face.zDirection);
    }

    /**
     * Returns true if the following positions are within the chunk kernel.
     *
     * @param x The x position within this chunks kernel (MIN_KERNEL_X ~ MAX_KERNEL_X)
     * @param y The y position within this chunks kernel (MIN_KERNEL_Y ~ MAX_KERNEL_Y)
     * @param z The z position within this chunks kernel (MIN_KERNEL_Z ~ MAX_KERNEL_Z)
     * */
    public boolean withinChunk(int x, int y, int z) {
        return kernel.withinBounds(x, y, z);
    }

    public int getLight(int x, int y, int z) {
        Integer value = lightmap.getValue(x, y, z);
        return value == null ? 0 : value;
    }

    public boolean setLight(int light, int x, int y, int z) {
        x = Mathf.clamp(x, 0, kernel.getWidth() - 1);
        y = Mathf.clamp(y, 0, kernel.getHeight() - 1);
        z = Mathf.clamp(z, 0, kernel.getDepth() - 1);

        // Mark this chunk as dirty if any changes occurred.
        if (getLight(x, y, z) != light)
            markDirty();

        lightmap.setValue(light, x, y, z);

        return dirty;
    }

    /**
     * @return The AABB which contains this chunk.
     * */
    public AABB getAABB() {
        return aabb;
    }

    /**
     * Makes this chunk 'dirty' so that its mesh will be rebuilt.
     * */
    public void markDirty() {
        dirty = true;
    }

    /**
     * Sets this chunks dirty value to false.
     */
    public void markClean() {
        dirty = false;
    }

    /**
     * @return True if this chunks mesh needs to be recomputed.
     * */
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void destroy() {
        kernel = null;
        lightmap = null;
    }

    public Grid3i<Voxel> getKernel() {
        return kernel;
    }

    public Grid3i<Integer> getLightmap() {
        return lightmap;
    }

    public int toFlatIndex(float x, float y, float z) {
        return toFlatIndex(Mathf.floor(x), Mathf.floor(y), Mathf.floor(z));
    }

    public int toFlatIndex(int x, int y, int z) {
        return kernel.toFlatIndex(ChunkPos.toKernelX(x), y, ChunkPos.toKernelZ(z));
    }

    public Vector3i fromFlatIndex(int flatIndex) {
        return kernel.fromFlatIndex(flatIndex);
    }
}
