package com.branwilliams.bundi.voxel.voxels;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Brandon
 * @since July 21, 2019
 */
public enum VoxelFace {
    FRONT(0, 0, 1),
    BACK(0, 0, -1),
    TOP(0, 1, 0),
    BOTTOM(0, -1, 0),
    RIGHT(1, 0, 0),
    LEFT(-1, 0, 0);

    public final int xDirection, yDirection, zDirection;

    VoxelFace(int xDirection, int yDirection, int zDirection) {
        this.xDirection = xDirection;
        this.yDirection = yDirection;
        this.zDirection = zDirection;
    }

    public static List<Float> positions(VoxelFace face,
                                        float minX, float minY, float minZ,
                                        float maxX, float maxY, float maxZ) {
        switch (face) {
            case FRONT:
                return Arrays.asList(
                        minX, minY, maxZ,
                        maxX, minY, maxZ,
                        maxX, maxY, maxZ,
                        minX, maxY, maxZ);
            case BACK:
                return Arrays.asList(
                        maxX, minY, minZ,
                        minX, minY, minZ,
                        minX, maxY, minZ,
                        maxX, maxY, minZ);
            case TOP:
                return Arrays.asList(
                        minX, maxY, maxZ,
                        maxX, maxY, maxZ,
                        maxX, maxY, minZ,
                        minX, maxY, minZ);

            case BOTTOM:
                return Arrays.asList(
                        minX, minY, minZ,
                        maxX, minY, minZ,
                        maxX, minY, maxZ,
                        minX, minY, maxZ);
            case RIGHT:
                return Arrays.asList(
                        maxX, minY, maxZ,
                        maxX, minY, minZ,
                        maxX, maxY, minZ,
                        maxX, maxY, maxZ);
            case LEFT:
                return Arrays.asList(
                        minX, minY, minZ,
                        minX, minY, maxZ,
                        minX, maxY, maxZ,
                        minX, maxY, minZ);
            default:
                return Collections.emptyList();
        }
    }

    public static List<Float> normals(VoxelFace face) {
        return Arrays.asList(
                (float) face.xDirection, (float) face.yDirection, (float) face.zDirection,
                (float) face.xDirection, (float) face.yDirection, (float) face.zDirection,
                (float) face.xDirection, (float) face.yDirection, (float) face.zDirection,
                (float) face.xDirection, (float) face.yDirection, (float) face.zDirection
        );
    }

    /***
     * @return The VoxelFace whose name corresponds to the name provided or null.
     */
    public static VoxelFace fromName(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}