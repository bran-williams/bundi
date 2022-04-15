package com.branwilliams.bundi.voxel.voxels.builder;

import com.branwilliams.bundi.voxel.voxels.model.VoxelProperties;
import com.branwilliams.bundi.voxel.voxels.Voxel;

/**
 * @author Brandon
 * @since August 11, 2019
 */
public class VoxelBuilder {

    public Voxel buildVoxel(String voxelIdentifier, VoxelProperties voxelProperties) {
        return new Voxel(() -> voxelIdentifier, voxelProperties.isOpaque(), voxelProperties.getLight());
    }
}
