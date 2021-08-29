package com.branwilliams.bundi.voxel.render.mesh.builder;


import com.branwilliams.bundi.voxel.render.mesh.ChunkMesh;

import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;


/**
 * @author Brandon
 * @since July 21, 2019
 */
public interface VoxelChunkMeshBuilder {

    boolean rebuildChunkMesh(VoxelWorld voxelWorld, VoxelChunk voxelChunk, ChunkMesh mesh);

}
