package com.branwilliams.bundi.voxel.voxels;

import com.branwilliams.bundi.voxel.voxels.impl.GlassVoxel;
import com.branwilliams.bundi.voxel.voxels.impl.LeavesVoxel;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public final class Voxels {

    public static final Voxel air            = new Voxel(VoxelIdentifiers.AIR, false);

    public static final Voxel grass          = new Voxel(VoxelIdentifiers.GRASS);

    public static final Voxel dirt           = new Voxel(VoxelIdentifiers.DIRT);

    public static final Voxel sand           = new Voxel(VoxelIdentifiers.SAND);

    public static final Voxel stone          = new Voxel(VoxelIdentifiers.STONE);

    public static final Voxel bricks         = new Voxel(VoxelIdentifiers.BRICKS);

    public static final Voxel bedrock        = new Voxel(VoxelIdentifiers.BEDROCK);

    public static final Voxel diamond_block  = new Voxel(VoxelIdentifiers.DIAMOND_BLOCK);

    public static final Voxel gold_block     = new Voxel(VoxelIdentifiers.GOLD_BLOCK);

    public static final Voxel iron_block     = new Voxel(VoxelIdentifiers.IRON_BLOCK);

    public static final Voxel glass          = new GlassVoxel();

    public static final Voxel oak_planks     = new Voxel(VoxelIdentifiers.OAK_PLANKS);

    public static final Voxel oak_log        = new Voxel(VoxelIdentifiers.OAK_LOG);

    public static final Voxel oak_leaves     = new LeavesVoxel();

    /***
     * Updates a mapping of voxel-identifiers to the voxel objects defined in this class. The voxels defined in this
     * class are special cases of the normal voxels.
     */
    public static void initializeVoxels(Map<String, Voxel> voxels) {
        for (Field field : Voxels.class.getDeclaredFields()) {
            if (Voxel.class.isAssignableFrom(field.getType())) {
                try {
                    Voxel voxel = (Voxel) field.get(null);
                    voxels.put(voxel.id.normalized(), voxel);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
