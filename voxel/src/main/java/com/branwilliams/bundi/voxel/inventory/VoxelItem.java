package com.branwilliams.bundi.voxel.inventory;

import com.branwilliams.bundi.voxel.math.AABB;
import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.world.VoxelWorld;
import org.joml.Vector3f;

public class VoxelItem implements Item {

    private final Voxel voxel;

    public VoxelItem(Voxel voxel) {
        this.voxel = voxel;
    }

    @Override
    public boolean isPlaceable() {
        return true;
    }

    @Override
    public boolean place(VoxelWorld world, Vector3f blockPosition) {
        AABB voxelAABB = voxel.getBoundingBox(blockPosition);

        if (world.canVoxelBePlaced(blockPosition,voxelAABB)) {
            world.getChunks().setVoxelAtPosition(voxel, blockPosition);
            return true;
        }

        return false;
    }

    public Voxel getVoxel() {
        return voxel;
    }
}
