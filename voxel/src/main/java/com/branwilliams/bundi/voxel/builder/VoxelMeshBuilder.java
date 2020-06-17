package com.branwilliams.bundi.voxel.builder;

import com.branwilliams.bundi.engine.mesh.Mesh;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public interface VoxelMeshBuilder {
    default Mesh buildVoxelMesh(Voxel voxel, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        return rebuildVoxelMesh(voxel, VoxelFace.values(), minX, maxX, minY, maxY, minZ, maxZ, new Mesh());
    }

    default Mesh rebuildVoxelMesh(Voxel voxel, float minX, float maxX, float minY, float maxY, float minZ, float maxZ,
                                  Mesh mesh) {
        return rebuildVoxelMesh(voxel, VoxelFace.values(), minX, maxX, minY, maxY, minZ, maxZ, mesh);
    }

    Mesh rebuildVoxelMesh(Voxel voxel, VoxelFace[] faces, float minX, float maxX, float minY, float maxY,
                               float minZ, float maxZ, Mesh mesh);
}
