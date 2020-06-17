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
    SAND,
    DIAMOND_BLOCK,
    GOLD_BLOCK,
    IRON_BLOCK,
    GLASS,

    OAK_PLANKS,
    OAK_LOG,
    OAK_LEAVES;

    @Override
    public String getId() {
        return toString().toLowerCase();
    }

}
