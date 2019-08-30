package com.branwilliams.bundi.voxel.world.generator;

import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public interface VoxelChunkGenerator {

    /**
     * @return A {@link VoxelChunk} at the provided chunk positions.
     * */
    VoxelChunk generateChunk(VoxelRegistry voxelRegistry, int chunkX, int chunkZ);
}
