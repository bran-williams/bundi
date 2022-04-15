package com.branwilliams.bundi.voxel.inventory;

import com.branwilliams.bundi.voxel.world.VoxelWorld;
import org.joml.Vector3f;

public interface Item {

    boolean isPlaceable();

    boolean place(VoxelWorld world, Vector3f blockPosition);

}
