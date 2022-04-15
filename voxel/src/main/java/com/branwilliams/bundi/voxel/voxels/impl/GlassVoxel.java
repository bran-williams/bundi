package com.branwilliams.bundi.voxel.voxels.impl;

import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelFace;
import com.branwilliams.bundi.voxel.voxels.VoxelIdentifiers;

/**
 * @author Brandon
 * @since August 13, 2019
 */
public class GlassVoxel extends Voxel {

    public GlassVoxel() {
        super(VoxelIdentifiers.GLASS, false);
    }

    public boolean shouldRenderFace(Voxel adjacentVoxel, VoxelFace face) {
        return !adjacentVoxel.id.equals(this.id) && super.shouldRenderFace(adjacentVoxel, face);
    }
}
