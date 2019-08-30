package com.branwilliams.bundi.voxel.world.storage;

import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.VoxelConstants;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.branwilliams.bundi.voxel.voxels.Voxels;
import com.branwilliams.bundi.voxel.world.chunk.ChunkPos;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;
import org.joml.Vector3f;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public interface ChunkStorage {

    /**
     * @return True if the voxel chunk is already loaded.
     * */
    boolean isLoaded(ChunkPos chunkPos);

    /**
     * @return The {@link VoxelChunk} at the given chunk position.
     * */
    VoxelChunk getChunk(ChunkPos chunkPos);

    /**
     * Loads the provided chunk into the chunk positions.
     * */
    void loadChunk(ChunkPos chunkPos, VoxelChunk voxelChunk);

    /**
     * Unloads the chunk at the chunk positions.
     * */
    void unloadChunk(ChunkPos chunkPos);

    /**
     * @return All loaded chunks within this chunk storage.
     * */
    Iterable<VoxelChunk> getLoadedChunks();

    /**
     * @return True if the voxel chunk is already loaded.
     * */
    default boolean isLoaded(int chunkX, int chunkZ) {
        return isLoaded(new ChunkPos(chunkX, chunkZ));
    }

    /**
     * @return The {@link VoxelChunk} at the given chunk position.
     * */
    default VoxelChunk getChunk(int chunkX, int chunkZ) {
        return getChunk(new ChunkPos(chunkX, chunkZ));
    }

    /**
     * Loads the provided chunk into the chunk positions.
     * */
    default void loadChunk(int chunkX, int chunkZ, VoxelChunk voxelChunk) {
        loadChunk(new ChunkPos(chunkX, chunkZ), voxelChunk);
    }

    /**
     * Unloads the chunk at the chunk positions.
     * */
    default void unloadChunk(int chunkX, int chunkZ) {
        unloadChunk(new ChunkPos(chunkX, chunkZ));
    }

    /**
     * @return The {@link VoxelChunk} located in the real position provided.
     * */
    default VoxelChunk getChunkAtPosition(Vector3f position) {
        return getChunkAtPosition(Mathf.floor(position.x), Mathf.floor(position.z));
    }

    /**
     * @return The {@link VoxelChunk} located in the real position provided.
     * */
    default VoxelChunk getChunkAtPosition(float x, float z) {
        return getChunkAtPosition(Mathf.floor(x), Mathf.floor(z));
    }

    /**
     * @return The {@link VoxelChunk} located in the real position provided.
     * */
    default VoxelChunk getChunkAtPosition(int x, int z) {
        // Divided by 16, converting the x and z positions to chunk positions.
        int chunkX = ChunkPos.toChunkX(x);
        int chunkZ = ChunkPos.toChunkZ(z);
        VoxelChunk chunk = getChunk(chunkX, chunkZ);
        return chunk;
    }

    /**
     * @return The {@link Voxel} at the given position plus the face offset.
     * */
    default Voxel getVoxelFacingPosition(Vector3f position, VoxelFace face) {
        return getVoxelAtPosition(position.x + face.xDirection, position.y + face.yDirection, position.z + face.zDirection);
    }

    /**
     * @return The {@link Voxel} at the given position plus the face offset.
     * */
    default Voxel getVoxelFacingPosition(float x, float y, float z, VoxelFace face) {
        return getVoxelAtPosition(x + face.xDirection, y + face.yDirection, z + face.zDirection);
    }

    /**
     * @return The {@link Voxel} at the given position plus the face offset.
     * */
    default Voxel getVoxelFacingPosition(int x, int y, int z, VoxelFace face) {
        return getVoxelAtPosition(x + face.xDirection, y + face.yDirection, z + face.zDirection);
    }

    /**
     * @return The {@link Voxel} at the given position.
     * */
    default Voxel getVoxelAtPosition(Vector3f position) {
        return getVoxelAtPosition(position.x, position.y, position.z);
    }

    /**
     * @return The {@link Voxel} at the given position.
     * */
    default Voxel getVoxelAtPosition(float x, float y, float z) {
        return getVoxelAtPosition(Mathf.floor(x), Mathf.floor(y), Mathf.floor(z));
    }

    /**
     * @return The {@link Voxel} at the given position.
     * */
    default Voxel getVoxelAtPosition(int x, int y, int z) {

        // Divided by 16, converting the x and z positions to chunk positions.
        VoxelChunk chunk = getChunk(ChunkPos.toChunkX(x), ChunkPos.toChunkZ(z));

        if (chunk != null) {
            // Take the last 4 bits (15 decimal = 1111 binary)
            return chunk.getVoxelAtPosition(ChunkPos.toKernelX(x), y, ChunkPos.toKernelX(z));
        } else {
            return Voxels.air;
        }
    }

    default void setVoxelAtPosition(Voxel voxel, Vector3f position) {
        setVoxelAtPosition(voxel, position.x, position.y, position.z);
    }

    default void setVoxelAtPosition(Voxel voxel, float x, float y, float z) {
        setVoxelAtPosition(voxel, Mathf.floor(x), Mathf.floor(y), Mathf.floor(z));
    }

    /**
     *
     * */
    default void setVoxelAtPosition(Voxel voxel, int x, int y, int z) {
        VoxelChunk chunk = getChunkAtPosition(x, z);

        if (chunk != null) {
            // Take the last 4 bits (15 decimal = 1111 binary)
            int kernelX = ChunkPos.toKernelX(x);
            int kernelZ = ChunkPos.toKernelZ(z);

            if (chunk.setVoxelAtPosition(voxel, kernelX, y, kernelZ)) {
                updateNeighborChunks(kernelX, kernelZ, chunk.chunkPos.getX(), chunk.chunkPos.getZ());
            }
        }
    }

    /**
     * Marks every neighbor around these chunk positions as dirty.
     * */
    default void updateAllNeighbors(int chunkX, int chunkZ) {
        markChunkDirty(chunkX - 1, chunkZ);
        markChunkDirty(chunkX + 1, chunkZ);
        markChunkDirty(chunkX, chunkZ - 1);
        markChunkDirty(chunkX, chunkZ + 1);

    }

    /**
     * Updates any neighbor chunks that may need to be marked dirty.
     * */
    default void updateNeighborChunks(int kernelX, int kernelZ, int chunkX, int chunkZ) {
        //
        if (kernelX == VoxelConstants.MIN_KERNEL_X) {
            markChunkDirty(chunkX - 1, chunkZ);
        }
        //
        if (kernelX == VoxelConstants.MAX_KERNEL_X) {
            markChunkDirty(chunkX + 1, chunkZ);
        }
        //
        if (kernelZ == VoxelConstants.MIN_KERNEL_Z) {
            markChunkDirty(chunkX, chunkZ - 1);
        }
        //
        if (kernelZ == VoxelConstants.MAX_KERNEL_Z) {
            markChunkDirty(chunkX, chunkZ + 1);
        }
    }

    /**
     * Marks a chunk dirty if it exists.
     * */
    default void markChunkDirty(int chunkX, int chunkZ) {
        VoxelChunk chunk = getChunk(chunkX, chunkZ);
        if (chunk != null)
            chunk.markDirty();
    }

}
