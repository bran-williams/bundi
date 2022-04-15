package com.branwilliams.bundi.voxel.world.storage;

import com.branwilliams.bundi.voxel.world.chunk.ChunkPos;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Brandon
 * @since August 19, 2019
 */
public class HashChunkStorage implements ChunkStorage {

    private Map<ChunkPos, VoxelChunk> chunks;

    public HashChunkStorage() {
        this.chunks = new HashMap<>();
    }

    @Override
    public boolean isLoaded(ChunkPos chunkPos) {
        return chunks.containsKey(chunkPos);
    }

    @Override
    public VoxelChunk getChunk(ChunkPos chunkPos) {
        return chunks.get(chunkPos);
    }

    @Override
    public void loadChunk(ChunkPos chunkPos, VoxelChunk voxelChunk) {
        chunks.put(chunkPos, voxelChunk);
        markAllNeighborChunksDirty(chunkPos.getX(), chunkPos.getZ());
    }

    @Override
    public void unloadChunk(ChunkPos chunkPos) {
        chunks.remove(chunkPos);
        markAllNeighborChunksDirty(chunkPos.getX(), chunkPos.getZ());
    }

    @Override
    public Iterable<VoxelChunk> getLoadedChunks() {
        return chunks.values();
    }

}
