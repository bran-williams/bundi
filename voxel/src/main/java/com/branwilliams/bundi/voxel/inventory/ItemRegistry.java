package com.branwilliams.bundi.voxel.inventory;

import com.branwilliams.bundi.voxel.voxels.Voxel;
import com.branwilliams.bundi.voxel.voxels.VoxelRegistry;

public class ItemRegistry extends Inventory {

    public ItemRegistry(VoxelRegistry voxelRegistry) {
        // Create voxel items for every voxel in registry.
        for (String voxelIdentifier : voxelRegistry.getSortedVoxelIdentifiers()) {
            Voxel voxel = voxelRegistry.getVoxel(voxelIdentifier);
            VoxelItem voxelItem = new VoxelItem(voxel);
            addItem(voxelItem);
        }
    }
}
