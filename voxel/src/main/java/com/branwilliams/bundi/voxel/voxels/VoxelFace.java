package com.branwilliams.bundi.voxel.voxels;

import com.branwilliams.bundi.voxel.builder.ChunkMeshVertex;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

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

    public static void createPositions(VoxelFace face,
                                 ChunkMeshVertex v0, ChunkMeshVertex v1, ChunkMeshVertex v2, ChunkMeshVertex v3,
                                 float minX, float minY, float minZ,
                                 float maxX, float maxY, float maxZ) {
        switch (face) {
            case FRONT:
                v0.vertex = new Vector3f(minX, minY, maxZ);
                v1.vertex = new Vector3f(maxX, minY, maxZ);
                v2.vertex = new Vector3f(maxX, maxY, maxZ);
                v3.vertex = new Vector3f(minX, maxY, maxZ);
                break;
            case BACK:
                v0.vertex = new Vector3f(maxX, minY, minZ);
                v1.vertex = new Vector3f(minX, minY, minZ);
                v2.vertex = new Vector3f(minX, maxY, minZ);
                v3.vertex = new Vector3f(maxX, maxY, minZ);
                break;
            case TOP:
                v0.vertex = new Vector3f(minX, maxY, maxZ);
                v1.vertex = new Vector3f(maxX, maxY, maxZ);
                v2.vertex = new Vector3f(maxX, maxY, minZ);
                v3.vertex = new Vector3f(minX, maxY, minZ);
                break;
            case BOTTOM:
                v0.vertex = new Vector3f(minX, minY, minZ);
                v1.vertex = new Vector3f(maxX, minY, minZ);
                v2.vertex = new Vector3f(maxX, minY, maxZ);
                v3.vertex = new Vector3f(minX, minY, maxZ);
                break;
            case RIGHT:
                v0.vertex = new Vector3f(maxX, minY, maxZ);
                v1.vertex = new Vector3f(maxX, minY, minZ);
                v2.vertex = new Vector3f(maxX, maxY, minZ);
                v3.vertex = new Vector3f(maxX, maxY, maxZ);
                break;
            case LEFT:
                v0.vertex = new Vector3f(minX, minY, minZ);
                v1.vertex = new Vector3f(minX, minY, maxZ);
                v2.vertex = new Vector3f(minX, maxY, maxZ);
                v3.vertex = new Vector3f(minX, maxY, minZ);
                break;
        }
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

    /**
     * Creates the normal vectors for the face provided.
     * */
    public static void createNormals(VoxelFace face,
                                     ChunkMeshVertex v0, ChunkMeshVertex v1, ChunkMeshVertex v2, ChunkMeshVertex v3) {
        Vector3f normal = new Vector3f((float) face.xDirection, (float) face.yDirection, (float) face.zDirection);
        v0.normal = new Vector3f(normal);
        v1.normal = new Vector3f(normal);
        v2.normal = new Vector3f(normal);
        v3.normal = new Vector3f(normal);
    }

    /**
     * Creates the normal vectors for the face provided.
     * */
    public static List<Float> normals(VoxelFace face) {
        return Arrays.asList(
                (float) face.xDirection, (float) face.yDirection, (float) face.zDirection,
                (float) face.xDirection, (float) face.yDirection, (float) face.zDirection,
                (float) face.xDirection, (float) face.yDirection, (float) face.zDirection,
                (float) face.xDirection, (float) face.yDirection, (float) face.zDirection
        );
    }

    /**
     * Creates an array of texture coordinates needed for a single face. The expected values of the provided Vector4f
     * are:
     *
     * <pre>
     * uv.x -> u
     * uv.y -> v
     * uv.z -> s
     * uv.w -> t
     * </pre>
     * */
    public static void createFaceUVs(Vector4f uv,
                                     ChunkMeshVertex v0, ChunkMeshVertex v1, ChunkMeshVertex v2, ChunkMeshVertex v3) {
        v0.uv = new Vector2f(uv.x, uv.w);
        v1.uv = new Vector2f(uv.z, uv.w);
        v2.uv = new Vector2f(uv.z, uv.y);
        v3.uv = new Vector2f(uv.x, uv.y);
    }

    /**
     * Creates an array of texture coordinates needed for a single face. The expected values of the provided Vector4f
     * are:
     *
     * <pre>
     * uv.x -> u
     * uv.y -> v
     * uv.z -> s
     * uv.w -> t
     * </pre>
     * */
    public static List<Float> createFaceUVs(Vector4f uv) {
        return Arrays.asList(
                uv.x, uv.w,
                uv.z, uv.w,
                uv.z, uv.y,
                uv.x, uv.y
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