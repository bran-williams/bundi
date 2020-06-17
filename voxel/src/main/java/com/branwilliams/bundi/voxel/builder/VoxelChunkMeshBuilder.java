package com.branwilliams.bundi.voxel.builder;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.engine.shader.dynamic.VertexElements;
import com.branwilliams.bundi.engine.shader.dynamic.VertexFormat;
import com.branwilliams.bundi.engine.util.Mathf;
import com.branwilliams.bundi.voxel.VoxelConstants;
import com.branwilliams.bundi.voxel.io.VoxelTexturePack;
import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.render.mesh.ChunkMesh;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import com.branwilliams.bundi.voxel.world.chunk.VoxelChunk;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.branwilliams.bundi.engine.util.MeshUtils.*;
import static com.branwilliams.bundi.voxel.VoxelConstants.CUBE_SIZE;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public interface VoxelChunkMeshBuilder {

    boolean rebuildChunkMesh(VoxelWorld voxelWorld, VoxelChunk voxelChunk, ChunkMesh mesh);

}
