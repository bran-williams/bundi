package com.branwilliams.bundi.voxel.world.chunk;

import com.branwilliams.bundi.engine.core.Destructible;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.VoxelConstants;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;

/**
 * Represents a chunk of a VoxelWorld
 *
 * @author Brandon
 * @since July 21, 2019
 */
public class VoxelChunk implements Destructible {

    public final ChunkPos chunkPos;

    private Voxel[][][] kernel;

    private boolean dirty = true;

    private AABB aabb;

    public VoxelChunk(ChunkPos chunkPos, Voxel[][][] kernel) {
        this.chunkPos = chunkPos;
        this.kernel = kernel;
        this.aabb = new AABB(chunkPos.getRealX(), chunkPos.getRealX() + VoxelConstants.CHUNK_X_SIZE,
                0F, VoxelConstants.CHUNK_Y_SIZE,
                chunkPos.getRealZ(), chunkPos.getRealZ() + VoxelConstants.CHUNK_Z_SIZE);
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
        return getVoxelAtPosition(x + face.xDirection, y + face.yDirection, z + face.zDirection);
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
    public Voxel getVoxelAtPosition(int x, int y, int z) {
        x = Mathf.clamp(x, kernel.length - 1,       0);
        y = Mathf.clamp(y, kernel[0].length - 1,    0);
        z = Mathf.clamp(z, kernel[0][0].length - 1, 0);

        return kernel[x][y][z];
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
    public boolean setVoxelAtPosition(Voxel voxel, int x, int y, int z) {
        x = Mathf.clamp(x, kernel.length - 1,       0);
        y = Mathf.clamp(y, kernel[0].length - 1,    0);
        z = Mathf.clamp(z, kernel[0][0].length - 1, 0);

        // Mark this chunk as dirty if any changes occurred.
        if (kernel[x][y][z] != voxel)
            markDirty();

        kernel[x][y][z] = voxel;

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
        return     x >= 0 && x < kernel.length
                && y >= 0 && y < kernel[0].length
                && z >= 0 && z < kernel[0][0].length;
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
    }
}
