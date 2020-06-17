package com.branwilliams.bundi.voxel.builder;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents a single vertex within the mesh of a voxel chunk.
 * */
public class ChunkMeshVertex {

    public Vector3f vertex;
    public Vector2f uv;
    public Vector3f normal;
    public Vector3f tangent;

    public ChunkMeshVertex() {
        this.vertex = new Vector3f();
        this.uv = new Vector2f();
        this.normal = new Vector3f();
        this.tangent = new Vector3f();
    }
}
