package com.branwilliams.bundi.voxel;

import com.branwilliams.bundi.voxel.render.mesh.builder.VoxelChunkMeshBuilder;
import com.branwilliams.bundi.voxel.render.mesh.ChunkMesh;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;

public class VoxelChunkLoadTask implements Runnable {

    private final VoxelChunkMeshBuilder voxelChunkMeshBuilder;

    private final VoxelChunk voxelChunk;

    private final VoxelWorld voxelWorld;

    private final ChunkMesh chunkMesh;

    public VoxelChunkLoadTask(VoxelChunkMeshBuilder voxelChunkMeshBuilder,
                              VoxelWorld voxelWorld, VoxelChunk voxelChunk, ChunkMesh chunkMesh) {
        this.voxelChunkMeshBuilder = voxelChunkMeshBuilder;
        this.voxelWorld = voxelWorld;
        this.voxelChunk = voxelChunk;
        this.chunkMesh = chunkMesh;
    }

    @Override
    public void run() {
        boolean rebuilt = voxelChunkMeshBuilder.rebuildChunkMesh(voxelWorld, voxelChunk, chunkMesh);
        if (rebuilt) {

//            queueChunkMesh();
        }
    }
}
