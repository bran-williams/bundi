package com.branwilliams.bundi.voxel.world.chunk;

import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.VoxelConstants;

import java.util.Objects;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class ChunkPos {

    private final int x, z;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    /**
     * @return The real x position of this chunk.
     * */
    public float getRealX() {
        return x * VoxelConstants.CHUNK_X_SIZE;
    }

    /**
     * @return The real z position of this chunk.
     * */
    public float getRealZ() {
        return z * VoxelConstants.CHUNK_Z_SIZE;
    }

    public static int toChunkX(float x) {
        return toChunkX(Mathf.floor(x));
    }

    public static int toChunkZ(float z) {
        return toChunkZ(Mathf.floor(z));
    }

    public static int toChunkX(int x) {
        return x >> VoxelConstants.CHUNK_SIZE_BITSHIFT;
    }


    public static int toChunkZ(int z) {
        return z >> VoxelConstants.CHUNK_SIZE_BITSHIFT;
    }

    public static int toKernelX(int x) {
        // Take the last 4 bits (15 decimal = 1111 binary)
        return x & (VoxelConstants.CHUNK_X_SIZE - 1);
    }

    public static int toKernelZ(int z) {
        // Take the last 4 bits (15 decimal = 1111 binary)
        return z & (VoxelConstants.CHUNK_Z_SIZE - 1);
    }

    public float distance(int chunkX, int chunkZ) {
        int dx = x - chunkX;
        int dz = z - chunkZ;
        return Mathf.sqrt(dx * dx + dz * dz);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return x == chunkPos.x &&
                z == chunkPos.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public String toString() {
        return "ChunkPos{" +
                "x=" + x +
                ", z=" + z +
                '}';
    }
}
