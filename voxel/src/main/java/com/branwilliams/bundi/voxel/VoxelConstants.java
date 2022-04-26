package com.branwilliams.bundi.voxel;

/**
 * @author Brandon
 * @since August 13, 2019
 */
public class VoxelConstants {

    /** This is the size of each full-sized voxel. */
    public static final float CUBE_SIZE = 1F;

    /** This is hardcoded for the generation of texture atlases. */
    public static final int MAX_TEXTURE_SIZE = 2048;

    /** Number of voxels a chunk will have in the x axis */
    public static final int CHUNK_X_SIZE = 16;

    /** Number of voxels a chunk will have in the y axis */
    public static final int CHUNK_Y_SIZE = 128;

    /** Number of voxels a chunk will have in the z axis */
    public static final int CHUNK_Z_SIZE = 16;

    /** Bit shifting right by this amount will convert a real xz into chunk xz. */
    public static final int CHUNK_SIZE_BITSHIFT = 4;

    /** Minimum x value within a chunk kernel */
    public static final int MIN_KERNEL_X = 0;

    /** Maximum x value within kernel */
    public static final int MAX_KERNEL_X = 15;

    /** Minimum z value within kernel */
    public static final int MIN_KERNEL_Z = 0;

    /** Maximum z value within kernel */
    public static final int MAX_KERNEL_Z = 15;

    public static final long CHUNK_ANIMATION_TIME_MS = 250L;

    public static final int ZERO_LIGHT = 0;

    public static final int MIN_LIGHT = ZERO_LIGHT;

    public static final int MAX_LIGHT = 15;

    public static final int DEFAULT_LIGHT = 3; // for game settings

    public static final int MAX_LIGHT_RED = MAX_LIGHT;

    public static final int MAX_LIGHT_GREEN = MAX_LIGHT;

    public static final int MAX_LIGHT_BLUE = MAX_LIGHT;

    public static final float MAX_AO = 3F;

    public static final float MIN_AO = 0F;

}
