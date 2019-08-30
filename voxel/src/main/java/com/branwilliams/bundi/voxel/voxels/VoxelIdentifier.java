package com.branwilliams.bundi.voxel.voxels;

/**
 * @author Brandon
 * @since August 04, 2019
 */
public interface VoxelIdentifier {

    String getId();

    default String id() {
        return getId();
    }

    default String normalized() {
        return getId().toLowerCase();
    }
}
