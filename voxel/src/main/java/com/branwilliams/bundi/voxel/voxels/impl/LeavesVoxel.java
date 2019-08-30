package com.branwilliams.bundi.voxel.voxels.impl;

import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.branwilliams.bundi.voxel.voxels.VoxelIdentifiers;

/**
 * @author Brandon
 * @since August 13, 2019
 */
public class LeavesVoxel extends Voxel {

    public LeavesVoxel() {
        super(VoxelIdentifiers.OAK_LEAVES, true);
    }

    public boolean shouldRenderFace(Voxel adjacentVoxel, VoxelFace face) {
        return super.shouldRenderFace(adjacentVoxel, face);
    }
}
