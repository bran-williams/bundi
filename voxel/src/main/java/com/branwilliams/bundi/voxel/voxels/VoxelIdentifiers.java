package com.branwilliams.bundi.voxel.voxels;

/**
 * @author Brandon
 * @since August 04, 2019
 */
public enum VoxelIdentifiers implements VoxelIdentifier {
    AIR,
    GRASS,
    DIRT,
    STONE,
    BEDROCK,
    BRICKS,
    COBBLESTONE,
    MOSSY_COBBLESTONE,
    OAK_PLANKS,
    SAND,
    DIAMOND_BLOCK,
    GOLD_BLOCK,
    IRON_BLOCK,
    GLASS,
    OAK_LEAVES;

    @Override
    public String getId() {
        return toString().toLowerCase();
    }

}
